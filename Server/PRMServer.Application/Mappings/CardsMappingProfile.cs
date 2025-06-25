using PRMServer.Application.DTOs.Cards;
using PRMServer.Data.Models;

namespace PRMServer.Application.Mappings
{
    public class CardsMappingProfile : AutoMapper.Profile
    {
        public CardsMappingProfile()
        {
            CreateMap<Card, CardDetailDTO>();
        }
    }
}
