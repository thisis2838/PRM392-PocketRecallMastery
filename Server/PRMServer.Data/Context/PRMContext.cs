using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using PRMServer.Data.Models;

namespace PRMServer.Data.Context
{
    public class PRMContext : IdentityDbContext<User, IdentityRole<int>, int>
    {
        public DbSet<Card> Cards { get; init; }
        public DbSet<Deck> Decks { get; init; }

        public PRMContext(DbContextOptions options) : base(options) { }
    }
}
