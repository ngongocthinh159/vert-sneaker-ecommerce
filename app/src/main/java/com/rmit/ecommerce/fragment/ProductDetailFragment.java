package com.rmit.ecommerce.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.DocumentTransform;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.SneakerModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailFragment extends Fragment {

    View view;
    TextView largeTitle;
    TextView productBrand_detail;
    TextView productName_detail;
    TextView productPrice_detail;
    TextView productDes_detail;
    TextView tvSize;
    MaterialButtonToggleGroup toggleGroup;
    MaterialButton btnAddToCart;
    MaterialButton btnCart;
    String pid;
    SneakerModel sneakerModel;
    ImageSlider imageSlider;

    public static boolean product_is_available = true;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductDetailFragment newInstance(String param1, String param2) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        product_is_available = true;

        // Get refs
        largeTitle = view.findViewById(R.id.largeTitle);
        productBrand_detail = view.findViewById(R.id.productBrand_detail);
        productName_detail = view.findViewById(R.id.productName_detail);
        productPrice_detail = view.findViewById(R.id.productPrice_detail);
        productDes_detail = view.findViewById(R.id.productDes_detail);
        tvSize = view.findViewById(R.id.tvSize);
        toggleGroup = view.findViewById(R.id.toggleGroup);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        btnCart = view.findViewById(R.id.btnCart);

        // Setup data
        bindDataToView();

        // Setup button
        setupButton();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupButton() {
        // Setup back button
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.popBackStack();
            }
        });

        // Setup shopping cart button
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (MainActivity.navController.getCurrentDestination().getId() != R.id.homeFragment) {
                    MainActivity.navController.navigateUp();
                }
                MainActivity.navController.navigate(R.id.shoppingCartFragment);
            }
        });

        // Setup AR button (hide or show - AR avail or un avail)
        setupARButton();

        // Setup add to cart btn
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current checked size is in the cart
                int checkedSize = 0;
                for (int i = 34; i <= 46; i++) {
                    if (((MaterialButton)toggleGroup.getChildAt(i - 34)).isChecked()) {
                        checkedSize = i;
                        break;
                    }
                }
                if (checkedSize == 0) {
                    Toast.makeText(MainActivity.context, "Please choose a size!", Toast.LENGTH_SHORT).show();
                    return; // => No size check => return
                }

                // Check if current product with checked size is in cart
                ArrayList<CartItemModel> items = MainActivity.repositoryManager.getCartItems();
                for (CartItemModel item : items) {
                    if (item.getPid().getId().equals(pid) && item.getSize() == checkedSize) {
                        Toast.makeText(MainActivity.context, "Already added in your shopping cart!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // If current product with checked size is not in cart => Create new cart item
                CartItemModel cartItemModel = new CartItemModel();
                cartItemModel.setPid(MainActivity.repositoryManager.getFireStore().collection("sneakers").document(pid));
                cartItemModel.setQuantity(1);
                cartItemModel.setSize(checkedSize);
                cartItemModel.setTimestamp(Timestamp.now());
                MainActivity.repositoryManager.getFireStore().collection("cartItems").add(cartItemModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String itemId = documentReference.getId(); // Newly created cart's item id

                        // Update local: Add new cart's item to current cart
                        Map<String, Object> data = new HashMap<>();
                        ArrayList<String> list = new ArrayList<>(MainActivity.repositoryManager.getCartObject().getCartItemIds());
                        list.add(itemId);
                        data.put("cartItemIds", list);
                        MainActivity.repositoryManager.getCartItems().add(cartItemModel);
                        MainActivity.repositoryManager.getCartObject().getCartItemIds().add(itemId);

                        // Update remote: Add new cart's item to current cart
                        MainActivity.repositoryManager.getFireStore()
                                .collection("carts")
                                .document(MainActivity.repositoryManager.getUser().getCurrentCartId())
                                .set(data, SetOptions.merge());

                        Toast.makeText(MainActivity.context, "Added to cart!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void bindDataToView() {
        // Get sneaker model
        if (getArguments() == null) return;
        pid = getArguments().getString("pid");
        MainActivity.repositoryManager.getFireStore()
                .collection("sneakers")
                .document(pid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            SneakerModel sneakerModel = task.getResult().toObject(SneakerModel.class);

                            if (sneakerModel != null) {
                                // Get available size
                                ArrayList<Integer> sizes = new ArrayList<>();
                                if (sneakerModel.getSize() != null && sneakerModel.getSize().size() != 0) {
                                    HashMap<String, Integer> cur_size = sneakerModel.getSize().get(0);
                                    if (cur_size != null) {
                                        for (Map.Entry<String, Integer> entry : cur_size.entrySet()) {
                                            String size = entry.getKey();
                                            int quantity = entry.getValue();
                                            if (quantity > 0) sizes.add(Integer.valueOf(size));
                                        }
                                    }
                                }
                                Collections.sort(sizes);

                                // Setup text data
                                largeTitle.setText(sneakerModel.getTitle());
                                productBrand_detail.setText(sneakerModel.getBrand());
                                productName_detail.setText(sneakerModel.getTitle());
                                productPrice_detail.setText("$" + String.valueOf(sneakerModel.getPrice()));
                                productDes_detail.setText(sneakerModel.getDescription());
                                if (sizes.size() == 0) tvSize.setText("Sold out");
                                for (int i = 34; i <= 46; i++) {
                                    if (!sizes.contains(i)) {
                                        // Turn off sizes that not available
                                        ((MaterialButton) toggleGroup.getChildAt(i - 34)).setVisibility(View.GONE);
                                    }
                                }

                                // Setup image
                                imageSlider = view.findViewById(R.id.imageSlider);
                                ArrayList<SlideModel> slideModels = new ArrayList<>();
                                imageSlider.setImageList(slideModels);
                                if (!sneakerModel.getImage().isEmpty() && sneakerModel.getImage() != null) {
                                    MainActivity.assetManager.fetchAllImages(sneakerModel.getImage(), imageSlider, slideModels);
                                }
                            } else {
                                product_is_available = false;
                                MainActivity.navController.navigateUp();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.context);
                                builder.setMessage("This product is currently unavailable!");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                        }
                    }
                });
    }

    private void setupARButton() {
        LinearLayout layoutViewAR = view.findViewById(R.id.layoutViewAR);
        MaterialButton btnViewAR = view.findViewById(R.id.btnViewAR);
        MaterialButton btnViewARSmall = view.findViewById(R.id.btnViewARSmall);

        // Set visibility
        if (MainActivity.isARAvailable) {
            layoutViewAR.setVisibility(View.VISIBLE);
            btnViewAR.setVisibility(View.VISIBLE);
        } else { // The device is unsupported or unknown.
            layoutViewAR.setVisibility(View.GONE);
            btnViewAR.setVisibility(View.GONE);
        }

        // Setup touch action
        btnViewAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_productDetailFragment_to_arFragment);
            }
        });

        btnViewARSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_productDetailFragment_to_arFragment);
            }
        });
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 100);
            Toast.makeText(MainActivity.context, "Need camera permission for this feature!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean cameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }
}