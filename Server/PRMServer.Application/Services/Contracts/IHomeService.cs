using PRMServer.Application.DTOs.Decks;

namespace PRMServer.Application.Services.Contracts
{
    public interface IHomeService
    {
        public Task<IList<DeckSummaryDTO>> WeeklyPopularDecks();
        public Task<IList<DeckSummaryDTO>> GetRecentDecks();
    }
}
