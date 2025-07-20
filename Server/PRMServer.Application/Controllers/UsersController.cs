using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Caching.Memory;
using PRMServer.Application.DTOs.Users;
using PRMServer.Application.Services;
using PRMServer.Application.Services.Contracts;
using PRMServer.Data.Models;
using System.Security.Claims;
using System.Text.RegularExpressions;

namespace PRMServer.Application.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly UserManager<User> _userManager;
        private readonly IUsersService _usersService;
        private readonly IOtpService _otpService;
        private readonly IMemoryCache _cache;

        public UsersController(UserManager<User> userManager, IUsersService users, IOtpService otpService, IMemoryCache cache)
        {
            _userManager = userManager;
            _usersService = users;
            _otpService = otpService;
            _cache = cache;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterDTO model)
        {
            if (string.IsNullOrWhiteSpace(model.UserName) || !Regex.IsMatch(model.UserName, @"^[a-zA-Z0-9]+$"))
            {
                return BadRequest("Username must contain only letters and numbers, with no spaces.");
            }
            if (await _usersService.DoesUserNameExist(model.UserName))
            {
                return BadRequest("Username already exists.");
            }
            if (await _usersService.DoesEmailExist(model.Email))
            {
                return BadRequest("Email already exists.");
            }
            var result = await _usersService.RegisterAsync(model);
            if (!result.Succeeded)
                return BadRequest(result.Errors);

            return Ok(new { Message = "Registration successful.", model.Email });
        }

        [HttpPost("pre-register")]
        public async Task<IActionResult> PreRegister(RegisterDTO model)
        {
            if (string.IsNullOrWhiteSpace(model.UserName) || !Regex.IsMatch(model.UserName, @"^[a-zA-Z0-9]+$"))
                return BadRequest("Username must contain only letters and numbers, with no spaces.");

            if (await _usersService.DoesUserNameExist(model.UserName))
                return BadRequest("Username already exists.");

            if (await _usersService.DoesEmailExist(model.Email))
                return BadRequest("Email already exists.");

            _cache.Set($"register:{model.Email}", model, TimeSpan.FromMinutes(10));

            await _otpService.GenerateAndSendOtp(model.Email, "register");
            return Ok("OTP sent to email. Please verify to complete registration.");
        }

        [HttpPost("complete-registration")]
        public async Task<IActionResult> CompleteRegistration([FromBody] VerifyOtpRequestDTO request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Otp))
                return BadRequest("Email and OTP are required.");

            var isValid = await _otpService.VerifyOtp(request.Email, request.Otp, "register");
            if (!isValid)
                return BadRequest("Invalid or expired OTP.");

            if (!_cache.TryGetValue($"register:{request.Email}", out RegisterDTO? model) || model == null)
            {
                return BadRequest("Registration session expired or invalid. Please try registering again.");
            }

            var result = await _usersService.RegisterAsync(model);
            if (!result.Succeeded)
                return BadRequest(result.Errors);

            // Mark email confirmed
            var user = await _userManager.FindByEmailAsync(model.Email);
            if (user != null)
            {
                user.EmailConfirmed = true;
                await _userManager.UpdateAsync(user);
            }

            // Remove cache entry
            _cache.Remove($"register:{request.Email}");

            return Ok("Registration completed and email confirmed.");
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginDTO model)
        {
            var token = await _usersService.LoginAsync(model);
            if (token == null)
                return Unauthorized("Invalid username or password.");

            return Ok(new { token });
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

        [HttpGet("{userId}")]
        public async Task<ActionResult<UserDetailDTO>> GetUser(int userId)
        {
            var user = await _usersService.GetUser(userId);
            if (user == null)
                return NotFound();
            return Ok(user);
        }

        [HttpPost("send-otp")]
        public async Task<IActionResult> SendOtp([FromBody] SendOtpRequestDTO request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) ||
                string.IsNullOrWhiteSpace(request.Purpose))
            {
                return BadRequest("Email and purpose are required.");
            }

            if (request.Purpose != "register")
            {
                var user = await _userManager.FindByEmailAsync(request.Email);
                if (user == null)
                    return NotFound("User not found.");
            }

            await _otpService.GenerateAndSendOtp(request.Email, request.Purpose);
            return Ok("OTP sent to email.");
        }

        [HttpPost("verify-otp")]
        public async Task<IActionResult> VerifyOtp([FromBody] VerifyOtpRequestDTO request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) ||
                string.IsNullOrWhiteSpace(request.Otp) ||
                string.IsNullOrWhiteSpace(request.Purpose))
            {
                return BadRequest("Email, OTP, and purpose are required.");
            }
            var user = await _userManager.FindByEmailAsync(request.Email);
            if (user == null)
                return NotFound("User not found.");

            var isValid = await _otpService.VerifyOtp(request.Email, request.Otp, request.Purpose);
            if (!isValid)
                return BadRequest("Invalid or expired OTP.");

            switch (request.Purpose)
            {
                case "register":
                    user.EmailConfirmed = true;
                    await _userManager.UpdateAsync(user);
                    return Ok("Email confirmed.");

                case "forgot_password":
                    var resetToken = await _userManager.GeneratePasswordResetTokenAsync(user);
                    return Ok(new { ResetToken = resetToken });

                default:
                    return BadRequest("Unsupported OTP purpose.");
            }
        }
    }
}
