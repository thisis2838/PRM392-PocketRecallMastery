package com.prm392g2.prmapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.entities.Card;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.entities.User;

import java.util.List;

public class DeckListAdapter extends  RecyclerView.Adapter<DeckListAdapter.ViewHolder>{
    private final List<Deck> decks;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Deck deck);
    }

    public DeckListAdapter(List<Deck> decks, OnItemClickListener listener) {
        this.decks = decks;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deck deck = decks.get(position);
        User creator = PRMDatabase.getInstance().userDao().getById(deck.creatorId);
        List<Card> cards = PRMDatabase.getInstance().cardDao().getByDeckId(deck.id);
        holder.deckName.setText(deck.name);
        holder.cardCount.setText(String.valueOf(cards.size()));
        holder.username.setText(creator.username);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView deckName;
        public TextView cardCount;
        public TextView username;
        public ShapeableImageView profileImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.txtDeckName);
            cardCount = itemView.findViewById(R.id.txtCardCount);
            username = itemView.findViewById(R.id.txtUsername);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }
}
