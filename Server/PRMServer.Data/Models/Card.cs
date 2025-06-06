using System.ComponentModel.DataAnnotations;

namespace PRMServer.Data.Models
{
    public record Card
    {
        public Guid Id { get; set; }

        [MaxLength(FRONT_MAX_LENGTH)]
        public required string Front { get; set; }

        [MaxLength(BACK_MAX_LENGTH)]
        public required string Back { get; set; }   

        public required int Index { get; set; }

        public required Guid DeckId { get; set; }
        public virtual Deck Deck { get; set; } = null!;


        public const int FRONT_MAX_LENGTH = 299;
        public const int BACK_MAX_LENGTH = 300;
    }
}
