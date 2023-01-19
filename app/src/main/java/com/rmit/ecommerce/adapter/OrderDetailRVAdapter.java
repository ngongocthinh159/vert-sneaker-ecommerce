package com.rmit.ecommerce.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.fragment.ShoppingCartFragment;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.SneakerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderDetailRVAdapter extends RecyclerView.Adapter<OrderDetailRVAdapter.ViewHolder>{
    Context context;
    ArrayList<CartItemModel> cartItems;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView productImage;
        TextView productBranch;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        TextView productSize;
        ProgressBar progressBar;
        ProgressBar progressBarAllCartItem;
        LinearLayout layoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            productImage = itemView.findViewById(R.id.productImageItem);
            productBranch = itemView.findViewById(R.id.productBranch);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity= itemView.findViewById(R.id.productQuantity);
            productSize = itemView.findViewById(R.id.productSize);
            progressBar = itemView.findViewById(R.id.progressBarItem);
            progressBarAllCartItem = itemView.findViewById(R.id.progressBarAllCartItem);
            layoutItem = itemView.findViewById(R.id.layoutItem);
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

        public TextView getProductPrice() {
            return productPrice;
        }

        public TextView getProductQuantity() {
            return productQuantity;
        }

        public TextView getProductSize() {
            return productSize;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public ProgressBar getProgressBarAllCartItem() {
            return progressBarAllCartItem;
        }

        public LinearLayout getLayoutItem() {
            return layoutItem;
        }
    }

    public OrderDetailRVAdapter(ArrayList<CartItemModel> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_order_detail_item, parent, false);

        context = parent.getContext();

        return new OrderDetailRVAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get refs
        MaterialCardView cardView = holder.getCardView();
        ImageView productImage = holder.getProductImage();
        TextView productBranch = holder.getProductBranch();
        TextView productName = holder.getProductName();
        TextView productPrice = holder.getProductPrice();
        TextView productQuantity = holder.getProductQuantity();
        TextView productSize = holder.getProductSize();
        ProgressBar progressBar = holder.getProgressBar();
        ProgressBar progressBarAllCartItem =  holder.getProgressBarAllCartItem();
        LinearLayout layoutItem = holder.getLayoutItem();

        // Map text data to view

        int size = cartItems.get(position).getSize();
        cartItems.get(position).getPid().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // If the product is still exist
                if (documentSnapshot.exists()) {
                    SneakerModel sneakerModel = documentSnapshot.toObject(SneakerModel.class);
                    productBranch.setText(sneakerModel.getBrand());
                    productName.setText(sneakerModel.getTitle());
                    productPrice.setText("$" + String.valueOf(sneakerModel.getPrice()));
                    productQuantity.setText(String.valueOf(cartItems.get(position).getQuantity()));
                    productSize.setText("Size: " + String.valueOf(size));

                    // Hide progressbar
                    progressBarAllCartItem.setVisibility(View.GONE);
                    layoutItem.setVisibility(View.VISIBLE);
                } else {
                    String unavailable_text = "Unavailable";
                    productBranch.setText(unavailable_text);
                    productName.setText(unavailable_text);
                    productPrice.setText(unavailable_text);
                    productQuantity.setText("?");
                    productSize.setText(unavailable_text);
                    cardView.setCardBackgroundColor(Color.parseColor("#696969"));
                }
            }
        });

        // Map image to view
        // Find image link
        String pid = cartItems.get(position).getPid().getId();
        SneakerModel sneakerModel = null;
        String imageStr = null;
        for (SneakerModel sneaker : MainActivity.repositoryManager.getSneakers()) {
            if (sneaker.getId().equals(pid)) {
                imageStr = sneaker.getImage();
                sneakerModel = sneaker;
                break;
            }
        }
        // Fetch image into imageview
        FirebaseStorage db = FirebaseStorage.getInstance();
        if (sneakerModel != null && sneakerModel.getFigureImage() != null) { // If the url of the image already is already fetched
            Picasso.get().load(sneakerModel.getFigureImage()).into(productImage);
        } else {
            productImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (imageStr != null && !imageStr.isEmpty()) {
                SneakerModel finalSneakerModel = sneakerModel;
                db.getReferenceFromUrl(imageStr).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        productImage.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        Picasso.get().load(uri).into(productImage);
                                        finalSneakerModel.setFigureImage(uri);
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
        return cartItems.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public ArrayList<CartItemModel> getCartItems() {
        return cartItems;
    }
}
