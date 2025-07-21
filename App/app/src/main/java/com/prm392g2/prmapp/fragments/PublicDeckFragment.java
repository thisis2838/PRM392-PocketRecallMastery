package com.prm392g2.prmapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckDetailActivity;
import com.prm392g2.prmapp.activities.RegisterActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListMetric;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicDeckFragment extends Fragment
{

    public RecyclerView recyclerView;
    public DeckListAdapter adapter;
    public List<DeckSummaryDTO> decks = new ArrayList<>();
    /*
    {
        decks.add(new Deck(1, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2023, 4, 1)));
        decks.add(new Deck(2, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2022, 4, 1)));
        decks.add(new Deck(3, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2021, 4, 1)));
    }
     */
    EditText searchtxt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_public_deck, container, false);
        recyclerView = view.findViewById(R.id.deck_list);
        searchtxt = view.findViewById(R.id.search_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeckListAdapter(
            decks,
            new DeckListAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(DeckSummaryDTO deck)
                {
                    Intent intent = new Intent(getActivity(), DeckDetailActivity.class);
                    intent.putExtra("deckId", deck.id);
                    startActivity(intent);
                }
            }
        );
        recyclerView.setAdapter(adapter);
        getDecks(null, null, null, DeckListMetric.Name, true);
        MaterialButton filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showFilterDialog();
            }
        });
        return view;
    }

    private void getDecks(String search, Integer minCardCount, Integer maxCardCount, DeckListMetric sortingMetric, boolean sortingAscending) {
        DeckApi api = ApiClient.getClient().create(DeckApi.class);
        Call<DeckListDTO> call = api.getPublicDecks(search, minCardCount, maxCardCount, sortingMetric.toString(), sortingAscending);

        call.enqueue(new Callback<DeckListDTO>() {
            @Override
            public void onResponse(Call<DeckListDTO> call, Response<DeckListDTO> response) {
                if(response.isSuccessful() && response.body() != null){
                    DeckListDTO deckListDTO = response.body();
                    decks.clear();
                    adapter.updateData(deckListDTO.decks);
                }else{
                    String errorMessage = "Registration failed: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    searchtxt.setText(errorMessage);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DeckListDTO> call, Throwable t)
            {
                searchtxt.setText(t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showFilterDialog()
    {
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
            //Get search
            String query = searchtxt.getText().toString();
            // Get sort by
            int sortById = radioGroupSortBy.getCheckedRadioButtonId();
            DeckListMetric sortingMetric;
            if (sortById == R.id.radioButtonSortByName) {
                sortingMetric = DeckListMetric.Name;
            } else if (sortById == R.id.radioButtonSortByView) {
                sortingMetric = DeckListMetric.View;
            } else if (sortById == R.id.radioButtonSortByDownload) {
                sortingMetric = DeckListMetric.Download;
            }
            else {
                sortingMetric = DeckListMetric.Name;
            }

            // Get sort order
            int sortOrderId = radioGroupSortOrder.getCheckedRadioButtonId();
            boolean ascending = sortOrderId == R.id.radioButtonAscending;

            // Get min/max card count
            String minStr = editTextMinCardCount.getText().toString();
            String maxStr = editTextMaxCardCount.getText().toString();
            int min = minStr.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(minStr);
            int max = maxStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxStr);

            getDecks(query, min, max, sortingMetric, ascending);

            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonReset.setOnClickListener(v ->
        {
            // Reset all fields
            radioGroupSortBy.check(R.id.radioButtonSortByName);
            radioGroupSortOrder.check(R.id.radioButtonAscending);
            editTextMinCardCount.setText("");
            editTextMaxCardCount.setText("");
        });

        dialog.show();
    }
}
