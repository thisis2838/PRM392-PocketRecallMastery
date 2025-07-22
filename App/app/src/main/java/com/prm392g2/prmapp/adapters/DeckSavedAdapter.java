package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392g2.prmapp.PRMApplication;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DeckSavedAdapter extends RecyclerView.Adapter<DeckSavedAdapter.ViewHolder>
{
    private List<DeckSummaryDTO> decks;

    public DeckSavedAdapter(List<DeckSummaryDTO> decks)
    {
        this.decks = decks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_deck_saved, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        DeckSummaryDTO deck = decks.get(position);

        holder.txtUsername.setText(deck.creator != null ? deck.creator.username : "");
        holder.txtDeckName.setText(deck.name);
        holder.txtVersion.setText(String.valueOf(deck.version));
        holder.txtCardCount.setText(String.valueOf(deck.cardsCount));

        // Date: last learnt or creation
        SimpleDateFormat sdf = PRMApplication.GLOBAL_DATE_FORMAT;
        String dateStr;
        if (deck.learning != null && deck.learning.lastLearnt != null)
        {
            dateStr = sdf.format(deck.learning.lastLearnt.getTime());
        }
        else if (deck.getCreatedAt() != null)
        {
            dateStr = sdf.format(deck.getCreatedAt().getTime());
        }
        else
        {
            dateStr = "";
        }
        holder.txtDate.setText(dateStr);

        // Progress bar: show if learning exists
        if (deck.learning != null)
        {
            holder.progLearning.setVisibility(View.VISIBLE);
            int progress = deck.learning.currentCardIndex + 1;
            holder.progLearning.setMax(deck.cardsCount);
            holder.progLearning.setProgress(progress);
        }
        else
        {
            holder.progLearning.setVisibility(View.GONE);
        }

        // Profile image: set placeholder (custom loading can be added)
        holder.profileImage.setImageResource(R.drawable.profile_placeholder);
    }

    @Override
    public int getItemCount()
    {
        return decks != null ? decks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ShapeableImageView profileImage;
        TextView txtUsername, txtDeckName, txtVersion, txtCardCount, txtDate;
        ProgressBar progLearning;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtDeckName = itemView.findViewById(R.id.txtDeckName);
            txtVersion = itemView.findViewById(R.id.txtVersion);
            txtCardCount = itemView.findViewById(R.id.txtCardCount);
            txtDate = itemView.findViewById(R.id.txtDate);
            progLearning = itemView.findViewById(R.id.progLearning);
        }
    }

    public void updateData(List<DeckSummaryDTO> newDecks)
    {
        this.decks = newDecks;
        notifyDataSetChanged();
    }
}