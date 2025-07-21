package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.dtos.cards.CardDetailDTO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CardEditingAdapter extends RecyclerView.Adapter<CardEditingAdapter.CardViewHolder>
{
    public interface OnCardClickListener
    {
        void onCardClick(CardDetailDTO card);
    }

    private List<CardDetailDTO> cards;
    private OnCardClickListener cardClickListener;

    public CardEditingAdapter(List<CardDetailDTO> cards)
    {
        this.cards = cards;
    }

    public void setOnCardClickListener(OnCardClickListener listener)
    {
        this.cardClickListener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_card_authoring, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position)
    {
        CardDetailDTO card = cards.get(position);
        holder.bind(card);
        holder.itemView.setOnClickListener(v ->
        {
            if (cardClickListener != null)
            {
                cardClickListener.onCardClick(card);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return cards != null ? cards.size() : 0;
    }

    public void updateData(List<CardDetailDTO> newCards)
    {
        this.cards = newCards.stream()
            .sorted(Comparator.comparingInt(x -> x.index))
            .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder
    {
        private CardDetailDTO card;
        TextView textCardIndex, textCardFront, textCardBack;

        CardViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textCardIndex = itemView.findViewById(R.id.textCardIndex);
            textCardFront = itemView.findViewById(R.id.textCardFront);
            textCardBack = itemView.findViewById(R.id.textCardBack);
        }

        public void bind(CardDetailDTO card)
        {
            this.card = card;
            refresh();
        }

        public void refresh()
        {
            if (card != null)
            {
                textCardIndex.setText(String.valueOf(card.index));
                textCardFront.setText(card.front);
                textCardBack.setText(card.back);
            }
        }
    }
}