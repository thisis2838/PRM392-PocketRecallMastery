package com.prm392g2.prmapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.entities.Card;

import java.util.ArrayList;
import java.util.List;

public class CardDetailAdapter extends RecyclerView.Adapter<CardDetailAdapter.CardDetailViewHolder> {
    private List<Card> cards = new ArrayList<>();
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Card card);
    }
    public CardDetailAdapter(List<Card> cards, OnItemClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }
    @Override
    public CardDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_detail, parent, false);
        return new CardDetailViewHolder(view);
    }
    @Override
    public void onBindViewHolder(CardDetailViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.txtDetailFront.setText(card.front);
        holder.txtDetailBack.setText(card.back);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(card));
    }
    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class CardDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDetailFront;
        private TextView txtDetailBack;
        private MaterialButton btnDetailMark;
        private View divider;
        public CardDetailViewHolder(View itemView) {
            super(itemView);
            txtDetailFront = itemView.findViewById(R.id.txtDetailFront);
            txtDetailBack = itemView.findViewById(R.id.txtDetailBack);
            btnDetailMark = itemView.findViewById(R.id.btnDetailMark);
        }
    }
}
