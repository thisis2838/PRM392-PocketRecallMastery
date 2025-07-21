package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;

import java.util.List;

public class DeckListAdapter extends RecyclerView.Adapter<DeckListAdapter.ViewHolder>
{
    private final List<DeckSummaryDTO> decks;
    private OnItemClickListener listener;

    public interface OnItemClickListener
    {
        void onItemClick(DeckSummaryDTO deck);
    }

    public DeckListAdapter(List<DeckSummaryDTO> decks, OnItemClickListener listener)
    {
        this.decks = decks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        DeckSummaryDTO deck = decks.get(position);

        holder.deckName.setText(deck.name);
        holder.cardCount.setText(String.valueOf(deck.cardsCount) + " cards");
        holder.username.setText(deck.creator.username);
        holder.version.setText("Version " + String.valueOf(deck.version));
        holder.viewCount.setText(String.valueOf(deck.viewsTotal));
        holder.downloadCount.setText(String.valueOf(deck.downloadsTotal));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.onItemClick(deck);
            }
        });
    }

    public void updateData(List<DeckSummaryDTO> newDecks)
    {
        decks.clear();
        decks.addAll(newDecks);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView deckName;
        public TextView cardCount;
        public TextView username;
        public TextView version;
        public TextView viewCount;
        public TextView downloadCount;
        public ShapeableImageView profileImage;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            deckName = itemView.findViewById(R.id.txtDeckName);
            cardCount = itemView.findViewById(R.id.txtCardCount);
            username = itemView.findViewById(R.id.txtUsername);
            profileImage = itemView.findViewById(R.id.profile_image);
            version = itemView.findViewById(R.id.txtVersion);
            viewCount = itemView.findViewById(R.id.txtViewCount);
            downloadCount = itemView.findViewById(R.id.txtDownloadCount);
        }
    }

    @Override
    public int getItemCount()
    {
        return decks.size();
    }
}
