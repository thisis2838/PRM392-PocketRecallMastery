using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PRMServer.Data.Models
{
    public record Deck
    {
        public Guid Id { get; set; }

        [MaxLength(NAME_MAX_LENGTH)]
        public required string Name { get; set; }

        [MaxLength(DESCRIPTION_MAX_LENGTH)]
        public required string Description { get; set; }

        public required bool IsPublic { get; set; } 

        public int Version { get; set; } = 1;

        public int Views { get; set; } = 0;

        public int Downloads { get; set; } = 0;

        public required DateTime CreatedAt { get; set; }

        public ICollection<Card> Cards { get; set; } = null!;


        public const int NAME_MAX_LENGTH = 100;
        public const int DESCRIPTION_MAX_LENGTH = 300;
    }
}