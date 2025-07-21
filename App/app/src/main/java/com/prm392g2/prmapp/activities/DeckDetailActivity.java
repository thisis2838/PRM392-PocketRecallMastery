package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
import com.prm392g2.prmapp.entities.Card;

import java.util.ArrayList;
import java.util.List;

public class DeckDetailActivity extends AppCompatActivity {

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
        RecyclerView recyclerView = findViewById(R.id.card_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        List<Card> cards = new ArrayList<>();
        cards.add(new Card(1, "Front 1", "Back 1", 1, 1));;
        cards.add(new Card(2, "Front 2", "Back 2", 2, 1));
        cards.add(new Card(3, "Front 3", "Back 3", 3, 1));
        cards.add(new Card(4, "Front 4 Front 5 lorem iptsum dolor", "Back 14", 4, 1));
        cards.add(new Card(4, "Front 5 lorem iptsum dolor Front 5 lorem iptsum dolorFront 5 lorem iptsum dolor", "Back 5", 5, 1));

        CardMainAdapter adapter = new CardMainAdapter(cards, new CardMainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Card card) {
                // Handle item click
            }
        });
        recyclerView.setAdapter(adapter);

        Button btnBeginLearning = findViewById(R.id.btnBeginLearning);
        btnBeginLearning.setOnClickListener(v -> {
            Intent intent = new Intent(DeckDetailActivity.this, DeckLearningActivity.class);
//            intent.putExtra()
            startActivity(intent);
        });

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());
    }
}