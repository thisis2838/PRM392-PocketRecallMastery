using System.ComponentModel.DataAnnotations;
using PRMServer.Data.Models;

namespace PRMServer.Application.DTOs.Decks
{
    public class DeckCreationDTO
    {
        [MaxLength(Deck.NAME_MAX_LENGTH)]
        public required string Name { get; set; }

        [MaxLength(Deck.NAME_MAX_LENGTH)]
        public required string Description { get; set; }
    }
}
