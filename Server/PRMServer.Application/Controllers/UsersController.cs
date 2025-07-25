﻿using Microsoft.AspNetCore.Authorization;
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
        public async Task<ActionResult<LoginResponseDTO>> Login(LoginRequestDTO model)
        {
            var response = await _usersService.LoginAsync(model);
            if (response == null)
                return Unauthorized("Invalid username or password.");

            return Ok(response);
        }

        [Authorize]
        [HttpGet("me")]
        public async Task<IActionResult> GetCurrentUser()
        {
            var userSummaryDto = await _usersService.GetCurrentUserAsync(User);
            if (userSummaryDto == null)
                return Unauthorized();

            return Ok(userSummaryDto);
        }


        [HttpGet("{userId:int}")]
        public async Task<ActionResult<UserDetailDTO>> GetUser(int userId)
        {
            var user = await _usersService.GetUser(userId);
            if (user == null)
                return NotFound();
            return Ok(user);
        }

        [Authorize]
        [HttpPost("request-email-change")]
        public async Task<IActionResult> RequestEmailChange([FromBody] EmailChangeDTO model)
        {
            if (!Regex.IsMatch(model.NewEmail, @"^[^@\s]+@[^@\s]+\.[^@\s]+$"))
                return BadRequest("Invalid email format.");

            if (await _usersService.DoesEmailExist(model.NewEmail))
                return BadRequest("Email already exists.");

            _cache.Set($"change-email:{model.NewEmail}", model.NewEmail, TimeSpan.FromMinutes(10));

            await _otpService.GenerateAndSendOtp(model.NewEmail, "change-email");

            return Ok("OTP sent to new email.");
        }

        [Authorize]
        [HttpPost("confirm-email-change")]
        public async Task<IActionResult> ConfirmEmailChange([FromBody] VerifyOtpRequestDTO request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) || string.IsNullOrWhiteSpace(request.Otp))
                return BadRequest("Email and OTP are required.");

            var user = await _userManager.GetUserAsync(User);
            if (user == null)
                return Unauthorized();

            var isValid = await _otpService.VerifyOtp(request.Email, request.Otp, "change-email");
            if (!isValid)
                return BadRequest("Invalid or expired OTP.");

            if (!_cache.TryGetValue($"change-email:{request.Email}", out string? cachedEmail) ||
                cachedEmail != request.Email)
            {
                return BadRequest("Email change session expired or invalid.");
            }

            user.Email = request.Email;
            user.UserName = user.UserName;
            user.EmailConfirmed = true;
            var result = await _userManager.UpdateAsync(user);

            if (!result.Succeeded)
                return BadRequest(result.Errors);

            var userDto = await _usersService.GetCurrentUserAsync(User);

            _cache.Remove($"change-email:{request.Email}");

            return Ok(userDto);
        }

        [HttpPost("send-otp")]
        public async Task<IActionResult> SendOtp([FromBody] SendOtpRequestDTO request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) ||
                string.IsNullOrWhiteSpace(request.Purpose))
            {
                return BadRequest("Email and purpose are required.");
            }

            switch (request.Purpose)
            {
                case "register":
                    await _otpService.GenerateAndSendOtp(request.Email, request.Purpose);
                    return Ok("OTP sent to email.");

                case "change-email":
                    // Allow sending OTP even if the email is not registered
                    if (await _usersService.DoesEmailExist(request.Email))
                        return BadRequest("This email is already in use.");

                    await _otpService.GenerateAndSendOtp(request.Email, request.Purpose);
                    return Ok("OTP sent to new email.");

                default:
                    var user = await _userManager.FindByEmailAsync(request.Email);
                    if (user != null)
                    {
                        await _otpService.GenerateAndSendOtp(request.Email, request.Purpose);
                    }
                    return Ok("If your email is registered, an OTP has been sent.");
            }
        }

        [HttpPost("confirm-reset-password")]
        public async Task<IActionResult> ConfirmResetPassword([FromBody] VerifyOtpRequestDTO request)
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

            var resetToken = await _userManager.GeneratePasswordResetTokenAsync(user);
            return Ok(new { ResetToken = resetToken });
        }

        [HttpPost("request-reset-password")]
        public async Task<IActionResult> RequestResetPassword([FromBody] ResetPasswordDTO dto)
        {
            if (string.IsNullOrWhiteSpace(dto.Email) ||
                string.IsNullOrWhiteSpace(dto.NewPassword))
            {
                return BadRequest("All fields are required.");
            }

            var user = await _userManager.FindByEmailAsync(dto.Email);
            if (user == null)
                return NotFound("User not found.");

            var token = await _userManager.GeneratePasswordResetTokenAsync(user);
            var result = await _userManager.ResetPasswordAsync(user, token, dto.NewPassword);
            if (!result.Succeeded)
                return BadRequest(result.Errors);

            return Ok("Password has been reset successfully.");
        }

        [HttpPost("change-password")]
        public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordDTO request)
        {
            if (string.IsNullOrWhiteSpace(request.CurrentPassword) || string.IsNullOrWhiteSpace(request.NewPassword))
                return BadRequest("Current and new passwords are required.");

            var userId = User.FindFirstValue(ClaimTypes.NameIdentifier);
            if (string.IsNullOrWhiteSpace(userId))
                return Unauthorized("User not authenticated.");
            var user = await _userManager.FindByIdAsync(userId);

            if (user == null)
                return Unauthorized("User not found.");

            var result = await _userManager.ChangePasswordAsync(user, request.CurrentPassword, request.NewPassword);
            if (!result.Succeeded)
            {
                var errorDescriptions = result.Errors.Select(e => e.Description).ToList();
                return BadRequest(errorDescriptions);
            }
            if (result.Succeeded)
                return Ok("Password changed successfully.");

            return BadRequest(result.Errors.Select(e => e.Description));
        }
    }
}
