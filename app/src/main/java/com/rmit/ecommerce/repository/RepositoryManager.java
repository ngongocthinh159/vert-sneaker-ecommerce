package com.rmit.ecommerce.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.fragment.ShoppingCartFragment;

import java.util.ArrayList;
import java.util.Map;

public class RepositoryManager {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<SneakerModel> sneakers = new ArrayList<>();
    private String userCartId;
    private CartModel cartObject;
    private ArrayList<CartItemModel> cartItems = new ArrayList<>();

    public RepositoryManager() {}

    public void fetchAllSneakers() {
        db.collection("sneakers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                sneakers.add(document.toObject(SneakerModel.class));
                            }
                            for (SneakerModel s : sneakers) {
                                System.out.println(s.getTitle() + " - " + s.getBrand() + " - " + s.getImage());
                            }
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });
    }

    public void fetchCartId() {
        // Get cart id from user id
        if (userCartId == null) {
            DocumentReference userDoc = db.collection("users").document(MainActivity.userManager.getUserId());
            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userCartId = (String) document.get("currentCartId");
                        fetchCartObject();
                    } else {

                    }
                }
            });
        }
    }

    public void fetchCartObject() {
        // Get cart information
        DocumentReference cartDoc = db.collection("carts").document(userCartId);
        cartDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    cartObject = document.toObject(CartModel.class);
                    fetchCartItems();
                } else {

                }
            }
        });
    }

    public void fetchCartItems() {
        cartItems.clear();
        ArrayList<String> cartItemIds = cartObject.getCartItemIds();
        CollectionReference collection = db.collection("cartItems");
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (cartItemIds.contains(document.getId())) {
                            cartItems.add(document.toObject(CartItemModel.class));
                        }
                    }
                }
            }
        });
    }

    public ArrayList<CartItemModel> getCartItems() {
        return cartItems;
    }

    public FirebaseFirestore getFireStore() {
        return db;
    }

    public String getUserCartId() {
        return userCartId;
    }

    public CartModel getCartObject() {
        return cartObject;
    }

    public void setCartObject(CartModel cartObject) {
        this.cartObject = cartObject;
    }

    public ArrayList<SneakerModel> getSneakers() {
        return sneakers;
    }

    public FirebaseFirestore getInstance() {
        return db;
    }

    public void setSneakers(ArrayList<SneakerModel> s) {
        sneakers = s;
    }
}
