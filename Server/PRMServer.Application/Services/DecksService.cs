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

        public async Task<DeckDetailDTO?> GetDeck(int id)
        {
            await _context.Decks.Where(x => x.Id == id).ExecuteUpdateAsync
            (x =>
                x
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
                    .Take(50) // Limit to 100 for performance; adjust as needed
                               //.ApplyPagination(arguments.PageIndex, arguments.PageSize)
                    .ToListAsync()
            };
        }
    }
}
