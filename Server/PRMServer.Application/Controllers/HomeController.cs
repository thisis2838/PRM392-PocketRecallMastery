using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using PRMServer.Application.DTOs.Decks;
using PRMServer.Application.Services.Contracts;

namespace PRMServer.Application.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class HomeController : ControllerBase
    {
        private readonly IHomeService _home;

        public HomeController(IHomeService home)
        {
            _home = home;
        }

        [HttpGet("recent")]
        public async Task<ActionResult<IList<DeckSummaryDTO>>> GetRecentDecks()
        {
            var result = await _home.GetRecentDecks();
            return Ok(result);
        }

        [HttpGet("popular/weekly")]
        public async Task<ActionResult<IList<DeckSummaryDTO>>> GetWeeklyPopularDecks()
        {
            var result = await _home.WeeklyPopularDecks();
            return Ok(result);
        }
    }
}
