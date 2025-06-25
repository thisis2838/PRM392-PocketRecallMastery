namespace PRMServer.Application.DTOs.Decks
{
    public record DeckListDTO
    {
        public required int TotalCount { get; set; }
        public required IList<DeckSummaryDTO> Decks { get; set; } = null!;
    }
}
