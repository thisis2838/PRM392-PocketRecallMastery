package com.prm392g2.prmapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.database.daos.DeckDao;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.entities.extensions.DeckWithAll;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeckViewModel extends AndroidViewModel {
    private final DeckDao deckDao;
    private final ExecutorService executorService;
    
    private final MutableLiveData<List<Deck>> allDecks = new MutableLiveData<>();
    private final MutableLiveData<DeckWithAll> selectedDeck = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DeckViewModel(@NonNull Application application) {
        super(application);
        deckDao = PRMDatabase.getInstance().deckDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    // LiveData getters
    public LiveData<List<Deck>> getAllDecks() {
        return allDecks;
    }

    public LiveData<DeckWithAll> getSelectedDeck() {
        return selectedDeck;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Load all decks
    public void loadAllDecks() {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                List<Deck> decks = deckDao.getAll();
                allDecks.postValue(decks);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to load decks: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Load deck by ID with all related data
    public void loadDeckById(int deckId) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                DeckWithAll deckWithAll = deckDao.getByIdWithAll(deckId);
                selectedDeck.postValue(deckWithAll);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to load deck: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Create new deck
    public void createDeck(String name, String description, int creatorId, int version) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                Deck newDeck = new Deck();
                newDeck.name = name;
                newDeck.description = description;
                newDeck.creatorId = creatorId;
                newDeck.version = version;
                
                // Set current date
                GregorianCalendar currentDate = new GregorianCalendar();
                newDeck.dateYear = currentDate.get(Calendar.YEAR);
                newDeck.dateMonth = currentDate.get(Calendar.MONTH);
                newDeck.dateDay = currentDate.get(Calendar.DAY_OF_MONTH);
                
                deckDao.insert(newDeck);
                loadAllDecks();
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to create deck: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Update existing deck
    public void updateDeck(Deck deck) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                deckDao.update(deck);
                loadAllDecks();
                
                // If this is the selected deck, reload it
                if (selectedDeck.getValue() != null && 
                    selectedDeck.getValue().deck.id == deck.id) {
                    loadDeckById(deck.id);
                }
                
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to update deck: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Delete deck
    public void deleteDeck(Deck deck) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                deckDao.delete(deck);
                loadAllDecks();
                
                // Clear selected deck if it was the deleted one
                if (selectedDeck.getValue() != null && 
                    selectedDeck.getValue().deck.id == deck.id) {
                    selectedDeck.postValue(null);
                }
                
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to delete deck: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Search decks by name
    public void searchDecksByName(String searchQuery) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                List<Deck> allDecksList = deckDao.getAll();
                List<Deck> filteredDecks = allDecksList.stream()
                    .filter(deck -> deck.name.toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
                
                allDecks.postValue(filteredDecks);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to search decks: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Get decks by creator
    public void getDecksByCreator(int creatorId) {
        isLoading.setValue(true);
        executorService.execute(() -> {
            try {
                List<Deck> allDecksList = deckDao.getAll();
                List<Deck> creatorDecks = allDecksList.stream()
                    .filter(deck -> deck.creatorId == creatorId)
                    .collect(java.util.stream.Collectors.toList());
                
                allDecks.postValue(creatorDecks);
                errorMessage.postValue(null);
            } catch (Exception e) {
                errorMessage.postValue("Failed to load creator decks: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    // Clear error message
    public void clearError() {
        errorMessage.setValue(null);
    }

    // Clear selected deck
    public void clearSelectedDeck() {
        selectedDeck.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    // Factory for creating ViewModel with custom parameters if needed
//    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
//        private final Application application;
//
//        public Factory(@NonNull Application application) {
//            super(application);
//            this.application = application;
//        }
//
//        @NonNull
//        @Override
//        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//            if (modelClass.isAssignableFrom(DeckViewModel.class)) {
//                return (T) new DeckViewModel(application);
//            }
//            throw new IllegalArgumentException("Unknown ViewModel class");
//        }
//    }
} 