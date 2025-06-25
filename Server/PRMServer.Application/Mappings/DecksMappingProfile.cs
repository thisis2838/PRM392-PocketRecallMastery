using PRMServer.Application.DTOs.Decks;
using PRMServer.Data.Models;

namespace PRMServer.Application.Mappings
{
    public class DecksMappingProfile : AutoMapper.Profile
    {
        public DecksMappingProfile()
        {
            CreateMap<Deck, DeckSummaryDTO>();
            CreateMap<Deck, DeckDetailDTO>();
        }
    }
}
