using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity;

namespace PRMServer.Data.Models
{
    public class User : IdentityUser<int>
    {
        [MaxLength(LANGUAGE_MAX_LENGTH)]
        public string? Language { get; set; }

        [MaxLength(THEME_MAX_LENGTH)]
        public string? ThemeName { get; set; }

        public bool IsNotificationOn { get; set; }

        public virtual ICollection<Deck> Decks { get; set; } = null!;


        public const int LANGUAGE_MAX_LENGTH = 50;
        public const int THEME_MAX_LENGTH = 50;
    }
}
