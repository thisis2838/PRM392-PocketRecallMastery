package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.dtos.mappings.CardMappings;
import com.prm392g2.prmapp.dtos.mappings.DeckMappings;
import com.prm392g2.prmapp.dtos.mappings.UserMappings;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SavedDecksService
{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;
    public SavedDecksService(Context context)
    {
        this.context = context;
    }

    public void getAll(Consumer<List<DeckSummaryDTO>> callback)
    {
        executor.execute(() ->
        {
            var db = PRMDatabase.getInstance().deckDao();
            var decks = db.getAllWithUserAndLearning().stream()
                .map((v) -> DeckMappings.DeckToDeckSummaryDTO(v.deck, v.creator, v.learning))
                .collect(Collectors.toList());
            callback.accept(decks);
        });
    }

    public void get(int id, int version, Consumer<DeckDetailDTO> callback)
    {
        executor.execute(() ->
        {
            var db = PRMDatabase.getInstance().deckDao();
            var ret = db.getByIdWithAll(id);
            if (ret == null)
            {
                callback.accept(null);
                return;
            }
            var dto = DeckMappings.DeckToDeckDetailDTO(ret.deck, ret.creator, ret.learning, ret.cards);
            callback.accept(dto);
        });
    }

    public void save(DeckDetailDTO deck, Runnable callback)
    {
        executor.execute(() ->
        {
            var deckDao = PRMDatabase.getInstance().deckDao();
            deckDao.deleteById(deck.id);

            var userDao = PRMDatabase.getInstance().userDao();
            var user = userDao.getById(deck.creator.id);
            if (user == null)
            {
                userDao.insert(UserMappings.UserSummaryDTOToUser(deck.creator));
            }

            var deckEntity = DeckMappings.DeckSummaryDTOToDeck(deck);
            deckDao.insert(deckEntity);

            var cardDao = PRMDatabase.getInstance().cardDao();
            var cards = deck.cards.stream()
                .map(x -> CardMappings.CardDetailDTOToCard(x, deck.id))
                .collect(Collectors.toList());
            cardDao.insertAll(cards);

            if (callback != null)
                callback.run();
        });
    }

    public void isSaved(int deckId, int version, Consumer<SavedDeckStatus> callback)
    {
        executor.execute(() ->
        {
            var db = PRMDatabase.getInstance().deckDao();
            var saved = db.getById(deckId);
            if (saved == null)
            {
                callback.accept(SavedDeckStatus.NOT_SAVED);
                return;
            }
            if (saved.version < version)
            {
                callback.accept(SavedDeckStatus.SAVED_OUTDATED);
                return;
            }
            callback.accept(SavedDeckStatus.SAVED_LATER);
        });
    }
    public enum SavedDeckStatus
    {
        SAVED_LATER,
        SAVED_OUTDATED,
        NOT_SAVED
    }

    public void remove(int deckId, Runnable callback)
    {
        executor.execute(() ->
        {
            var db = PRMDatabase.getInstance().deckDao();
            db.deleteById(deckId);
            callback.run();
        });
    }

    private static SavedDecksService instance;
    public static void initialize(Context context)
    {
        instance = new SavedDecksService(context);
    }
    public static SavedDecksService getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
