using Microsoft.AspNetCore.Mvc;
using PRMServer.Application.DTOs.Users;
using PRMServer.Application.Services.Contracts;

namespace PRMServer.Application.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly IUsersService _users;

        public UsersController(IUsersService users)
        {
            _users = users;
        }

        [HttpGet("{userId:guid}")]
        public async Task<ActionResult<UserDetailDTO>> GetUser(int userId)
        {
            var user = await _users.GetUser(userId);
            if (user == null)
                return NotFound();
            return Ok(user);
        }
    }
}
