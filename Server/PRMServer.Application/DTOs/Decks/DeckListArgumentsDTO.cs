namespace PRMServer.Application.DTOs.Decks
{
    public record DeckListArgumentsDTO
    {
        public string? Search { get; set; } = null;
        public int? MinCardCount { get; set; } = null;
        public int? MaxCardCount { get; set; } = null;
        public DeckListMetric SortingMetric { get; set; } = DeckListMetric.Name;
        public bool SortingAscending { get; set; } = true;
        
        //public int PageIndex { get; set; } = 0;
        //public int PageSize { get; set; } = 10;
    }

    public enum DeckListMetric
    { 
        Name,
        View,
        Download,
    }
}
