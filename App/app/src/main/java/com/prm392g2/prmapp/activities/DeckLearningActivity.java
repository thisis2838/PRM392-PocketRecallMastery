package com.prm392g2.prmapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.CardLearningAdapter;
import com.prm392g2.prmapp.entities.Card;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.entities.Learning;
import com.prm392g2.prmapp.services.CardService;
import com.prm392g2.prmapp.services.DecksService;
import com.prm392g2.prmapp.services.LearningService;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class DeckLearningActivity extends AppCompatActivity
{
    LinearProgressIndicator progressBar;
    Learning learning;
    Deck deck;
    List<Card> cards = new ArrayList<>();
    boolean isRelearning = false;

    CardLearningAdapter adapter;
    int LastLearnIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_learning);
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main), (v, insets) ->
            {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        );
        progressBar = findViewById(R.id.progressBar);
        int deckId = getIntent().getIntExtra("deckId", -1);
        if (deckId == -1)
        {
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.card_list_learning);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        DecksService.getInstance().getSavedDeckById(deckId, d ->{
            if (d == null){
                runOnUiThread(() ->{
                    Toast.makeText(this, "Deck not found", Toast.LENGTH_SHORT).show();
                });
                finish();
                return;
            }
            deck = d;
        });
        CardService.getInstance().getCardsByDeckId(deckId, c -> {
            if (c == null || c.isEmpty()){
                runOnUiThread(() ->{
                    Toast.makeText(this, "Deck is empty", Toast.LENGTH_SHORT).show();
                });
                finish();
                return;
            }
            cards = c;
        });
        LearningService.getInstance().getByDeckId(deckId, learnings -> {
            if (learnings == null || learnings.isEmpty()) {
                Learning newLearning = new Learning();
                newLearning.deckId = deckId;
                newLearning.currentCardIndex = 0;
                newLearning.hardCardIndexes = new ArrayList<>();
                newLearning.lastLearnt = new GregorianCalendar();
                LearningService.getInstance().insert(newLearning, () -> {
                    Log.d("DB", "Learning inserted");
                });
            } else {
                learning = learnings.get(0);
                Log.d("DB", "Existing learning found: " + learnings.size());
            }
        });


        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "Front 1", "Back 1", 1, 1));
        cards.add(new Card(2, "Front 2", "Back 2", 2, 1));
        cards.add(new Card(3, "Front 3", "Back 3", 3, 1));
        cards.add(new Card(4, "Front 4 Front 5 lorem iptsum dolor", "Back 14", 4, 1));
        cards.add(new Card(4, "Front 5 lorem iptsum dolor Front 5 lorem iptsum dolorFront 5 lorem iptsum dolor", "Back 5", 5, 1));

        progressBar.setProgress((int) ((1.0f / cards.size()) * 100));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null)
                {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION)
                    {
                        int progress = (int) (((firstVisibleItemPosition + 1.0f) / cards.size()) * 100);
                        progressBar.setProgress(progress);
                    }
                }

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // Scroll stopped
                    if(!isRelearning) {
                        View snappedView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                        if (snappedView != null) {
                            int snappedPosition = recyclerView.getChildAdapterPosition(snappedView);
                            LearningService.getInstance().updateCurrentCardIndex(learning.id, snappedPosition, () -> {

                            });
                        }
                    }
                }
            }
        });

        adapter = new CardLearningAdapter(
            cards, new CardLearningAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(Card card)
                {
                    // Handle item click
                }

            },
            new CardLearningAdapter.OnMarkClickListener()
            {
                @Override
                public void onMarkClick(Card card)
                {
                    learning.hardCardIndexes.add(card.index);
                    LearningService.getInstance().updateHardIndexes(learning.id, learning.hardCardIndexes, () -> {
                        runOnUiThread(() ->{
                            Toast.makeText(DeckLearningActivity.this, "Card marked", Toast.LENGTH_SHORT).show();
                        });
                    });
                }
            },
            learning.hardCardIndexes
        );
        recyclerView.setAdapter(adapter);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());

        Button finishButton = findViewById(R.id.btnFinish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRelearning){
                    ShowConfirmRelearn();
                }
                else{
                    finish();

                }
            }
        });
    }

    private void ShowConfirmRelearn()
    {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Finish learning")
            .setMessage("Do you want to relearn marked cards?")
            .setPositiveButton(
                "Yes", (dialog, which) ->
                {
                    List<Card> RelearnCards = new ArrayList<>();
                    for(int i: learning.hardCardIndexes){
                        RelearnCards.add(cards.get(i));
                    }
                    adapter.updateData(RelearnCards);
                }
            )
            .setNegativeButton(
                "No", (dialog, which) ->
                {
                    this.finish();
                }
            )
            .show();
    }
}