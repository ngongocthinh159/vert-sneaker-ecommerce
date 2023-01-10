package com.rmit.ecommerce.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.fragment.HomeFragment;
import com.rmit.ecommerce.fragment.ShoppingCartFragment;

import java.util.ArrayList;
import java.util.Map;

public class RepositoryManager {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArrayList<SneakerModel> sneakers = new ArrayList<>();
    private static final ArrayList<SneakerModel> sneakers_bestSeller = new ArrayList<>();
    private static final ArrayList<SneakerModel> sneakers_popular = new ArrayList<>();
    private static final ArrayList<SneakerModel> sneakers_newarrival = new ArrayList<>();
    private CartModel cartObject;
    private final ArrayList<CartItemModel> cartItems = new ArrayList<>();
    private UserModel user;

    public RepositoryManager() {}

    public void fetchAllSneakers() {
        sneakers.clear();
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

                            getSneakersInCategory();
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });
    }

    public void fetchUserInformation() {
        db.collection("users").document(MainActivity.userManager.getUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(UserModel.class);
                            fetchCartObject();
                        }
                    }
                });
    }

    public void fetchCartObject() {
        // Get cart information
        DocumentReference cartDoc = db.collection("carts").document(user.getCurrentCartId());
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
                        if (cartObject.getCartItemIds().contains(document.getId())) {
                            cartItems.add(document.toObject(CartItemModel.class));
                        }
                    }
                }
            }
        });
    }

    private void getSneakersInCategory() {
        sneakers_bestSeller.clear();
        sneakers_popular.clear();
        sneakers_newarrival.clear();
        for (SneakerModel sneaker : sneakers) {
            if (sneaker.getCategory() == null) continue;
            if (sneaker.getCategory().contains("bestseller")) {
                sneakers_bestSeller.add(sneaker);
            }

            if (sneaker.getCategory().contains("popular")) {
                sneakers_popular.add(sneaker);
            }

            if (sneaker.getCategory().contains("newarrival")) {
                sneakers_newarrival.add(sneaker);
            }
        }
    }

    public ArrayList<CartItemModel> getCartItems() {
        return cartItems;
    }

    public FirebaseFirestore getFireStore() {
        return db;
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

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ArrayList<SneakerModel> getPopularSneakers() {
        return sneakers_popular;
    }

    public ArrayList<SneakerModel> getBestSellerSneakers() {
        return sneakers_bestSeller;
    }

    public ArrayList<SneakerModel> getNewArrivalSneakers() {
        return sneakers_newarrival;
    }
}
