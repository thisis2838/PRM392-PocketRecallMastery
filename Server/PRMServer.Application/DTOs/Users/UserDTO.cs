namespace PRMServer.Application.DTOs.Users
{
    public class UserDTO
    {
        public int Id { get; set; }
        public string? Username { get; set; }
        public string? Email { get; set; }
        public string? Language { get; set; }
        public string? ThemeName { get; set; }
        public bool IsNotificationOn { get; set; }
    }
}
