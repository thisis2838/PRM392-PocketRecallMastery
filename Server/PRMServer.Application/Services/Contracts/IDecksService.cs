using Microsoft.AspNetCore.Identity;
using PRMServer.Application.DTOs.Decks;

namespace PRMServer.Application.Services.Contracts
{
    public interface IDecksService
    {
        public Task<DeckListDTO> GetUserDecks(int userId, DeckListArgumentsDTO arguments, bool onlyPublic);
        public Task<DeckListDTO> GetPublicDecks(DeckListArgumentsDTO arguments);
        public Task<DeckDetailDTO?> GetDeck(int id);
    }
}
