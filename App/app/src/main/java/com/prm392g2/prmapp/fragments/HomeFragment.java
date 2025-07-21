package com.prm392g2.prmapp.fragments;

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
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.entities.Deck;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeFragment extends Fragment
{
    RecyclerView topDeckList;

    public List<DeckSummaryDTO> decks = new ArrayList<>();
    /*
    {
        decks.add(new Deck(1, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2023, 4, 1)));
        decks.add(new Deck(2, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2022, 4, 1)));
        decks.add(new Deck(3, "English Vocabulary", "Learn common English words", 1, 1, new GregorianCalendar(2021, 4, 1)));
    }
    */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        topDeckList = view.findViewById(R.id.topDecksList);
        topDeckList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DeckListAdapter deckListAdapter = new DeckListAdapter(
            decks,
            new DeckListAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(DeckSummaryDTO deck)
                {

                }
            }
        );
        topDeckList.setAdapter(deckListAdapter);
        return view;
    }
}
