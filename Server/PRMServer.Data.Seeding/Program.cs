using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using PRMServer.Data.Context;
using PRMServer.Data.Models;

namespace PRMServer.Data.Seeding
{
    internal class Program
    {
        static PRMContext _context = null!;
        static User _adminUser = null!;

        static async Task Main(string[] args)
        {
            DbContextOptionsBuilder builder = new();
            var configuration = new ConfigurationBuilder().AddUserSecrets<Program>().Build();
            builder.UseSqlServer(configuration["ConnectionString"]);
            _context = new PRMContext(builder.Options);

            await Clean();
            await SeedDecks();
        }

        static async Task Clean()
        {
            await _context.Database.EnsureDeletedAsync();
            await _context.Database.EnsureCreatedAsync();

            _adminUser  = new User
            {
                UserName = "admin",
                PasswordHash = new PasswordHasher<User>().HashPassword(new User(), "admin"),
                SecurityStamp = Random.Shared.Next().ToString("X"),
                ConcurrencyStamp = Random.Shared.Next().ToString(),
                NormalizedUserName = "ADMIN",
            };
            await _context.Users.AddAsync(_adminUser);
            await _context.SaveChangesAsync();
        }

        static async Task SeedDecks()
        {
            var random = new Random();
            
            int deckCount = random.Next(50, 100);
            var decks = new List<Deck>(deckCount);
            for (int i = 0; i < deckCount; i++)
            {
                var viewsTotal = random.Next(10, 100000);
                var viewsWeekly = (int)Math.Floor(viewsTotal * 0.15d);
                var downloadsTotal = (int)Math.Floor(viewsTotal * 0.85d);
                var downloadsWeekly = (int)Math.Floor(downloadsTotal * 0.15d);

                var deck = new Deck
                {
                    Name = $"Deck {i + 1}",
                    Description = $"This is a description for deck {i + 1}.",
                    IsPublic = random.Next(0, 20) == 1,
                    ViewsTotal = viewsTotal,
                    ViewsWeekly = viewsWeekly,
                    DownloadsTotal = downloadsTotal,
                    DownloadsWeekly = downloadsWeekly,
                    CreatedAt = DateOnly.FromDateTime(DateTime.Now),
                    CreatorId = _adminUser.Id,
                    Cards = new List<Card>()
                };

                int cardCount = random.Next(10, 100);
                for (int j = 0; j < cardCount; j++)
                {
                    var a = random.Next(10, 20);
                    var b = random.Next(40, 60);
                    var c = a + b;

                    deck.Cards.Add(new Card
                    {
                        Front = $"{a}+{b}=",
                        Back = c.ToString(),
                        DeckId = default,
                        Index = j
                    });
                }

                decks.Add(deck);
            }

            await _context.Decks.AddRangeAsync(decks);
            await _context.SaveChangesAsync();
        }
    }
}
