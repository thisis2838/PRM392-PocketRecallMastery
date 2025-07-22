using AutoMapper;
using AutoMapper.QueryableExtensions;
using Microsoft.EntityFrameworkCore;
using PRMServer.Application.DTOs.Decks;
using PRMServer.Application.Services.Contracts;
using PRMServer.Data.Context;

namespace PRMServer.Application.Services
{
    public class HomeService : IHomeService
    {
        private readonly PRMContext _context;
        private readonly IMapper _mapper;

        public HomeService(PRMContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<IList<DeckSummaryDTO>> GetRecentDecks()
        {
            return await _context.Decks
                .OrderByDescending(x => x.CreatedAt)
                .Where(x => x.IsPublic)
                .Take(10)
                .ProjectTo<DeckSummaryDTO>(_mapper.ConfigurationProvider)
                .ToListAsync();
        }

        public async Task<IList<DeckSummaryDTO>> WeeklyPopularDecks()
        {
            return await _context.Decks
                .OrderByDescending(x => x.ViewsWeekly + x.DownloadsWeekly)
                .Where(x => x.IsPublic)
                .Take(10)
                .ProjectTo<DeckSummaryDTO>(_mapper.ConfigurationProvider)
                .ToListAsync();
        }
    }
}
