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

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.DeckAuthoringActivity;
import com.prm392g2.prmapp.adapters.DeckListAdapter;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.services.DecksService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDecksFragment extends Fragment
{
    private RecyclerView recyclerView;
    private DeckListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_decks, container, false);
        recyclerView = view.findViewById(R.id.recylerMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeckListAdapter(
            new ArrayList<>(), deck ->
        {
            // Handle deck click if needed
        }
        );
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), DeckAuthoringActivity.class);
                startActivity(intent);
            }
        });

        loadMyDecks();

        return view;
    }

    private void loadMyDecks()
    {
        DecksService.getInstance().getMyDecks(new Callback<DeckListDTO>()
        {
            @Override
            public void onResponse(Call<DeckListDTO> call, Response<DeckListDTO> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    requireActivity().runOnUiThread(() -> adapter.updateData(response.body().decks));
                }
                else
                {
                    requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "No decks found.", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<DeckListDTO> call, Throwable t)
            {
                requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "Failed to load decks.", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}