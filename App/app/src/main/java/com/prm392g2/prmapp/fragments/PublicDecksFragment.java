package com.prm392g2.prmapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckDetailActivity;
import com.prm392g2.prmapp.activities.DeckEditingActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListMetric;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.services.DecksService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicDecksFragment extends Fragment
{
    public RecyclerView recyclerView;
    public DeckListAdapter adapter;
    public List<DeckSummaryDTO> decks = new ArrayList<>();
    private DeckListArgumentsDTO arguments = new DeckListArgumentsDTO();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_public_decks, container, false);
        recyclerView = view.findViewById(R.id.deck_list);

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

                @Override
                public void onEditClick(DeckSummaryDTO deck)
                {
                    Intent intent = new Intent(getActivity(), DeckEditingActivity.class);
                    intent.putExtra("deckId", deck.id);
                    startActivity(intent);
                }
            }
        );
        recyclerView.setAdapter(adapter);

        MaterialButton filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterDialog());

        EditText searchBox = view.findViewById(R.id.search_bar);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    arguments.search = v.getText().toString();
                    getDecks(arguments);
                    return true; // Consume the event
                }

                return false;
            }
        });

        getDecks(arguments);
        return view;
    }

    private void getDecks(DeckListArgumentsDTO arguments)
    {
        DecksService.getInstance().getPublic(
            arguments, new Callback<DeckListDTO>()
            {
                @Override
                public void onResponse(Call<DeckListDTO> call, Response<DeckListDTO> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        DeckListDTO deckListDTO = response.body();
                        decks.clear();
                        decks.addAll(deckListDTO.decks);
                        adapter.updateData(deckListDTO.decks);
                    }
                }

                @Override
                public void onFailure(Call<DeckListDTO> call, Throwable t)
                {
                    // Handle error
                }
            }
        );
    }

    private void showFilterDialog()
    {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);

        RadioGroup radioGroupSortBy = dialogView.findViewById(R.id.radioGroupSortBy);
        RadioGroup radioGroupSortOrder = dialogView.findViewById(R.id.radioGroupSortOrder);
        EditText editTextMinCardCount = dialogView.findViewById(R.id.editTextMinCardCount);
        EditText editTextMaxCardCount = dialogView.findViewById(R.id.editTextMaxCardCount);
        Button buttonApply = dialogView.findViewById(R.id.buttonApply);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonReset = dialogView.findViewById(R.id.buttonReset);

        // Set current values
        if (arguments.minCardCount != null)
            editTextMinCardCount.setText(String.valueOf(arguments.minCardCount));
        if (arguments.maxCardCount != null)
            editTextMaxCardCount.setText(String.valueOf(arguments.maxCardCount));
        radioGroupSortBy.check(arguments.sortingMetric == DeckListMetric.Name ? R.id.radioButtonSortByName :
            arguments.sortingMetric == DeckListMetric.View ? R.id.radioButtonSortByView : R.id.radioButtonSortByDownload);
        radioGroupSortOrder.check(arguments.sortingAscending ? R.id.radioButtonAscending : R.id.radioButtonDescending);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setView(dialogView)
            .create();

        buttonApply.setOnClickListener(v ->
        {
            // Sort by
            int sortById = radioGroupSortBy.getCheckedRadioButtonId();
            if (sortById == R.id.radioButtonSortByName)
                arguments.sortingMetric = DeckListMetric.Name;
            else if (sortById == R.id.radioButtonSortByView)
                arguments.sortingMetric = DeckListMetric.View;
            else if (sortById == R.id.radioButtonSortByDownload)
                arguments.sortingMetric = DeckListMetric.Download;

            // Sort order
            arguments.sortingAscending = radioGroupSortOrder.getCheckedRadioButtonId() == R.id.radioButtonAscending;

            // Min/max card count
            String minStr = editTextMinCardCount.getText().toString();
            String maxStr = editTextMaxCardCount.getText().toString();
            arguments.minCardCount = minStr.isEmpty() ? null : Integer.parseInt(minStr);
            arguments.maxCardCount = maxStr.isEmpty() ? null : Integer.parseInt(maxStr);

            getDecks(arguments);
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonReset.setOnClickListener(v ->
        {
            radioGroupSortBy.check(R.id.radioButtonSortByName);
            radioGroupSortOrder.check(R.id.radioButtonAscending);
            editTextMinCardCount.setText("");
            editTextMaxCardCount.setText("");
            arguments = new DeckListArgumentsDTO();
        });

        dialog.show();
    }
}