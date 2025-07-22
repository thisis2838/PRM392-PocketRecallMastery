package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.database.entities.Card;

import java.util.ArrayList;
import java.util.List;

public class CardLearningAdapter extends RecyclerView.Adapter<CardLearningAdapter.CardLearningViewHolder>
{
    private List<Card> cards;
    private List<Boolean> isFlipped = new ArrayList<>();

    public interface OnItemClickListener
    {
        void onItemClick(Card card);
    }

    private OnItemClickListener listener;

    public interface OnMarkClickListener
    {
        void onMarkClick(Card card);
    }

    private OnMarkClickListener markClickListener;

    public CardLearningAdapter(List<Card> cards, OnItemClickListener listener, OnMarkClickListener markClickListener)
    {
        this.cards = cards;
        this.listener = listener;
        this.markClickListener = markClickListener;
        for (int i = 0; i < cards.size(); i++)
        {
            isFlipped.add(false);
        }
    }

    @Override
    public CardLearningViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_card_learning, parent, false);
        return new CardLearningViewHolder(view);
    }

    static class CardLearningViewHolder extends RecyclerView.ViewHolder
    {
        TextView question;
        TextView answer;
        MaterialButton btnBeginLearningMark;

        public CardLearningViewHolder(View itemView)
        {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            btnBeginLearningMark = itemView.findViewById(R.id.btnBeginLearningMark);
        }
    }

    @Override
    public void onBindViewHolder(CardLearningViewHolder holder, int position)
    {
        Card card = cards.get(position);
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
        holder.btnBeginLearningMark.setOnClickListener(v ->
        {
            markClickListener.onMarkClick(card);
            holder.btnBeginLearningMark.setIconResource(R.drawable.ic_star_filled);
        });
    }

    @Override
    public int getItemCount()
    {
        return cards.size();
    }
}
