package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.prm392g2.prmapp.PRMApplication;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.services.UsersService;

import java.util.List;

public class DeckListAdapter extends RecyclerView.Adapter<DeckListAdapter.ViewHolder>
{
    private final List<DeckSummaryDTO> decks;
    private OnItemClickListener listener;

    public interface OnItemClickListener
    {
        void onItemClick(DeckSummaryDTO deck);

        void onEditClick(DeckSummaryDTO deck);
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
        holder.cardCount.setText(String.valueOf(deck.cardsCount));
        holder.username.setText(deck.creator.username);
        holder.date.setText(PRMApplication.GLOBAL_DATE_FORMAT.format(deck.getCreatedAt().getTime()));
        holder.viewCount.setText(String.valueOf(deck.viewsTotal));
        holder.downloadCount.setText(String.valueOf(deck.downloadsTotal));

        if (UsersService.getInstance().isLoggedIn() && deck.creator.id == UsersService.getInstance().getUserId())
        {
            holder.butEdit.setVisibility(View.VISIBLE);
            holder.butEdit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.onEditClick(deck);
                }
            });

        }
        else
        {
            holder.butEdit.setVisibility(View.INVISIBLE);
            holder.butEdit.setOnClickListener(null);
        }

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
        public TextView date;
        public TextView viewCount;
        public TextView downloadCount;
        public ShapeableImageView profileImage;
        public MaterialButton butEdit;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            deckName = itemView.findViewById(R.id.txtDeckName);
            cardCount = itemView.findViewById(R.id.txtCardCount);
            username = itemView.findViewById(R.id.txtUsername);
            profileImage = itemView.findViewById(R.id.profile_image);
            date = itemView.findViewById(R.id.txtDate);
            viewCount = itemView.findViewById(R.id.txtViewCount);
            downloadCount = itemView.findViewById(R.id.txtDownloadCount);
            butEdit = itemView.findViewById(R.id.butEdit);
        }
    }

    @Override
    public int getItemCount()
    {
        return decks.size();
    }
}
