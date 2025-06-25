using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using PRMServer.Application.DTOs.Decks;
using PRMServer.Application.Services.Contracts;

namespace PRMServer.Application.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class DecksController : ControllerBase
    {
        private readonly IDecksService _decks;

        public DecksController(IDecksService decks)
        {
            _decks = decks;
        }

        [HttpGet("user")]
        public async Task<ActionResult<DeckListDTO>> GetUserDecks([FromQuery] DeckListArgumentsDTO arguments)
        {
            throw new NotImplementedException();
            /*
            var result = await _decks.GetUserDecks(userId, arguments);
            return Ok(result);
            */
        }

        [HttpGet("public")]
        public async Task<ActionResult<DeckListDTO>> GetPublicDecks([FromQuery] DeckListArgumentsDTO arguments)
        {
            var result = await _decks.GetPublicDecks(arguments);
            return Ok(result);
        }

        [HttpGet("{id:guid}")]
        public async Task<ActionResult<DeckDetailDTO>> GetDeck(int id)
        {
            var result = await _decks.GetDeck(id);
            if (result == null)
                return NotFound();
            return Ok(result);
        }

    }
}
