package com.prm392g2.prmapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckDetailActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicDeckFragment extends Fragment {

    public RecyclerView recyclerView;
    public DeckListAdapter adapter;
    public List<Deck> decks = new ArrayList<>();
    {
        decks.add(new Deck(1, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2023, 4, 1)));
        decks.add(new Deck(2, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2022, 4, 1)));
        decks.add(new Deck(3, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2021, 4, 1)));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_deck, container, false);
        recyclerView = view.findViewById(R.id.deck_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeckListAdapter(decks,
            new DeckListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Deck deck) {
                    Intent intent = new Intent(getActivity(), DeckDetailActivity.class);
                    intent.putExtra("deckId", deck.id);
                    startActivity(intent);
                }
            }
        );
        recyclerView.setAdapter(adapter);

        MaterialButton filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        return view;
    }

    private void getDecks(DeckListArgumentsDTO arguments) {
        DeckApi api = ApiClient.getClient().create(DeckApi.class);
        Call<DeckListDTO> call = api.getPublicDecks(arguments);

        call.enqueue(new Callback<DeckListDTO>() {
            @Override
            public void onResponse(Call<DeckListDTO> call, Response<DeckListDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    DeckListDTO deckListDTO = response.body();
                    decks.clear();
                    adapter.updateData(deckListDTO.Decks);
                }else{

                }
            }

            @Override
            public void onFailure(Call<DeckListDTO> call, Throwable t) {

            }
        });
    }

    private void showFilterDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);

        // Get references to dialog widgets
        RadioGroup radioGroupSortBy = dialogView.findViewById(R.id.radioGroupSortBy);
        RadioGroup radioGroupSortOrder = dialogView.findViewById(R.id.radioGroupSortOrder);
        EditText editTextMinCardCount = dialogView.findViewById(R.id.editTextMinCardCount);
        EditText editTextMaxCardCount = dialogView.findViewById(R.id.editTextMaxCardCount);
        Button buttonApply = dialogView.findViewById(R.id.buttonApply);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonReset = dialogView.findViewById(R.id.buttonReset);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        buttonApply.setOnClickListener(v -> {
            // Get sort by
            int sortById = radioGroupSortBy.getCheckedRadioButtonId();
            boolean sortByName = sortById == R.id.radioButtonSortByName;

            // Get sort order
            int sortOrderId = radioGroupSortOrder.getCheckedRadioButtonId();
            boolean ascending = sortOrderId == R.id.radioButtonAscending;

            // Get min/max card count
            String minStr = editTextMinCardCount.getText().toString();
            String maxStr = editTextMaxCardCount.getText().toString();
            int min = minStr.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(minStr);
            int max = maxStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxStr);

            // Filter and sort decks
            List<Deck> filtered = new ArrayList<>();
            for (Deck deck : decks) {
                int cardCount = deck.cardCount; // Replace with your actual card count property if different
                if (cardCount >= min && cardCount <= max) {
                    filtered.add(deck);
                }
            }
            // Sort
            if (sortByName) {
                filtered.sort((d1, d2) -> ascending ?
                    d1.name.compareToIgnoreCase(d2.name) :
                    d2.name.compareToIgnoreCase(d1.name));
            } else {
                filtered.sort((d1, d2) -> ascending ?
                    Integer.compare(d1.cardCount, d2.cardCount) :
                    Integer.compare(d2.cardCount, d1.cardCount));
            }

            adapter.updateData(filtered); // You may need to implement updateData in your adapter
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonReset.setOnClickListener(v -> {
            // Reset all fields
            radioGroupSortBy.check(R.id.radioButtonSortByName);
            radioGroupSortOrder.check(R.id.radioButtonAscending);
            editTextMinCardCount.setText("");
            editTextMaxCardCount.setText("");
        });

        dialog.show();
    }
}
