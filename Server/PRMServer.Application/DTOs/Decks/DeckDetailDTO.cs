using PRMServer.Application.DTOs.Cards;

namespace PRMServer.Application.DTOs.Decks
{
    public record DeckDetailDTO : DeckSummaryDTO
    {
        public required IList<CardDetailDTO> Cards { get; set; }
    }
}
