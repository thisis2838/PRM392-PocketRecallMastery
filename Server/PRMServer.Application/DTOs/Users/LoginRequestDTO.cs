using System.ComponentModel.DataAnnotations;

namespace PRMServer.Application.DTOs.Users
{
    public class LoginDTO
    {
        [Required]
        public string UserName { get; set; } = null!;

        [Required]
        public string Password { get; set; } = null!;
    }
}
