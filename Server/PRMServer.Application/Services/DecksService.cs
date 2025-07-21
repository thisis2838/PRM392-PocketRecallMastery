using System.Text.RegularExpressions;
using AutoMapper;
using AutoMapper.QueryableExtensions;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using PRMServer.Application.DTOs.Decks;
using PRMServer.Application.Services.Contracts;
using PRMServer.Application.Utilities.Helpers;
using PRMServer.Data.Context;
using PRMServer.Data.Models;

namespace PRMServer.Application.Services
{
    public class DecksService : IDecksService
    {
        private readonly PRMContext _context;
        private readonly IMapper _mapper;

        public DecksService(PRMContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<int> CreateDeck(int userId, DeckCreationDTO creation)
        {
            var deck = new Deck
            {
                Name = creation.Name,
                Description = creation.Description,
                CreatedAt = DateOnly.FromDateTime(DateTime.Now),
                CreatorId = userId,
                IsPublic = false,
                Version = 1
            };
            await _context.Decks.AddAsync(deck);
            await _context.SaveChangesAsync();
            return deck.Id;
        }

        public async Task EditDeck(int id, int userId, DeckEditDTO creation)
        {
            var entity = await _context.Decks
                .Include(x => x.Cards)
                .SingleOrDefaultAsync(x => x.Id == id);
            if (entity is null)
                throw new KeyNotFoundException();
            if (entity.CreatorId != userId)
                throw new UnauthorizedAccessException();
            if (creation.Cards.GroupBy(x => x.Index).Any(x => x.Count() > 1))
                throw new ArgumentException();

            entity.UpdatedAt = DateOnly.FromDateTime(DateTime.Now);
            entity.IsPublic = creation.IsPublic;
            entity.Name = creation.Name;
            entity.Description = creation.Description;
            entity.Cards = creation.Cards
                .Select(x => new Card
                {
                    Back = x.Back,
                    Front = x.Front,
                    DeckId = id,
                    Index = x.Index,
                })
                .ToList();
            entity.Version++;
            await _context.SaveChangesAsync();
        }

        public async Task DeleteDeck(int deckId, int userId)
        {
            var entity = await _context.Decks
                .Include(x => x.Cards)
                .SingleOrDefaultAsync(x => x.Id == deckId);
            if (entity is null)
                throw new KeyNotFoundException();
            if (entity.CreatorId != userId)
                throw new UnauthorizedAccessException();

            entity.Cards.Clear();
            _context.Decks.Remove(entity);
            await _context.SaveChangesAsync();
        }

        public async Task<DeckDetailDTO?> GetDeck(int id, int? userId)
        {
            var pre = await _context.Decks.FindAsync(id);
            if (pre is null)
                return null;
            if ((!userId.HasValue || pre.CreatorId != userId) && !pre.IsPublic)
                throw new UnauthorizedAccessException();

            await _context.Decks.Where(x => x.Id == id).ExecuteUpdateAsync
            (setter =>
                setter
                    .SetProperty(y => y.ViewsTotal, y => y.ViewsTotal + 1)
                    .SetProperty(y => y.ViewsWeekly, y => y.ViewsWeekly + 1)
            );
            return await _context.Decks
                .ProjectTo<DeckDetailDTO>(_mapper.ConfigurationProvider)
                .SingleOrDefaultAsync(d => d.Id == id);
        }

        public async Task<DeckListDTO> GetPublicDecks(DeckListArgumentsDTO arguments)
        {
            var query = _context.Decks.Where(d => d.IsPublic);
            return await GetList(query, arguments);
        }

        public async Task<DeckListDTO> GetUserDecks(int userId, DeckListArgumentsDTO arguments, bool onlyPublic)
        {
            var query = _context.Decks.Where(d => d.CreatorId == userId);
            if (onlyPublic)
                query = query.Where(x => x.IsPublic);
            return await GetList(query, arguments);
        }

        private async Task<DeckListDTO> GetList(IQueryable<Deck> decks, DeckListArgumentsDTO arguments)
        {
            if (!string.IsNullOrWhiteSpace(arguments.Search))
            {
               decks = decks.Where(x => x.Name.Contains(arguments.Search));
            }

            if (arguments.MinCardCount != null)
                decks = decks.Where(x => x.Cards.Count >= arguments.MinCardCount);
            if (arguments.MaxCardCount != null)
                decks = decks.Where(x => x.Cards.Count <= arguments.MaxCardCount);

            switch (arguments.SortingMetric)
            {
                case DeckListMetric.Name:
                    decks = decks.OrderBy(x => x.Name.ToLower()); break;
                case DeckListMetric.View:
                    decks = decks.OrderBy(x => x.ViewsTotal); break;
                case DeckListMetric.Download:
                    decks = decks.OrderBy(x => x.DownloadsTotal); break;
            }
            if (!arguments.SortingAscending)
                decks = decks.Reverse();

            return new DeckListDTO
            {
                TotalCount = await decks.CountAsync(),
                Decks = await decks
                    .ProjectTo<DeckSummaryDTO>(_mapper.ConfigurationProvider)
                    //.Take(50) // Limit to 100 for performance; adjust as needed
                               //.ApplyPagination(arguments.PageIndex, arguments.PageSize)
                    .ToListAsync()
            };
        }
    }
}
