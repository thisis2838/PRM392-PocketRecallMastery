package com.prm392g2.prmapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckDetailActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.entities.Deck;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

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
        return view;
    }
}
