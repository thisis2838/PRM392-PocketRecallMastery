using AutoMapper;
using AutoMapper.QueryableExtensions;
using Microsoft.EntityFrameworkCore;
using PRMServer.Application.DTOs.Users;
using PRMServer.Application.Services.Contracts;
using PRMServer.Data.Context;

namespace PRMServer.Application.Services
{
    public class UsersService : IUsersService
    {
        private readonly PRMContext _context;
        private readonly IMapper _mapper;

        public UsersService(PRMContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public Task<UserDetailDTO?> GetUser(int userId)
        {
            return _context.Users
                .ProjectTo<UserDetailDTO>(_mapper.ConfigurationProvider)
                .SingleOrDefaultAsync(x => x.Id == userId);
        }
    }
}
