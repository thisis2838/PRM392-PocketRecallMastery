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
        public virtual ICollection<Deck> Decks { get; set; } = null!;
    }
}
