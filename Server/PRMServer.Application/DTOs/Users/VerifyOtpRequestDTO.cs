namespace PRMServer.Application.DTOs.Users
{
    public class VerifyOtpRequestDTO
    {
        public string? Email { get; set; }
        public string? Otp { get; set; }
        public string? Purpose { get; set; }
    }
}
