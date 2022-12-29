package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    String[] mDataSet;
    String category;
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

        public MaterialCardView getCardView() {
            return cardView;
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
    }

    public MyRecyclerViewAdapter(String[] dataSet, String category) {
        this.mDataSet = dataSet;
        this.category = category;
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
        MaterialCardView cardView =  holder.getCardView();
        float cardWidthPixel = (MainActivity.device_width_pxl - Helper.convertDpToPixel(9*3, context)) / 2;
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        params.width = (int) cardWidthPixel;
        cardView.setLayoutParams(params);

        // Setup card view touch action
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_global_productDetailFragment);
            }
        });

        // Setup card view color
//        if (position == 0) {
//            cardView.setCardBackgroundColor(Color.parseColor("#67666f"));
//        }

        if (category.equals("best_seller") && position == 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#6c8694"));
            TextView productBranch = holder.getProductBranch();
            productBranch.setTextColor(Color.parseColor("#FFFFFF"));

            TextView productName = holder.getProductName();
            productName.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
