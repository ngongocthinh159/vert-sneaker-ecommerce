package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.repository.SneakerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeItemRVAdapter extends RecyclerView.Adapter<HomeItemRVAdapter.ViewHolder> {

    ArrayList<SneakerModel> sneakers;
    String category;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView productImage;
        TextView productBranch;
        TextView productName;
        TextView productPrice;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            productImage = itemView.findViewById(R.id.productImage);
            productBranch = itemView.findViewById(R.id.productBranch);
            productName = itemView.findViewById(R.id.productName);
            progressBar = itemView.findViewById(R.id.progressBar);
            productPrice = itemView.findViewById(R.id.productPrice);
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

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public TextView getProductPrice() {
            return productPrice;
        }
    }

    public HomeItemRVAdapter(ArrayList<SneakerModel> sneakers, String category) {
        this.sneakers = sneakers;
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
    public void onBindViewHolder(@NonNull HomeItemRVAdapter.ViewHolder holder, int position) {
        // Get references to View
        MaterialCardView cardView =  holder.getCardView();
        ImageView productImage = holder.getProductImage();
        TextView productBranch = holder.getProductBranch();
        TextView productName = holder.getProductName();
        TextView productPrice = holder.getProductPrice();
        ProgressBar progressBar = holder.getProgressBar();

        // Resize width for card view (responsive)
        float cardWidthPixel = (MainActivity.device_width_pxl - Helper.convertDpToPixel(9*3, context)) / 2;
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        params.width = (int) cardWidthPixel;
        cardView.setLayoutParams(params);

        // Setup card view touch action
        Bundle bundle = new Bundle();
        bundle.putString("pid", sneakers.get(position).getId());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_global_productDetailFragment, bundle);
            }
        });

        // Setup card view color (best seller category)
        if (category.equals("best_seller") && position == 1) {
            cardView.setCardBackgroundColor(Color.parseColor("#6c8694"));
            productBranch.setTextColor(Color.parseColor("#FFFFFF"));
            productName.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // Fetch text data to app
        productBranch.setText(sneakers.get(position).getBrand());
        productName.setText(sneakers.get(position).getTitle());
        double temp_price = sneakers.get(position).getPrice();
        String text_price = "";
        if (Math.floor(temp_price) == Math.ceil(temp_price)) text_price = String.valueOf((int) temp_price);
        productPrice.setText("$" + text_price);

        // Fetch image data
        String imageStr = sneakers.get(position).getImage();
        FirebaseStorage db = FirebaseStorage.getInstance();
        if (sneakers.get(position).getFigureImage() != null) {
            Picasso.get().load(sneakers.get(position).getFigureImage()).into(productImage);
        } else {
            productImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (imageStr != null && !imageStr.isEmpty() && productImage.getDrawable() == null) {
                db.getReferenceFromUrl(imageStr).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        productImage.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        sneakers.get(position).setFigureImage(uri);
                                        Picasso.get().load(uri).into(productImage);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Uh-oh, an error occurred!
                            }
                        });
            }
        }
    }

    @Override
    public int getItemCount() {
        return sneakers.size();
    }
}
