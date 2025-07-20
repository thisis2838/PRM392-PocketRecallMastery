namespace PRMServer.Application.DTOs.Users
{
    public class ResetPasswordDTO
    {
        public string? Email { get; set; }
        public string? NewPassword { get; set; }
    }
}
