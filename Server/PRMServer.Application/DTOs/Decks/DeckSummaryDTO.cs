using PRMServer.Application.DTOs.Users;

namespace PRMServer.Application.DTOs.Decks
{
    public record DeckSummaryDTO
    {
        public required int Id { get; set; }
        public required string Name { get; set; }
        public required string Description { get; set; }
        public required bool IsPublic { get; set; }
        public required int Version { get; set; }
        public required int ViewsTotal { get; set; }
        public required int ViewsWeekly { get; set; }
        public required int DownloadsTotal { get; set; }
        public required int DownloadsWeekly { get; set; }
        public required DateTime CreatedAt { get; set; }
        public required int CardsCount { get; set; }
        public required UserSummaryDTO Creator { get; set; }
    }
}
