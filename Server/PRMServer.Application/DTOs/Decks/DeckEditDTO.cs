using System.ComponentModel.DataAnnotations;
using PRMServer.Application.DTOs.Cards;
using PRMServer.Data.Models;

namespace PRMServer.Application.DTOs.Decks
{
    public class DeckEditDTO
    {
        [MaxLength(Deck.NAME_MAX_LENGTH)]
        public required string Name { get; set; }

        [MaxLength(Deck.NAME_MAX_LENGTH)]
        public required string Description { get; set; }

        public required bool IsPublic { get; set; }

        public required IList<CardDetailDTO> Cards { get; set; }
    }
}
