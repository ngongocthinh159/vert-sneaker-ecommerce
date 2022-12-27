package com.rmit.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    String[] mDataSet;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView productImage;
        TextView productBranch;
        TextView productName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            productImage = itemView.findViewById(R.id.productImage);
            productBranch = itemView.findViewById(R.id.productBranch);
            productName = itemView.findViewById(R.id.productName);
        }

        public ImageView getProductImage() {
            return productImage;
        }

        public TextView getProductBranch() {
            return productBranch;
        }

        public TextView getProductName() {
            return productName;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }
    }

    public MyRecyclerViewAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_shoe, parent, false);

        context = parent.getContext();

        return new ViewHolder(v);
    }

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        // Resize width for card view (responsive)
        float cardWidthPixel = (MainActivity.device_width_pxl - Helper.convertDpToPixel(9*3, context)) / 2;
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        params.width = (int) cardWidthPixel;
        holder.getCardView().setLayoutParams(params);

//        holder.getProductBranch().setText(mDataSet[position]);
//        holder.getProductName().setText(mDataSet[position]);

    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
