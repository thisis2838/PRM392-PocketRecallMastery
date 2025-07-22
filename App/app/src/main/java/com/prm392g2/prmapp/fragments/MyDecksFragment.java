package com.prm392g2.prmapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckCreationActivity;
import com.prm392g2.prmapp.activities.DeckDetailActivity;
import com.prm392g2.prmapp.activities.DeckEditingActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.services.DecksService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDecksFragment extends Fragment
{
    private RecyclerView recyclerView;
    private DeckListAdapter adapter;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_decks, container, false);
        recyclerView = view.findViewById(R.id.recylerMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeckListAdapter(
            new ArrayList<>(),
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

        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), DeckCreationActivity.class);
                startActivity(intent);
            }
        });
        fabAdd.setVisibility(View.GONE);

        loadMyDecks();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Reload decks when fragment is resumed
        loadMyDecks();
    }

    private void loadMyDecks()
    {
        if (getView() == null || getActivity() == null)
        {
            return; // Avoid NullPointerException if activity is not available
        }

        var textListError = getView().findViewById(R.id.textListError);
        textListError.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);

        DecksService.getInstance().getMine(new Callback<DeckListDTO>()
        {
            @Override
            public void onResponse(Call<DeckListDTO> call, Response<DeckListDTO> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    requireActivity().runOnUiThread(() -> {
                        fabAdd.setVisibility(View.VISIBLE);
                        adapter.updateData(response.body().decks);
                    });
                }
                else
                {
                    requireActivity().runOnUiThread(() ->
                    {
                        textListError.setVisibility(View.VISIBLE);
                        fabAdd.setVisibility(View.GONE);
                    });
                }
            }

            @Override
            public void onFailure(Call<DeckListDTO> call, Throwable t)
            {
                requireActivity().runOnUiThread(() ->
                {
                    textListError.setVisibility(View.VISIBLE);
                    fabAdd.setVisibility(View.GONE);
                });
            }
        });
    }
}