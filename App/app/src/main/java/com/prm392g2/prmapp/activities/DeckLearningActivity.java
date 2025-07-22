package com.prm392g2.prmapp.activities;

import android.os.Bundle;
import android.widget.Button;

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
import com.prm392g2.prmapp.database.entities.Card;

import java.util.ArrayList;
import java.util.List;

public class DeckLearningActivity extends AppCompatActivity
{
    LinearProgressIndicator progressBar;

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

        RecyclerView recyclerView = findViewById(R.id.card_list_learning);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "front 1", "back 1", 1, 1));
        ;
        cards.add(new Card(2, "front 2", "back 2", 2, 1));
        cards.add(new Card(3, "front 3", "back 3", 3, 1));
        cards.add(new Card(4, "front 4", "back 4", 4, 1));
        cards.add(new Card(5, "front 5", "back 5", 5, 1));

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

        CardLearningAdapter adapter = new CardLearningAdapter(
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

                }
            }
        );
        recyclerView.setAdapter(adapter);

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());

        Button finishButton = findViewById(R.id.btnFinish);
        finishButton.setOnClickListener(v -> ShowConfirmRelearn());
    }

    private void ShowConfirmRelearn()
    {
        this.finish();
        return;
        /*
        new MaterialAlertDialogBuilder(this)
            .setTitle("Finish learning")
            .setMessage("Do you want to relearn marked cards?")
            .setPositiveButton(
                "Yes", (dialog, which) ->
                {
                    // Handle positive button click
                }
            )
            .setNegativeButton(
                "No", (dialog, which) ->
                {
                    this.finish();
                }
            )
            .show();

         */
    }
}