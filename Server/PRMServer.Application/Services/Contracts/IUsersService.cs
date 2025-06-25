using PRMServer.Application.DTOs.Users;

namespace PRMServer.Application.Services.Contracts
{
    public interface IUsersService
    {
        public Task<UserDetailDTO?> GetUser(int userId);
    }
}
