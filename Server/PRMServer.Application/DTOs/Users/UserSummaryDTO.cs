namespace PRMServer.Application.DTOs.Users
{
    public record UserSummaryDTO
    {
        public required int Id { get; set; }
        public required string Username { get; set; }
        public required int DecksCount { get; set; }
    }
}
