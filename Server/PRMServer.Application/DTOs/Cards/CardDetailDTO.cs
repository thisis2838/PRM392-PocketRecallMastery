namespace PRMServer.Application.DTOs.Cards
{
    public record CardDetailDTO
    {
        public required int Id { get; init; }
        public required string Front { get; set; }
        public required string Back { get; set; }
        public required int Index { get; set; }
    }
}
