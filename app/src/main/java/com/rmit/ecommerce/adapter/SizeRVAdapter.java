package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.SizeModel;

import java.util.ArrayList;

public class SizeRVAdapter extends RecyclerView.Adapter<SizeRVAdapter.ViewHolder> {
    ArrayList<SizeModel> sizes;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView sizeLabel;
        TextView quantity;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            sizeLabel = itemView.findViewById(R.id.sizeLabel);
            quantity = itemView.findViewById(R.id.quantity);
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public TextView getSizeLabel() {
            return sizeLabel;
        }

        public TextView getQuantity() {
            return quantity;
        }
    }

    public SizeRVAdapter(ArrayList<SizeModel> sizes) {
        this.sizes = sizes;
    }
    public SizeRVAdapter() {
        this.sizes = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_size, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialCardView materialCardView = holder.getCardView();
        TextView sizeLabel = holder.getSizeLabel();
        TextView quantity =  holder.getQuantity();

        SizeModel sizeModel = sizes.get(position);
        sizeLabel.setText(sizeModel.getSizeLabel());
        quantity.setText(Integer.toString(sizeModel.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }
}
