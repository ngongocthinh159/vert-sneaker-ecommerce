package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.SizeModel;

import java.util.ArrayList;

public class SizeRVAdapter extends RecyclerView.Adapter<SizeRVAdapter.ViewHolder> {
    ArrayList<SizeModel> sizes;
    Context context;

    public ArrayList<SizeModel> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<SizeModel> sizes) {
        this.sizes = sizes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView sizeLabel;
        TextView quantity;
        MaterialButton minusQuantity;
        MaterialButton plusQuantity;
        MaterialButton deleteSize;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            sizeLabel = itemView.findViewById(R.id.sizeLabel);
            quantity = itemView.findViewById(R.id.quantity);
            minusQuantity = itemView.findViewById(R.id.minusQuantity);
            plusQuantity = itemView.findViewById(R.id.plusQuantity);
            deleteSize = itemView.findViewById(R.id.btnDeleteSize);

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

        public MaterialButton getMinusQuantity() {
            return minusQuantity;
        }

        public MaterialButton getPlusQuantity() {
            return plusQuantity;
        }

        public MaterialButton getDeleteSize() {
            return deleteSize;
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
        MaterialButton minusQuantity = holder.getMinusQuantity();
        MaterialButton plusQuantity = holder.getPlusQuantity();
        MaterialButton deleteSize = holder.getDeleteSize();
        SizeModel sizeModel = sizes.get(position);
        sizeLabel.setText(sizeModel.getSizeLabel());
        quantity.setText(Integer.toString(sizeModel.getQuantity()));

        plusQuantity.setOnClickListener(v -> {
            Integer newQuantity = sizes.get(position).getQuantity() + 1;
            sizes.get(position).setQuantity(newQuantity);
            quantity.setText(Integer.toString(newQuantity));
            if (minusQuantity.getVisibility() == View.GONE) {
                minusQuantity.setVisibility(View.VISIBLE);
            }
        });

        minusQuantity.setOnClickListener(v -> {
            Integer newQuantity = sizes.get(position).getQuantity() - 1;
            sizes.get(position).setQuantity(newQuantity);
            quantity.setText(Integer.toString(newQuantity));
            if (newQuantity == 0) {
                minusQuantity.setVisibility(View.GONE);
            } else {
                minusQuantity.setVisibility(View.VISIBLE);
            }
        });

        deleteSize.setOnClickListener(v -> {

        });



    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }
}
