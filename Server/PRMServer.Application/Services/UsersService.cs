using AutoMapper;
using AutoMapper.QueryableExtensions;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using PRMServer.Application.DTOs.Users;
using PRMServer.Application.Services.Contracts;
using PRMServer.Data.Context;
using PRMServer.Data.Models;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace PRMServer.Application.Services
{
    public class UsersService : IUsersService
    {
        private readonly PRMContext _context;
        private readonly UserManager<User> _userManager;
        private readonly SignInManager<User> _signInManager;
        private readonly IConfiguration _configuration;
        private readonly IMapper _mapper;

        public UsersService(PRMContext context, UserManager<User> userManager, SignInManager<User> signInManager, IConfiguration configuration, IMapper mapper)
        {
            _context = context;
            _userManager = userManager;
            _signInManager = signInManager;
            _configuration = configuration;
            _mapper = mapper;
        }

        public async Task<IdentityResult> RegisterAsync(RegisterDTO dto)
        {
            var user = new User
            {
                UserName = dto.UserName,
                Email = dto.Email
            };

            return await _userManager.CreateAsync(user, dto.Password);
        }

        public async Task<string?> LoginAsync(LoginRequestDTO dto)
        {
            var user = await _userManager.FindByNameAsync(dto.UserName);
            if (user == null) return null;

            var result = await _signInManager.CheckPasswordSignInAsync(user, dto.Password, false);
            if (!result.Succeeded) return null;

            return GenerateJwtToken(user);
        }

        public async Task LogoutAsync()
        {
            await _signInManager.SignOutAsync();
        }

        private string GenerateJwtToken(User user)
        {
            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, user.UserName!),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()),
                new Claim(ClaimTypes.NameIdentifier, user.Id.ToString())
            };

            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["Jwt:Key"]!));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: _configuration["Jwt:Issuer"],
                audience: _configuration["Jwt:Audience"],
                claims: claims,
                expires: DateTime.Now.AddHours(24),
                signingCredentials: creds
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }

        public async Task<UserSummaryDTO?> GetCurrentUserAsync(ClaimsPrincipal user)
        {
            var userIdString = user.FindFirstValue(ClaimTypes.NameIdentifier);
            if (string.IsNullOrEmpty(userIdString) || !int.TryParse(userIdString, out var userId))
                return null;

            var userEntity = await _userManager.FindByIdAsync(userId.ToString());
            if (userEntity == null)
                return null;

            return new UserSummaryDTO
            {
                Id = userEntity.Id,
                Username = userEntity.UserName ?? "null",
                Email = userEntity.Email ?? "null",
                DecksCount = await _context.Decks.CountAsync(d => d.CreatorId == userEntity.Id)
            };
        }

        public Task<UserDetailDTO?> GetUser(int userId)
        {
            return _context.Users
                .ProjectTo<UserDetailDTO>(_mapper.ConfigurationProvider)
                .SingleOrDefaultAsync(x => x.Id == userId);
        }

        public async Task<bool> DoesUserNameExist(string userName)
        {
            return await _userManager.Users.AnyAsync(u => u.UserName == userName);
        }

        public async Task<bool> DoesEmailExist(string email)
        {
            return await _userManager.Users.AnyAsync(u => u.Email == email);
        }
    }
}
