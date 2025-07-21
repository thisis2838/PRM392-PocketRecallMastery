using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using PRMServer.Application.DTOs.Users;
using PRMServer.Application.Services;
using PRMServer.Application.Services.Contracts;
using System.Security.Claims;

namespace PRMServer.Application.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly IUsersService _usersService;

        public UsersController(IUsersService users)
        {
            _usersService = users;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterDTO model)
        {
            if (await _usersService.DoesUserNameExist(model.UserName))
            {
                return BadRequest("UserName existed");
            }
            if (await _usersService.DoesEmailExist(model.Email))
            {
                return BadRequest("Email existed");
            }
            var result = await _usersService.RegisterAsync(model);
            if (!result.Succeeded)
                return BadRequest(result.Errors);

            return Ok("Registration successful");
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequestDTO model)
        {
            var token = await _usersService.LoginAsync(model);
            if (token == null)
                return Unauthorized("Invalid username or password");

            return Ok(new { token });
        }

        [Authorize]
        [HttpPost("logout")]
        public async Task<IActionResult> Logout()
        {
            await _usersService.LogoutAsync();
            return Ok("Logged out");
        }

        [Authorize]
        [HttpGet("me")]
        public async Task<IActionResult> GetCurrentUser()
        {
            var userDto = await _usersService.GetCurrentUserAsync(User);
            if (userDto == null)
                return Unauthorized();

            return Ok(userDto);
        }


        [HttpGet("{userId:guid}")]
        public async Task<ActionResult<UserDetailDTO>> GetUser(int userId)
        {
            var user = await _usersService.GetUser(userId);
            if (user == null)
                return NotFound();
            return Ok(user);
        }
    }
}
