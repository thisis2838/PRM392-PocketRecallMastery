using System.Text.RegularExpressions;
using AutoMapper;
using AutoMapper.QueryableExtensions;
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

        public Task<DeckDetailDTO?> GetDeck(int id)
        {
            return _context.Decks
                .ProjectTo<DeckDetailDTO>(_mapper.ConfigurationProvider)
                .SingleOrDefaultAsync(d => d.Id == id);
        }

        public async Task<DeckListDTO> GetPublicDecks(DeckListArgumentsDTO arguments)
        {
            var query = _context.Decks.Where(d => d.IsPublic);
            return await GetList(query, arguments);
        }

        public async Task<DeckListDTO> GetUserPublicDecks(int userId, DeckListArgumentsDTO arguments)
        {
            var query = _context.Decks.Where(d => d.CreatorId == userId && d.IsPublic);
            return await GetList(query, arguments);
        }

        public async Task<DeckListDTO> GetUserDecks(int userId, DeckListArgumentsDTO arguments)
        {
            var query = _context.Decks.Where(d => d.CreatorId == userId);
            return await GetList(query, arguments);
        }

        private async Task<DeckListDTO> GetList(IQueryable<Deck> decks, DeckListArgumentsDTO arguments)
        {
            if (arguments.Search != null)
            {
                var regex = arguments.Search;
                regex = Regex.Replace(regex, " +", ".*");
                decks = decks.Where(x => Regex.IsMatch(x.Name, regex, RegexOptions.IgnoreCase));
            }

            if (arguments.MinCardCount != null)
                decks = decks.Where(x => x.Cards.Count >= arguments.MinCardCount);
            if (arguments.MaxCardCount != null)
                decks = decks.Where(x => x.Cards.Count <= arguments.MaxCardCount);

            switch (arguments.SortingMetric)
            {
                case DeckListMetric.Name:
                    decks = decks.OrderBy(x => x.Name.ToLower()); break;
                case DeckListMetric.PopularityWeekly:
                    decks = decks.OrderBy(x => x.ViewsWeekly + x.DownloadsWeekly); break;
                case DeckListMetric.PopularityTotal:
                    decks = decks.OrderBy(x => x.ViewsTotal + x.DownloadsTotal); break;
            }
            if (!arguments.SortingAscending)
                decks = decks.Reverse();

            return new DeckListDTO
            {
                TotalCount = await decks.CountAsync(),
                Decks = await decks
                    .ProjectTo<DeckSummaryDTO>(_mapper.ConfigurationProvider)
                    .ApplyPagination(arguments.PageIndex, arguments.PageSize)
                    .ToListAsync()
            };
        }
    }
}
