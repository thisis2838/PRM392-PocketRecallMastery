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
        public async Task<ActionResult<DeckListDTO>> GetByUser(int userId)
        {
            var result = await _decks.GetUserDecks(userId, new(), true);
            return Ok(result);
        }

        [HttpGet("my")]
        [Authorize]
        public async Task<ActionResult<DeckListDTO>> GetMine()
        {
            var userId = HttpContext.GetUserId()!.Value;
            var result = await _decks.GetUserDecks(userId, new(), false);
            return Ok(result);
        }

        [HttpGet("public")]
        public async Task<ActionResult<DeckListDTO>> GetPublic([FromQuery] DeckListArgumentsDTO arguments)
        {
            var result = await _decks.GetPublicDecks(arguments);
            return Ok(result);
        }

        [HttpGet("{id:int}")]
        public async Task<ActionResult<DeckDetailDTO>> Get(int id)
        {   
            try
            {
                var result = await _decks.GetDeck(id, HttpContext.GetUserId());
                if (result == null)
                    return NotFound();
                return Ok(result);
            }
            catch (UnauthorizedAccessException) { return Unauthorized(); }
        }

        [HttpPut]
        [Authorize]
        public async Task<ActionResult<int>> Create([FromBody] DeckCreationDTO dto)
        {
            var userId = HttpContext.GetUserId()!.Value;
            var id = await _decks.CreateDeck(userId, dto);
            return Ok(id);
        }

        [HttpPost("{id:int}")]
        [Authorize]
        public async Task<IActionResult> Edit(int id, [FromBody] DeckEditDTO dto)
        {
            var userId = HttpContext.GetUserId()!.Value;

            try
            {
                await _decks.EditDeck(id, userId, dto);
            }
            catch (KeyNotFoundException) { return NotFound();  }
            catch (ArgumentException) { return BadRequest();  }
            catch (UnauthorizedAccessException) { return Unauthorized(); }

            return Ok();
        }

        [HttpDelete("{id:int}")]
        [Authorize]
        public async Task<IActionResult> Delete(int id)
        {
            var userId = HttpContext.GetUserId()!.Value;

            try
            {
                await _decks.DeleteDeck(id, userId);
            }
            catch (KeyNotFoundException) { return NotFound(); }
            catch (UnauthorizedAccessException) { return Unauthorized(); }

            return Ok();
        }
    }
}
