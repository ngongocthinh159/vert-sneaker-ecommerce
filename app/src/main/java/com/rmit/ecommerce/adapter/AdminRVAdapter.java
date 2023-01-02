package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.SneakerModel;

import java.util.ArrayList;

public class AdminRVAdapter extends RecyclerView.Adapter<AdminRVAdapter.ViewHolder> {
    ArrayList<SneakerModel> sneakers;
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

    public AdminRVAdapter() {
        sneakers = MainActivity.repositoryManager.getSneakers(AdminRVAdapter.this);
    }

    public AdminRVAdapter(ArrayList<SneakerModel> sneakers) {
        this.sneakers = sneakers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_shoe, parent, false);

        context = parent.getContext();

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRVAdapter.ViewHolder holder, int position) {
        // Get references to View
        MaterialCardView cardView =  holder.getCardView();
        ImageView productImage = holder.getProductImage();
        TextView productBranch = holder.getProductBranch();
        TextView productName = holder.getProductName();

        // Resize width for card view (responsive)
        float cardWidthPixel = (MainActivity.device_width_pxl - Helper.convertDpToPixel(9*3, context)) / 2;
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        params.width = (int) cardWidthPixel;
        cardView.setLayoutParams(params);

        // Setup card view touch action
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Navigate to Edit or size manager
                MainActivity.navController.navigate(R.id.action_homeAdminFragment_to_productManageFragment);
            }
        });

        productBranch.setText(sneakers.get(position).getBrand());
        productName.setText(sneakers.get(position).getTitle());
        String imageStr = sneakers.get(position).getImage();
        if (!imageStr.isEmpty()) {
            productImage.setImageDrawable(MainActivity.assetManager.fetchImage(imageStr, AdminRVAdapter.this));
        }
    }

    @Override
    public int getItemCount() {
        return sneakers.size();
    }


}
