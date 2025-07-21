package com.prm392g2.prmapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.adapters.CardEditingAdapter;
import com.prm392g2.prmapp.adapters.CardEditingAdapter.CardViewHolder;
import com.prm392g2.prmapp.dtos.cards.CardDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckEditDTO;
import com.prm392g2.prmapp.services.DecksService;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeckEditingActivity extends AppCompatActivity
{
    private CheckBox chkIsPublic;
    private EditText editName, editDescription;
    private RecyclerView recCards;
    private CardEditingAdapter cardAdapter;
    private List<CardDetailDTO> cards;
    private int deckId;
    private DeckDetailDTO deck;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_editing);
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main), (v, insets) ->
            {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        );

        deckId = getIntent().getIntExtra("deckId", -1);
        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        recCards = findViewById(R.id.recCards);
        Button butSave = findViewById(R.id.butSave);
        Button butDelete = findViewById(R.id.butDelete);
        Button butAddCard = findViewById(R.id.butAddCard);
        chkIsPublic = findViewById(R.id.chkIsPublic);

        Button back = findViewById(R.id.btn_back2);
        back.setOnClickListener(v -> finish());

        recCards.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardEditingAdapter(null /* will set data after load */);
        recCards.setAdapter(cardAdapter);

        // Drag-and-drop support
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                int from = viewHolder.getAbsoluteAdapterPosition();
                int to = target.getAbsoluteAdapterPosition();
                cards.get(from).index = to + 1;
                cards.get(to).index = from + 1;
                Collections.swap(cards, from, to);
                cardAdapter.notifyItemMoved(from, to);

                ((CardViewHolder)viewHolder).refresh();
                ((CardViewHolder)target).refresh();

                //updateCardIndices();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
            }
        });
        itemTouchHelper.attachToRecyclerView(recCards);

        // Card click to edit
        cardAdapter.setOnCardClickListener(card -> showEditCardDialog(card));

        // Load deck
        loadDeck();

        butAddCard.setOnClickListener(v -> showAddCardDialog());
        butSave.setOnClickListener(v -> saveDeck());
        butDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadDeck()
    {
        DecksService.getInstance().getById(
            deckId, new Callback<DeckDetailDTO>()
            {
                @Override
                public void onResponse(Call<DeckDetailDTO> call, Response<DeckDetailDTO> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        deck = response.body();
                        runOnUiThread(() ->
                        {
                            editName.setText(deck.name);
                            editDescription.setText(deck.description);
                            cards = deck.cards;
                            updateCardIndices();
                            cardAdapter.updateData(cards);
                            chkIsPublic.setChecked(deck.isPublic);
                        });
                    }
                    else
                    {
                        runOnUiThread(() -> Toast.makeText(DeckEditingActivity.this, "Failed to load deck", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<DeckDetailDTO> call, Throwable t)
                {
                    runOnUiThread(() -> Toast.makeText(DeckEditingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        );
    }

    private void saveDeck()
    {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        if (name.isEmpty())
        {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        DeckEditDTO dto = new DeckEditDTO();
        dto.name = name;
        dto.description = description;
        dto.isPublic = chkIsPublic.isChecked();
        dto.cards = cards;
        DecksService.getInstance().edit(
            deckId, dto, new Callback<Void>()
            {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response)
                {
                    runOnUiThread(() ->
                    {
                        if (response.isSuccessful())
                        {
                            Toast.makeText(DeckEditingActivity.this, "Deck saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(DeckEditingActivity.this, "Failed to save deck", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t)
                {
                    runOnUiThread(() -> Toast.makeText(DeckEditingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        );
    }

    private void confirmDelete()
    {
        new AlertDialog.Builder(this)
            .setTitle("Delete Deck")
            .setMessage("Are you sure you want to delete this deck?")
            .setPositiveButton("Delete", (dialog, which) -> deleteDeck())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteDeck()
    {
        DecksService.getInstance().delete(
            deckId, new Callback<Void>()
            {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response)
                {
                    runOnUiThread(() ->
                    {
                        Toast.makeText(DeckEditingActivity.this, "Deck deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t)
                {
                    runOnUiThread(() -> Toast.makeText(DeckEditingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        );
    }

    private void showAddCardDialog()
    {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_card, null);
        EditText editFront = dialogView.findViewById(R.id.editTextFront);
        EditText editBack = dialogView.findViewById(R.id.editTextBack);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonDelete = dialogView.findViewById(R.id.butDelete);
        buttonDelete.setVisibility(View.GONE);
        TextView title = dialogView.findViewById(R.id.textViewTitle);
        title.setText("Add Card");

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .create();

        buttonSave.setOnClickListener(v ->
        {
            String front = editFront.getText().toString().trim();
            String back = editBack.getText().toString().trim();
            if (!front.isEmpty() || !back.isEmpty())
            {
                CardDetailDTO newCard = new CardDetailDTO();
                newCard.front = front;
                newCard.back = back;
                newCard.index = cards.size() + 1;
                cards.add(newCard);
                updateCardIndices();
                cardAdapter.updateData(cards);
                dialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        //dialog.setTitle("Add Card");
        dialog.show();
    }

    private void showEditCardDialog(CardDetailDTO card)
    {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_card, null);
        EditText editFront = dialogView.findViewById(R.id.editTextFront);
        EditText editBack = dialogView.findViewById(R.id.editTextBack);
        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonDelete = dialogView.findViewById(R.id.butDelete);
        TextView title = dialogView.findViewById(R.id.textViewTitle);
        title.setText("Edit Card");

        editFront.setText(card.front);
        editBack.setText(card.back);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .create();

        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                card.front = editFront.getText().toString();
                card.back = editBack.getText().toString();
                cardAdapter.notifyDataSetChanged();
            }
        });
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cards.remove(card);
                updateCardIndices();
                cardAdapter.updateData(cards);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateCardIndices()
    {
        for (int i = 0; i < cards.size(); i++)
        {
            cards.get(i).index = i + 1;
        }
    }
}