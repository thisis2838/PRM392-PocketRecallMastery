package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.CardDetailAdapter;
import com.prm392g2.prmapp.adapters.CardMainAdapter;
import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.dtos.cards.CardDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.entities.Card;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeckDetailActivity extends AppCompatActivity {

    private DeckDetailDTO deck;
    private CardMainAdapter cardMainAdapter;
    private CardDetailAdapter cardDetailAdapter;
    private RecyclerView cardListRecyclerView;
    private RecyclerView cardDetailsRecyclerView;
    private Button btnBeginLearning;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        cardListRecyclerView = findViewById(R.id.card_list);
        cardDetailsRecyclerView = findViewById(R.id.cardDetailsList);
        btnBeginLearning = findViewById(R.id.btnBeginLearning);
        btnBack = findViewById(R.id.btn_back);

        // Initialize adapters with empty lists
        cardMainAdapter = new CardMainAdapter(new ArrayList<>(), card -> {
            // Handle item click for main card list
        });
        cardDetailAdapter = new CardDetailAdapter(new ArrayList<>(), card -> {
            // Handle item click for card details list
        });

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup button listeners
        setupButtonListeners();

        // Fetch deck details
        int deckId = getIntent().getIntExtra("deckId", -1);
        if (deckId == -1) {
            Toast.makeText(this, "Invalid Deck ID", Toast.LENGTH_LONG).show();
            finish(); // Close activity if deckId is invalid
            return;
        }
        fetchDeckDetails(deckId);
    }

    private void setupRecyclerViews() {
        cardListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(cardListRecyclerView);
        cardListRecyclerView.setAdapter(cardMainAdapter);

        cardDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardDetailsRecyclerView.setAdapter(cardDetailAdapter);
    }

    private void setupButtonListeners() {
        btnBeginLearning.setOnClickListener(v -> {
            if (deck != null && deck.cards != null && !deck.cards.isEmpty()) {
                Intent intent = new Intent(DeckDetailActivity.this, DeckLearningActivity.class);
                // Pass necessary data to DeckLearningActivity, for example, the deck or cards
                // intent.putExtra("deckData", deck); // Make sure DeckDetailDTO is Parcelable or Serializable
                startActivity(intent);
            } else {
                Toast.makeText(DeckDetailActivity.this, "No cards available to learn.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void fetchDeckDetails(int deckId) {
        DeckApi api = ApiClient.getClient().create(DeckApi.class);
        Call<DeckDetailDTO> call = api.getDeckById(String.valueOf(deckId));
        call.enqueue(new Callback<DeckDetailDTO>() {
             @Override
             public void onResponse(Call<DeckDetailDTO> call, Response<DeckDetailDTO> response) {
                 if(response.isSuccessful() && response.body() != null){
                     deck = response.body();
                     updateUIWithDeckDetails();
                    if (deck.cards == null || deck.cards.isEmpty()) {
                        Toast.makeText(DeckDetailActivity.this, "No cards found in this deck.", Toast.LENGTH_LONG).show();
                    }
                 } else {
                     String errorMessage = "Failed " + response.code();
                     try {
                         if (response.errorBody() != null) {
                             errorMessage += " - " + response.errorBody().string();
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                        errorMessage += " (Error reading error body)";
                     }
                     Toast.makeText(DeckDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                 }
             }

             @Override
             public void onFailure(Call<DeckDetailDTO> call, Throwable t) {
                 Toast.makeText(DeckDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
             }
         });
    }

    private void updateUIWithDeckDetails() {
        if (deck != null && deck.cards != null) {
            cardMainAdapter.updateData(deck.cards);
            cardDetailAdapter.updateData(deck.cards);
        } else {
            Toast.makeText(this, "Deck data or cards are null, cannot update UI.", Toast.LENGTH_SHORT).show();
        }
    }
}