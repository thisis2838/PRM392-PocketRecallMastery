package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.dtos.decks.DeckCreationDTO;
import com.prm392g2.prmapp.services.DecksService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeckCreationActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deck_creation);
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main), (v, insets) ->
            {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        );

        EditText editName = findViewById(R.id.editName);
        EditText editDescription = findViewById(R.id.editDescription);
        Button butAdd = findViewById(R.id.butAdd);

        butAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = editName.getText().toString().trim();
                String description = editDescription.getText().toString().trim();

                if (name.isEmpty())
                {
                    Toast.makeText(DeckCreationActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                DeckCreationDTO dto = new DeckCreationDTO();
                dto.name = name;
                dto.description = description;

                DecksService.getInstance().create(
                    dto, new Callback<Integer>()
                    {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                runOnUiThread(() ->
                                {
                                    Toast.makeText(DeckCreationActivity.this, "Deck created!", Toast.LENGTH_SHORT).show();
                                    // go to deck edit activity
                                    int deckId = response.body();
                                    Intent intent = new Intent(DeckCreationActivity.this, DeckEditingActivity.class);
                                    intent.putExtra("deckId", deckId);
                                    startActivity(intent);
                                    finish();
                                });
                            }
                            else
                            {
                                runOnUiThread(() -> Toast.makeText(DeckCreationActivity.this, "Failed to create deck", Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t)
                        {
                            runOnUiThread(() -> Toast.makeText(DeckCreationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    }
                );
            }
        });
    }
}