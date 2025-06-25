using PRMServer.Application.DTOs.Users;
using PRMServer.Data.Models;

namespace PRMServer.Application.Mappings
{
    public class UsersMappingProfile : AutoMapper.Profile
    {
        public UsersMappingProfile()
        {
            CreateMap<User, UserSummaryDTO>();
            CreateMap<User, UserDetailDTO>()
                .ForMember(x => x.DecksViewsTotal, x => x.MapFrom(y => y.Decks.Sum(z => z.ViewsTotal)))
                .ForMember(x => x.DecksViewsWeekly, x => x.MapFrom(y => y.Decks.Sum(z => z.ViewsWeekly)))
                .ForMember(x => x.DecksDownloadsTotal, x => x.MapFrom(y => y.Decks.Sum(z => z.DownloadsTotal)))
                .ForMember(x => x.DecksDownloadsWeekly, x => x.MapFrom(y => y.Decks.Sum(z => z.DownloadsWeekly)))
                .ForMember(x => x.PopularDecks, x => x.MapFrom
                (y => 
                    y.Decks
                        .OrderByDescending(x => x.ViewsTotal + x.DownloadsTotal)
                        .Take(UserDetailDTO.MAX_POPULAR_DECKS)
                ))
                ;
        }
    }
}
