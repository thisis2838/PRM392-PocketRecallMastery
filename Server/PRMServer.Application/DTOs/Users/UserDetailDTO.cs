using PRMServer.Application.DTOs.Decks;

namespace PRMServer.Application.DTOs.Users
{
    public record UserDetailDTO : UserSummaryDTO
    {
        public required IList<DeckSummaryDTO> PopularDecks { get; set; } = null!;
        public required int DecksViewsTotal { get; set; }
        public required int DecksViewsWeekly { get; set; }
        public required int DecksDownloadsTotal { get; set; }
        public required int DecksDownloadsWeekly { get; set; }

        public const int MAX_POPULAR_DECKS = 10;
    }
}
