package com.rmit.ecommerce.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.storage.StorageReference;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.SneakerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminRVAdapter extends RecyclerView.Adapter<AdminRVAdapter.ViewHolder> {
    ArrayList<SneakerModel> sneakers;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView productImage;
        TextView productBranch;
        TextView productName;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            productImage = itemView.findViewById(R.id.productImage);
            productBranch = itemView.findViewById(R.id.productBranch);
            productName = itemView.findViewById(R.id.productName);
            progressBar = itemView.findViewById(R.id.progressBar);
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
    }

    public AdminRVAdapter() {
        sneakers = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull AdminRVAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get references to View

        MaterialCardView cardView =  holder.getCardView();
        ImageView productImage = holder.getProductImage();
        TextView productBranch = holder.getProductBranch();
        TextView productName = holder.getProductName();
        ProgressBar progressBar = holder.getProgressBar();

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
                MainActivity.adminCrudService.getInstance().setCurrentSneakerId(sneakers.get(position).getId());
                MainActivity.navController.navigate(R.id.action_homeAdminFragment_to_productManageFragment);
            }
        });

        productBranch.setText(sneakers.get(position).getBrand());
        productName.setText(sneakers.get(position).getTitle());
        String imageStr = sneakers.get(position).getImage();
        FirebaseStorage db = FirebaseStorage.getInstance();
        if (sneakers.get(position).getFigureImage() != null) {
            Picasso.get().load(sneakers.get(position).getFigureImage()).into(productImage);
        } else {
            productImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (!imageStr.isEmpty() && productImage.getDrawable() == null) {
                db.getReferenceFromUrl(imageStr).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                System.out.println("UH OH I HAVE TO FETCH AGAIN");
                                if (listResult.getItems().isEmpty()) return;
                                listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        productImage.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        System.out.println("RECEIVED URI: " + uri.toString());
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
