using Microsoft.AspNetCore.Identity;
using PRMServer.Application.DTOs.Decks;

namespace PRMServer.Application.Services.Contracts
{
    public interface IDecksService
    {
        public Task<DeckListDTO> GetUserDecks(int userId, DeckListArgumentsDTO arguments, bool onlyPublic);
        public Task<DeckListDTO> GetPublicDecks(DeckListArgumentsDTO arguments);
        public Task<DeckDetailDTO?> GetDeck(int id, int? userId);

        public Task<int> CreateDeck(int userId, DeckCreationDTO creation);
        public Task EditDeck(int deckId, int userId, DeckEditDTO creation);
        public Task DeleteDeck(int deckId, int userId);
    }
}
