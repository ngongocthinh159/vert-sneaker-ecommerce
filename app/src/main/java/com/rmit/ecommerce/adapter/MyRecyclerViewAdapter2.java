package com.rmit.ecommerce.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.SneakerModel;

import java.util.ArrayList;
import java.util.Map;

public class MyRecyclerViewAdapter2 extends RecyclerView.Adapter<MyRecyclerViewAdapter2.ViewHolder>{
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
        TextView productMaxQuantity;
        MaterialButton btnIncrease;
        MaterialButton btnDecrease;
        MaterialButton btnDeleteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            productImage = itemView.findViewById(R.id.productImage);
            productBranch = itemView.findViewById(R.id.productBranch);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity= itemView.findViewById(R.id.productQuantity);
            productSize = itemView.findViewById(R.id.productSize);
            productMaxQuantity= itemView.findViewById(R.id.productMaxQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
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

        public TextView getProductMaxQuantity() {
            return productMaxQuantity;
        }

        public MaterialButton getBtnIncrease() {
            return btnIncrease;
        }

        public MaterialButton getBtnDecrease() {
            return btnDecrease;
        }

        public MaterialButton getBtnDeleteItem() {
            return btnDeleteItem;
        }
    }

    public MyRecyclerViewAdapter2(ArrayList<CartItemModel> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_shopping_item, parent, false);

        context = parent.getContext();

        return new MyRecyclerViewAdapter2.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MaterialCardView cardView = holder.getCardView();
        ImageView productImage = holder.getProductImage();
        TextView productBranch = holder.getProductBranch();
        TextView productName = holder.getProductName();
        TextView productPrice = holder.getProductPrice();
        TextView productQuantity = holder.getProductQuantity();
        TextView productSize = holder.getProductSize();
        TextView productMaxQuantity = holder.getProductMaxQuantity();
        MaterialButton btnIncrease = holder.getBtnIncrease();
        MaterialButton btnDecrease = holder.getBtnDecrease();
        MaterialButton btnDeleteItem = holder.getBtnDeleteItem();

        // Map text data to view
        productQuantity.setText(String.valueOf(cartItems.get(position).getQuantity()));
        int size = cartItems.get(position).getSize();
        final int[] maxQuantity = {-1};
        productSize.setText(String.valueOf(size));
        cartItems.get(position).getPid().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    SneakerModel sneakerModel = documentSnapshot.toObject(SneakerModel.class);
                    productBranch.setText(sneakerModel.getBrand());
                    productName.setText(sneakerModel.getTitle());
                    productPrice.setText("$" + String.valueOf(sneakerModel.getPrice()));
                    maxQuantity[0] = sneakerModel.getSize().get(0).get(size + "");
                    productMaxQuantity.setText(maxQuantity[0] + " left over");

                    // Setup button visibility
                    if (Integer.parseInt(productQuantity.getText().toString()) < maxQuantity[0]) {
                        btnIncrease.setVisibility(View.VISIBLE);
                    }
                    if (Integer.parseInt(productQuantity.getText().toString()) > 1) {
                        btnDecrease.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Setup button action
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productQuantity.setText(String.valueOf(Integer.parseInt(productQuantity.getText().toString()) + 1));
                cartItems.get(position).setQuantity(cartItems.get(position).getQuantity() + 1);

                if (Integer.parseInt(productQuantity.getText().toString()) == maxQuantity[0]) {
                    btnIncrease.setVisibility(View.INVISIBLE);
                }

                if (btnDecrease.getVisibility() == View.INVISIBLE) btnDecrease.setVisibility(View.VISIBLE);
            }
        });

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productQuantity.setText(String.valueOf(Integer.parseInt(productQuantity.getText().toString()) - 1));
                cartItems.get(position).setQuantity(cartItems.get(position).getQuantity() - 1);

                if (Integer.parseInt(productQuantity.getText().toString()) == 1) {
                    btnDecrease.setVisibility(View.INVISIBLE);
                }

                if (btnIncrease.getVisibility() == View.INVISIBLE) btnIncrease.setVisibility(View.VISIBLE);
            }
        });

        btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.context);
                alertDialogBuilder.setTitle("Confirm")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete item Id from cart object's cartItemIDs list
                                String itemId = cartItems.get(position).getId();
                                ArrayList<String> itemIds = MainActivity.repositoryManager.getCartObject().getCartItemIds();
                                ArrayList<String> newList = new ArrayList<>();
                                for (String id : itemIds) {
                                    if (!id.equals(itemId)) newList.add(id);
                                }
                                MainActivity.repositoryManager.getCartObject().setCartItemIds(newList); // Update local
                                MainActivity.repositoryManager.getFireStore()
                                        .collection("carts")
                                        .document(MainActivity.repositoryManager.getUser().getCurrentCartId())
                                        .update("cartItemIds", newList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Delete cart item
                                                cartItems.remove(position); // Update local
                                                MainActivity.repositoryManager.getFireStore().
                                                        collection("cartItems").
                                                        document(itemId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                MyRecyclerViewAdapter2.this.notifyItemChanged(position);
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialogBuilder.show();
            }
        });

        // Map image to view
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
//        System.out.println("aaaaaaaaaaaaaaaaaaaaaa");
    }
}
