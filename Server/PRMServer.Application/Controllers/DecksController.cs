using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using PRMServer.Application.DTOs.Decks;
using PRMServer.Application.Services.Contracts;
using PRMServer.Application.Utilities.Helpers;

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
        public async Task<ActionResult<DeckListDTO>> GetUserDecks(int userId)
        {
            var result = await _decks.GetUserDecks(userId, new(), true);
            return Ok(result);
        }

        [HttpGet("my")]
        [Authorize]
        public async Task<ActionResult<DeckListDTO>> GetMyDecks()
        {
            var userId = HttpContext.GetUserId()!.Value;
            var result = await _decks.GetUserDecks(userId, new(), false);
            return Ok(result);
        }

        [HttpGet("public")]
        public async Task<ActionResult<DeckListDTO>> GetPublicDecks([FromQuery] DeckListArgumentsDTO arguments)
        {
            var result = await _decks.GetPublicDecks(arguments);
            return Ok(result);
        }

        [HttpGet("{id:int}")]
        public async Task<ActionResult<DeckDetailDTO>> GetDeck(int id)
        {   
            var result = await _decks.GetDeck(id);
            if (result == null)
                return NotFound();
            return Ok(result);
        }
    }
}
