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
import com.prm392g2.prmapp.activities.DeckEditingActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.services.HomeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment
{
    private RecyclerView recTopDecks, recRecentDecks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recTopDecks = view.findViewById(R.id.recTopDecks);
        recTopDecks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recTopDecks.setVisibility(View.INVISIBLE);

        recRecentDecks = view.findViewById(R.id.recRecentDecks);
        recRecentDecks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recRecentDecks.setVisibility(View.INVISIBLE);

        final var clickListener = new DeckListAdapter.OnItemClickListener()
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
        };

        HomeService.getInstance().getWeeklyPopularDecks(new Callback<List<DeckSummaryDTO>>()
        {
            @Override
            public void onResponse(Call<List<DeckSummaryDTO>> call, Response<List<DeckSummaryDTO>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    requireActivity().runOnUiThread(() ->
                    {
                        recTopDecks.setVisibility(View.VISIBLE);
                        var adapter = new DeckListAdapter(response.body(), clickListener);
                        recTopDecks.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<DeckSummaryDTO>> call, Throwable t)
            {
            }
        });

        HomeService.getInstance().getRecentDecks(new Callback<List<DeckSummaryDTO>>()
        {
            @Override
            public void onResponse(Call<List<DeckSummaryDTO>> call, Response<List<DeckSummaryDTO>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    requireActivity().runOnUiThread(() ->
                    {
                        recRecentDecks.setVisibility(View.VISIBLE);
                        var adapter = new DeckListAdapter(response.body(), clickListener);
                        recRecentDecks.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<DeckSummaryDTO>> call, Throwable t)
            {
            }
        });

        return view;
    }
}