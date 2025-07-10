package com.prm392g2.prmapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.database.daos.CardDao;
import com.prm392g2.prmapp.entities.Card;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CardViewModel extends AndroidViewModel {
    private final CardDao cardDao;
    private final ExecutorService executorService;
    
    private final MutableLiveData<List<Card>> cardsByDeck = new MutableLiveData<>();
    private final MutableLiveData<Card> selectedCard = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentCardIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isCardFlipped = new MutableLiveData<>(false);

    public CardViewModel(@NonNull Application application) {
        super(application);
        cardDao = PRMDatabase.getInstance().cardDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    // LiveData getters
    public LiveData<List<Card>> getCardsByDeck() {
        return cardsByDeck;
    }

    public LiveData<Card> getSelectedCard() {
        return selectedCard;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Integer> getCurrentCardIndex() {
        return currentCardIndex;
    }

    public LiveData<Boolean> getIsCardFlipped() {
        return isCardFlipped;
    }

    // Load cards by deck ID
    public void loadCardsByDeck(int deckId) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                List<Card> cards = cardDao.getByDeckId(deckId);
                cardsByDeck.postValue(cards);
                
                // Reset card state
                currentCardIndex.postValue(0);
                isCardFlipped.postValue(false);
                
                // Set first card as selected if available
                if (!cards.isEmpty()) {
                    selectedCard.postValue(cards.get(0));
                } else {
                    selectedCard.postValue(null);
                }
                
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to load cards: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Load card by ID
    public void loadCardById(int cardId) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                Card card = cardDao.getById(cardId);
                selectedCard.postValue(card);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to load card: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Create new card
    public void createCard(String front, String back, int deckId) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                List<Card> currentCards = cardDao.getByDeckId(deckId);
                int nextIndex = currentCards.size();
                
                Card newCard = new Card();
                newCard.front = front;
                newCard.back = back;
                newCard.deckId = deckId;
                newCard.index = nextIndex;
                
                cardDao.insert(newCard);
                loadCardsByDeck(deckId);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to create card: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Update existing card
    public void updateCard(Card card) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                cardDao.update(card);
                loadCardsByDeck(card.deckId);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to update card: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Delete card
    public void deleteCard(Card card) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                cardDao.delete(card);
                loadCardsByDeck(card.deckId);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to delete card: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Navigation methods for flashcard study
    public void nextCard() {
        List<Card> cards = cardsByDeck.getValue();
        if (cards != null && !cards.isEmpty()) {
            int currentIndex = currentCardIndex.getValue() != null ? currentCardIndex.getValue() : 0;
            int nextIndex = (currentIndex + 1) % cards.size();
            
            currentCardIndex.setValue(nextIndex);
            selectedCard.setValue(cards.get(nextIndex));
            isCardFlipped.setValue(false);
        }
    }

    public void previousCard() {
        List<Card> cards = cardsByDeck.getValue();
        if (cards != null && !cards.isEmpty()) {
            int currentIndex = currentCardIndex.getValue() != null ? currentCardIndex.getValue() : 0;
            int prevIndex = (currentIndex - 1 + cards.size()) % cards.size();
            
            currentCardIndex.setValue(prevIndex);
            selectedCard.setValue(cards.get(prevIndex));
            isCardFlipped.setValue(false);
        }
    }

    public void goToCard(int index) {
        List<Card> cards = cardsByDeck.getValue();
        if (cards != null && index >= 0 && index < cards.size()) {
            currentCardIndex.setValue(index);
            selectedCard.setValue(cards.get(index));
            isCardFlipped.setValue(false);
        }
    }

    // Flip card (show front/back)
    public void flipCard() {
        Boolean isFlipped = isCardFlipped.getValue();
        isCardFlipped.setValue(isFlipped != null ? !isFlipped : true);
    }

    // Shuffle cards
    public void shuffleCards() {
        List<Card> cards = cardsByDeck.getValue();
        if (cards != null && !cards.isEmpty()) {
            // Create a copy and shuffle
            List<Card> shuffledCards = new java.util.ArrayList<>(cards);
            java.util.Collections.shuffle(shuffledCards);
            
            // Update indices to maintain order
            for (int i = 0; i < shuffledCards.size(); i++) {
                shuffledCards.get(i).index = i;
            }
            
            cardsByDeck.setValue(shuffledCards);
            currentCardIndex.setValue(0);
            selectedCard.setValue(shuffledCards.get(0));
            isCardFlipped.setValue(false);
        }
    }

    // Get current card
    public Card getCurrentCard() {
        List<Card> cards = cardsByDeck.getValue();
        Integer currentIndex = currentCardIndex.getValue();
        
        if (cards != null && currentIndex != null && currentIndex >= 0 && currentIndex < cards.size()) {
            return cards.get(currentIndex);
        }
        return null;
    }

    // Get total number of cards
    public int getTotalCards() {
        List<Card> cards = cardsByDeck.getValue();
        return cards != null ? cards.size() : 0;
    }

    // Check if we're at the first card
    public boolean isFirstCard() {
        Integer currentIndex = currentCardIndex.getValue();
        return currentIndex != null && currentIndex == 0;
    }

    // Check if we're at the last card
    public boolean isLastCard() {
        List<Card> cards = cardsByDeck.getValue();
        Integer currentIndex = currentCardIndex.getValue();
        return cards != null && currentIndex != null && currentIndex == cards.size() - 1;
    }

    // Clear error message
    public void clearError() {
        errorMessage.setValue(null);
    }

    // Reset card state
    public void resetCardState() {
        currentCardIndex.setValue(0);
        isCardFlipped.setValue(false);
        
        List<Card> cards = cardsByDeck.getValue();
        if (cards != null && !cards.isEmpty()) {
            selectedCard.setValue(cards.get(0));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    // Factory for creating ViewModel with custom parameters if needed
    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
        private final Application application;

        public Factory(@NonNull Application application) {
            super(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(CardViewModel.class)) {
                return (T) new CardViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
} 