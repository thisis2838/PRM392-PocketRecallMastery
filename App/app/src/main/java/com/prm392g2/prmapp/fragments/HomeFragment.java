package com.prm392g2.prmapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.prm392g2.prmapp.services.UserWeeklyStatsService;

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

        TextView textTopDecksError = view.findViewById(R.id.textTopDecksError);
        textTopDecksError.setVisibility(View.GONE);

        TextView textRecentDecksError = view.findViewById(R.id.textRecentDecksError);
        textRecentDecksError.setVisibility(View.GONE);

        updateWeeklyStats();

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
                        textTopDecksError.setVisibility(View.GONE);
                        var adapter = new DeckListAdapter(response.body(), clickListener);
                        recTopDecks.setAdapter(adapter);
                    });
                }
                else
                {
                    requireActivity().runOnUiThread(() ->
                    {
                        recTopDecks.setVisibility(View.INVISIBLE);
                        textTopDecksError.setVisibility(View.VISIBLE);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<DeckSummaryDTO>> call, Throwable t)
            {
                requireActivity().runOnUiThread(() ->
                {
                    recTopDecks.setVisibility(View.INVISIBLE);
                    textTopDecksError.setVisibility(View.VISIBLE);
                });
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
                        textRecentDecksError.setVisibility(View.GONE);
                        var adapter = new DeckListAdapter(response.body(), clickListener);
                        recRecentDecks.setAdapter(adapter);
                    });
                }
                else
                {
                    requireActivity().runOnUiThread(() ->
                    {
                        recRecentDecks.setVisibility(View.INVISIBLE);
                        textRecentDecksError.setVisibility(View.VISIBLE);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<DeckSummaryDTO>> call, Throwable t)
            {
                requireActivity().runOnUiThread(() ->
                {
                    recRecentDecks.setVisibility(View.INVISIBLE);
                    textRecentDecksError.setVisibility(View.VISIBLE);
                });
            }
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateWeeklyStats();
    }

    private void updateWeeklyStats()
    {
        if (getView() == null)
        {
            return;
        }

        ((TextView) (getView().findViewById(R.id.textDecksViewed)))
            .setText(Integer.toString(UserWeeklyStatsService.getInstance().getDeckViews()));
        ((TextView) (getView().findViewById(R.id.textDecksLearned)))
            .setText(Integer.toString(UserWeeklyStatsService.getInstance().getDecksLearned()));
        ((TextView) (getView().findViewById(R.id.textCardsTurned)))
            .setText(Integer.toString(UserWeeklyStatsService.getInstance().getCardsTurned()));
    }
}