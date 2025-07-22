package com.prm392g2.prmapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckDetailActivity;
import com.prm392g2.prmapp.adapters.DeckSavedAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.services.SavedDecksService;

import java.util.ArrayList;
import java.util.List;

public class SavedDecksFragment extends Fragment
{
    private RecyclerView recSavedList;
    private DeckSavedAdapter adapter;
    private List<DeckSummaryDTO> decks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_saved_decks, container, false);

        recSavedList = view.findViewById(R.id.recSavedList);
        recSavedList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeckSavedAdapter(decks)
        {
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position)
            {
                super.onBindViewHolder(holder, position);
                holder.itemView.setOnClickListener(v ->
                {
                    DeckSummaryDTO deck = decks.get(position);
                    Intent intent = new Intent(getActivity(), DeckDetailActivity.class);
                    intent.putExtra("deckId", deck.id);
                    intent.putExtra("saved", true);
                    startActivity(intent);
                });
            }
        };
        recSavedList.setAdapter(adapter);

        loadDecks();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadDecks();
    }

    private void loadDecks()
    {
        SavedDecksService.getInstance().getAll(result ->
        {
            requireActivity().runOnUiThread(() ->
            {
                decks.clear();
                decks.addAll(result);
                adapter.notifyDataSetChanged();
            });
        });
    }
}