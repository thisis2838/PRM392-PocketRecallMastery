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
        public int Id { get; set; }

        [MaxLength(NAME_MAX_LENGTH)]
        public required string Name { get; set; }

        [MaxLength(DESCRIPTION_MAX_LENGTH)]
        public required string Description { get; set; }

        public required bool IsPublic { get; set; } 

        public int Version { get; set; } = 1;

        public int ViewsTotal { get; set; } = 0;
        public int ViewsWeekly { get; set; } = 0;

        public int DownloadsTotal { get; set; } = 0;
        public int DownloadsWeekly { get; set; } = 0;

        public required DateOnly CreatedAt { get; set; }
        public DateOnly? UpdatedAt { get; set; }

        public virtual ICollection<Card> Cards { get; set; } = null!;

        public required int CreatorId { get; set; }
        public virtual User Creator { get; set; } = null!;


        public const int NAME_MAX_LENGTH = 100;
        public const int DESCRIPTION_MAX_LENGTH = 300;
    }
}