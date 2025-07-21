package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.dtos.cards.CardDetailDTO;
import com.prm392g2.prmapp.entities.Card;

import java.util.ArrayList;
import java.util.List;

public class CardMainAdapter extends RecyclerView.Adapter<CardMainAdapter.CardViewHolder>
{
    private List<CardDetailDTO> cards;
    private List<Boolean> isFlipped = new ArrayList<>();

    public interface OnItemClickListener
    {
        void onItemClick(Card card);
    }

    private OnItemClickListener listener;

    public CardMainAdapter(List<CardDetailDTO> cards, OnItemClickListener listener)
    {
        this.cards = cards;
        this.listener = listener;
        for (int i = 0; i < cards.size(); i++)
        {
            isFlipped.add(false);
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_card_main, parent, false);
        return new CardViewHolder(view);
    }

    static class CardViewHolder extends RecyclerView.ViewHolder
    {
        TextView question;
        TextView answer;
        TextView index;

        public CardViewHolder(View itemView)
        {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            index = itemView.findViewById(R.id.textIndex);
        }
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position)
    {
        var card = cards.get(position);
        holder.question.setText(card.front);
        holder.answer.setText(card.back);
        holder.itemView.setOnClickListener(v ->
        {
            isFlipped.set(position, !isFlipped.get(position));
            notifyItemChanged(position);
        });
        if (isFlipped.get(position))
        {
            holder.question.setVisibility(View.GONE);
            holder.answer.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.question.setVisibility(View.VISIBLE);
            holder.answer.setVisibility(View.GONE);
        }

        holder.index.setText(Integer.toString(card.index));
    }

    @Override
    public int getItemCount()
    {
        return cards.size();
    }
}
