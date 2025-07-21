package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

import com.prm392g2.prmapp.PRMApplication;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.CardMainAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.services.DecksService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeckDetailActivity extends AppCompatActivity
{
    private int deckId;
    private RecyclerView cardRecycler;
    private CardMainAdapter adapter;

    // UI fields
    private TextView txtDeckName, txtDeckDescription, txtDeckCreator, txtCardCount, txtDownloadCount, txtViewCount, txtDate, txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_detail);
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main), (v, insets) ->
            {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        );

        deckId = getIntent().getIntExtra("deckId", -1);
        if (deckId == -1)
        {
            finish();
            return;
        }

        // Find UI fields
        cardRecycler = findViewById(R.id.card_list);
        cardRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(cardRecycler);

        txtDeckName = findViewById(R.id.txtDetailDeckName);
        txtDeckDescription = findViewById(R.id.txtDetailDeckDescription);
        txtDeckCreator = findViewById(R.id.txtDetailDeckCreator);
        txtCardCount = findViewById(R.id.txtDetailCardCount);
        txtDate = findViewById(R.id.txtDate);
        txtDownloadCount = findViewById(R.id.txtDownloadCount);
        txtViewCount = findViewById(R.id.txtViewCount);
        txtVersion = findViewById(R.id.txtDetailVersion);

        // Set up empty adapter for now
        cardRecycler.setVisibility(RecyclerView.GONE);

        Button btnBeginLearning = findViewById(R.id.btnBeginLearning);
        btnBeginLearning.setOnClickListener(v ->
        {
            Intent intent = new Intent(DeckDetailActivity.this, DeckLearningActivity.class);
            // intent.putExtra() as needed
            startActivity(intent);
        });

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());

        // Fetch deck details
        fetchDeckDetails();
    }

    private void fetchDeckDetails()
    {
        DecksService.getInstance().getById(
            deckId, new Callback<DeckDetailDTO>()
            {
                @Override
                public void onResponse(Call<DeckDetailDTO> call, Response<DeckDetailDTO> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        DeckDetailDTO deck = response.body();
                        runOnUiThread(() -> showDeckDetails(deck));
                    }
                    else
                    {
                        runOnUiThread(() -> Toast.makeText(DeckDetailActivity.this, "Failed to load deck details", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<DeckDetailDTO> call, Throwable t)
                {
                    runOnUiThread(() -> Toast.makeText(DeckDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        );
    }

    private void showDeckDetails(DeckDetailDTO deck)
    {
        txtDeckName.setText(deck.name);
        txtDeckDescription.setText(deck.description != null ? deck.description : "");
        txtDeckCreator.setText(deck.creator.username != null ? deck.creator.username : "");
        txtCardCount.setText(Integer.toString(deck.cardsCount));

        txtDownloadCount.setText(String.format("%d (%d this week)", deck.downloadsTotal, deck.downloadsWeekly));
        txtViewCount.setText(String.format("%d (%d this week)", deck.viewsTotal, deck.viewsWeekly));

        txtDate.setText(PRMApplication.GLOBAL_DATE_FORMAT.format(deck.getCreatedAt().getTime()));
        txtVersion.setText("v" + deck.version);

        cardRecycler.setVisibility(RecyclerView.VISIBLE);

        adapter = new CardMainAdapter(deck.cards, card ->
        {
        });
        cardRecycler.setAdapter(adapter);
    }
}