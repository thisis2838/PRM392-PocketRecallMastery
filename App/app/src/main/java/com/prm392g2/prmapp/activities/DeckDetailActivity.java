package com.prm392g2.prmapp.activities;

import static com.prm392g2.prmapp.services.SavedDecksService.SavedDeckStatus.NOT_SAVED;
import static com.prm392g2.prmapp.services.SavedDecksService.SavedDeckStatus.SAVED_LATER;
import static com.prm392g2.prmapp.services.SavedDecksService.SavedDeckStatus.SAVED_OUTDATED;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.prm392g2.prmapp.services.SavedDecksService;
import com.prm392g2.prmapp.services.UserWeeklyStatsService;

import org.w3c.dom.Text;

public class DeckDetailActivity extends AppCompatActivity
{
    private int deckId;
    private int deckVersion;
    private boolean isSavedDeck;
    private RecyclerView cardRecycler;
    private CardMainAdapter adapter;

    // UI fields
    private TextView txtDeckName, txtDeckDescription, txtDeckCreator, txtCardCount, txtDownloadCount, txtViewCount, txtDate, txtVersion, textSaveInfo;
    private ProgressBar progLearning;
    private Button btnBeginLearning, butOfflineSave;
    private ImageView icView, icDownload;

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
        deckVersion = getIntent().getIntExtra("version", -1); // If version is needed
        isSavedDeck = getIntent().getBooleanExtra("saved", false);

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
        textSaveInfo = findViewById(R.id.textSaveInfo);
        progLearning = findViewById(R.id.progLearning);
        btnBeginLearning = findViewById(R.id.btnBeginLearning);
        butOfflineSave = findViewById(R.id.butOfflineSave);
        icView = findViewById(R.id.icView);
        icDownload = findViewById(R.id.icDownload);

        cardRecycler.setVisibility(RecyclerView.GONE);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());

        UserWeeklyStatsService.getInstance().incrementDeckViews();

        if (isSavedDeck)
        {
            butOfflineSave.setVisibility(Button.GONE);
            textSaveInfo.setVisibility(TextView.VISIBLE);
            textSaveInfo.setText("Viewing saved deck");
            fetchSavedDeckDetails();
        }
        else
        {
            butOfflineSave.setVisibility(Button.VISIBLE);
            textSaveInfo.setVisibility(TextView.GONE);
            progLearning.setVisibility(ProgressBar.GONE);

            fetchDeckDetails();
        }
    }

    private void fetchSavedDeckDetails()
    {
        SavedDecksService.getInstance().get(
            deckId, deckVersion, deck ->
            {
                if (deck == null)
                {
                    runOnUiThread(() -> Toast.makeText(DeckDetailActivity.this, "Saved deck not found", Toast.LENGTH_SHORT).show());
                    return;
                }
                runOnUiThread(() -> gotSavedDeck(deck));
            }
        );
    }

    private void gotSavedDeck(DeckDetailDTO deck)
    {
        txtDeckName.setText(deck.name);
        txtDeckDescription.setText(deck.description != null ? deck.description : "");
        txtDeckCreator.setText(deck.creator.username != null ? deck.creator.username : "");
        txtCardCount.setText(Integer.toString(deck.cardsCount));
        txtDownloadCount.setVisibility(View.GONE);
        txtViewCount.setVisibility((View.GONE));
        icView.setVisibility(View.GONE);
        icDownload.setVisibility(View.GONE);

        var dateText = PRMApplication.GLOBAL_DATE_FORMAT.format(deck.getCreatedAt().getTime());
        if (deck.getUpdatedAt() != null)
        {
            dateText += " (last updated " + PRMApplication.GLOBAL_DATE_FORMAT.format(deck.getUpdatedAt().getTime()) + ")";
        }
        txtDate.setText(dateText);

        txtVersion.setText("v" + deck.version);

        cardRecycler.setVisibility(RecyclerView.VISIBLE);

        adapter = new CardMainAdapter(
            deck.cards, card ->
        {
        }
        );
        cardRecycler.setAdapter(adapter);

        // Show progress bar if learning info exists
        if (deck.learning != null)
        {
            progLearning.setVisibility(ProgressBar.VISIBLE);
            int progress = deck.learning.currentCardIndex + 1;
            progLearning.setMax(deck.cardsCount);
            progLearning.setProgress(progress);
            btnBeginLearning.setText("Continue learning");
        }
        else
        {
            progLearning.setVisibility(ProgressBar.GONE);
            btnBeginLearning.setText("Begin learning");
        }

        btnBeginLearning.setOnClickListener(v ->
        {
            Intent intent = new Intent(DeckDetailActivity.this, DeckLearningActivity.class);
            intent.putExtra("deckId", deck.id);
            intent.putExtra("saved", true);
            startActivity(intent);
        });

        ((Button) findViewById(R.id.btnRemoveSave)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(DeckDetailActivity.this)
                    .setTitle("Remove saved deck")
                    .setMessage("Are you sure you want to remove this deck from your offline saves? It will also remove any learning progress associated with it.")
                    .setPositiveButton(
                        "Yes", (dialog, which) ->
                        {
                            SavedDecksService.getInstance().remove(
                                deck.id, () ->
                                {
                                    runOnUiThread(() ->
                                    {
                                        Toast.makeText(DeckDetailActivity.this, "Deck removed from offline saves", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                                }
                            );
                        }
                    )
                    .setNegativeButton("No", null)
                    .show();
            }
        });
    }

    private void fetchDeckDetails()
    {
        DecksService.getInstance().getById(
            deckId, new retrofit2.Callback<DeckDetailDTO>()
            {
                @Override
                public void onResponse(retrofit2.Call<DeckDetailDTO> call, retrofit2.Response<DeckDetailDTO> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        DeckDetailDTO deck = response.body();
                        runOnUiThread(() -> gotDeck(deck));
                    }
                    else
                    {
                        runOnUiThread(() -> Toast.makeText(DeckDetailActivity.this, "Failed to load deck details", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<DeckDetailDTO> call, Throwable t)
                {
                    runOnUiThread(() -> Toast.makeText(DeckDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        );
    }

    private void gotDeck(DeckDetailDTO deck)
    {
        txtDeckName.setText(deck.name);
        txtDeckDescription.setText(deck.description != null ? deck.description : "");
        txtDeckCreator.setText(deck.creator.username != null ? deck.creator.username : "");
        txtCardCount.setText(Integer.toString(deck.cardsCount));

        txtDownloadCount.setVisibility(View.VISIBLE);
        txtViewCount.setVisibility((View.VISIBLE));
        icView.setVisibility(View.VISIBLE);
        icDownload.setVisibility(View.VISIBLE);
        txtDownloadCount.setText(String.format("%d (%d this week)", deck.downloadsTotal, deck.downloadsWeekly));
        txtViewCount.setText(String.format("%d (%d this week)", deck.viewsTotal, deck.viewsWeekly));

        var dateText = PRMApplication.GLOBAL_DATE_FORMAT.format(deck.getCreatedAt().getTime());
        if (deck.getUpdatedAt() != null)
        {
            dateText += " (last updated " + PRMApplication.GLOBAL_DATE_FORMAT.format(deck.getUpdatedAt().getTime()) + ")";
        }
        txtDate.setText(dateText);

        txtVersion.setText("v" + deck.version);

        cardRecycler.setVisibility(RecyclerView.VISIBLE);

        adapter = new CardMainAdapter(
            deck.cards, card ->
        {
        }
        );
        cardRecycler.setAdapter(adapter);

        progLearning.setVisibility(ProgressBar.GONE);

        btnBeginLearning.setText("Begin learning");
        btnBeginLearning.setOnClickListener(v ->
        {
            Intent intent = new Intent(DeckDetailActivity.this, DeckLearningActivity.class);
            intent.putExtra("deckId", deck.id);
            startActivity(intent);
        });

        SavedDecksService.getInstance().isSaved(
            deck.id, deck.version, saved ->
            {
                runOnUiThread(() ->
                {
                    if (saved == SAVED_LATER)
                    {
                        butOfflineSave.setVisibility(Button.GONE);
                        textSaveInfo.setVisibility(TextView.VISIBLE);
                        textSaveInfo.setText("This deck is already saved.");
                        return;
                    }
                    else
                    {
                        butOfflineSave.setVisibility(Button.VISIBLE);
                        if (saved == SAVED_OUTDATED)
                        {
                            textSaveInfo.setVisibility(TextView.VISIBLE);
                            textSaveInfo.setText("Older version of deck saved.");
                        }
                        else
                        {
                            textSaveInfo.setVisibility(TextView.GONE);
                        }

                        butOfflineSave.setOnClickListener(v ->
                        {
                            Runnable saveDeck = () ->
                            {
                                SavedDecksService.getInstance().save(
                                    deck, () ->
                                    {
                                        runOnUiThread(() ->
                                        {
                                            Toast.makeText(
                                                DeckDetailActivity.this,
                                                "Deck saved for offline use",
                                                Toast.LENGTH_SHORT
                                            ).show();
                                        });
                                    }
                                );
                            };

                            if (saved == SAVED_OUTDATED)
                            {
                                new AlertDialog.Builder(DeckDetailActivity.this)
                                    .setTitle("Update saved deck")
                                    .setMessage("Saving this deck again will remove existing learn progresses. Do you want to proceed?")
                                    .setPositiveButton(
                                        "Yes", (dialog, which) ->
                                        {
                                            saveDeck.run();
                                        }
                                    )
                                    .setNegativeButton("No", null)
                                    .show();
                            }
                            else
                            {
                                saveDeck.run();
                            }
                        });
                    }
                });
            }
        );
    }
}