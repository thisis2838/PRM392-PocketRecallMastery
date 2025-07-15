using Microsoft.AspNetCore.Identity;
using PRMServer.Application.DTOs.Users;
using System.Security.Claims;

namespace PRMServer.Application.Services.Contracts
{
    public interface IUsersService
    {
        Task<IdentityResult> RegisterAsync(RegisterDTO dto);
        Task<string?> LoginAsync(LoginDTO dto);
        Task LogoutAsync();
        public Task<UserDetailDTO?> GetUser(int userId);
        Task<UserDTO?> GetCurrentUserAsync(ClaimsPrincipal user);
        public Task<bool> DoesUserNameExist(string userName);
        public Task<bool> DoesEmailExist(string email);
    }
}
