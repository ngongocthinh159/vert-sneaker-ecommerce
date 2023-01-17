package com.rmit.ecommerce.fragment;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.rmit.ecommerce.SaveSharedPreference;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.MyRecyclerViewAdapter;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.RepositoryManager;
import com.rmit.ecommerce.repository.SneakerModel;
import com.rmit.ecommerce.repository.UserImageManager;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private View view;
    private ProgressDialog pd;
    private boolean isFirstFetch = true;
    private ProgressBar progressBarHome;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        if (isFirstFetch && MainActivity.userManager.isLoggedIn()) {
            fetchAllSneakers();
            isFirstFetch = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!ProductDetailFragment.product_is_available) {
            fetchAllSneakers();
            ProductDetailFragment.product_is_available = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Button searchByImage = view.findViewById(R.id.btnSearchByImage);

        searchByImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            getActivity().startActivityForResult(intent, MainActivity.mlManager.RQ_ML_CODE);
        });

        if (MainActivity.userManager.isLoggedIn()) {
            // Get refs
            progressBarHome = view.findViewById(R.id.progressBarHome);

            // Setup recycler view
            setupRecyclerView();

            // Setup search bar
            TextInputEditText searchBar = view.findViewById(R.id.searchBar);
            searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        Bundle bundle = new Bundle();
                        bundle.putString("category", "all");
                        MainActivity.navController.navigate(R.id.action_global_searchFragment, bundle);
                    }
                }
            });

            // Setup image slide
            setupImageSlider();

            // Setup see all buttons
            setupSeeAllButton(view);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If a normal user logged in => Redirect to getting started page
        if (!MainActivity.userManager.isLoggedIn()) {
            Helper.popBackStackAll();
            MainActivity.bottomNav.setVisibility(View.GONE);
            MainActivity.navController.navigate(R.id.gettingStartedFragment);
        }
    }

    private void setupSeeAllButton(View view) {
        Button seeAllBestSeller = view.findViewById(R.id.seeAllBestSeller);
        Button seeAllPopular = view.findViewById(R.id.seeAllPopular);
        Button seeAllNewArrival = view.findViewById(R.id.seeAllNewArrival);

        seeAllBestSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("category", "bestseller");
                MainActivity.navController.navigate(R.id.action_global_searchFragment, bundle);
            }
        });

        seeAllPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("category", "popular");
                MainActivity.navController.navigate(R.id.action_global_searchFragment, bundle);
            }
        });

        seeAllNewArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("category", "newarrival");
                MainActivity.navController.navigate(R.id.action_global_searchFragment, bundle);
            }
        });
    }

    private void setupImageSlider() {
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);

        ArrayList<SneakerModel> sneaker_trending = MainActivity.repositoryManager.getTrendingSneakers();
        if (sneaker_trending.size() == 0) {
            imageSlider.setVisibility(View.GONE);
            return;
        }

        imageSlider.setVisibility(View.VISIBLE);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        ArrayList<Uri> list = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();
        for (SneakerModel sneaker : sneaker_trending) {
            // If the trending image link has been fetched
            if (sneaker.getTrendingImage() != null) {
                list.add(sneaker.getTrendingImage());
                ids.add(sneaker.getId());
                slideModels.add(new SlideModel(String.valueOf(sneaker.getTrendingImage()),  ScaleTypes.CENTER_CROP));

                if (list.size() == sneaker_trending.size()) {
                    imageSlider.setImageList(slideModels);
                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            Bundle bundle = new Bundle();
                            bundle.putString("pid", ids.get(i));
                            MainActivity.navController.navigate(R.id.action_global_productDetailFragment, bundle);
                        }
                    });
                }
                continue;
            }

            // Fetch trending image link
            String imgUrl = sneaker.getImage() + "/trending";
            StorageReference gsReference = MainActivity.assetManager.getStorage().getReferenceFromUrl(imgUrl);
            gsReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            list.add(uri);
                            ids.add(sneaker.getId());
                            sneaker.setTrendingImage(uri);
                            if (list.size() == sneaker_trending.size()) {
                                for (Uri uri1 : list) {
                                    slideModels.add(new SlideModel(String.valueOf(uri1),  ScaleTypes.CENTER_CROP));
                                }
                                imageSlider.setImageList(slideModels);
                                imageSlider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("pid", ids.get(i));
                                        MainActivity.navController.navigate(R.id.action_global_productDetailFragment, bundle);
                                    }
                                });
                            }
                        }
                    });
                }
            });


        }
    }

    public void setupRecyclerView() {
        // Setup first category recycler view
        setupFirstCategoryRecyclerView(view);

        // Setup 2nd recycler view
        setupSecondCategoryRecyclerView(view);

        // Setup 3rd recycler view
        setupThirdCategoryRecyclerView(view);
    }

    private void setupThirdCategoryRecyclerView(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.newArrivalCategory);
        ArrayList<SneakerModel> sneakers_newArrival = MainActivity.repositoryManager.getNewArrivalSneakers();
        if (sneakers_newArrival.size() == 0) {
            return;
        }

        linearLayout.setVisibility(View.VISIBLE);
        if (progressBarHome.getVisibility() == View.VISIBLE) progressBarHome.setVisibility(View.GONE);
        // Bind data
        RecyclerView rV3 = view.findViewById(R.id.rV3);

        // Adapter
        MyRecyclerViewAdapter myRecyclerViewAdapter3 = new MyRecyclerViewAdapter(sneakers_newArrival, "new_arrivals");

        // Layout
        LinearLayoutManager layoutManager3
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        // Bind adapter and layout
        rV3.setAdapter(myRecyclerViewAdapter3);
        rV3.setLayoutManager(layoutManager3);
    }

    private void setupSecondCategoryRecyclerView(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.popularSneakerCategory);
        ArrayList<SneakerModel> sneakers_popular = MainActivity.repositoryManager.getPopularSneakers();
        if (sneakers_popular.size() == 0) {
            return;
        }

        linearLayout.setVisibility(View.VISIBLE);
        if (progressBarHome.getVisibility() == View.VISIBLE) progressBarHome.setVisibility(View.GONE);

        // Bind data
        RecyclerView rV2 = view.findViewById(R.id.rV2);

        // Adapter
        MyRecyclerViewAdapter myRecyclerViewAdapter2 = new MyRecyclerViewAdapter(sneakers_popular, "popular_sneakers");

        // Layout
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        // Bind adapter and layout
        rV2.setAdapter(myRecyclerViewAdapter2);
        rV2.setLayoutManager(layoutManager2);
    }

    private void setupFirstCategoryRecyclerView(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.bestSellerCategory);
        ArrayList<SneakerModel> sneakers_best = MainActivity.repositoryManager.getBestSellerSneakers();
        if (sneakers_best.size() == 0) { // Check if this category is empty
            return;
        }

        linearLayout.setVisibility(View.VISIBLE);
        if (progressBarHome.getVisibility() == View.VISIBLE) progressBarHome.setVisibility(View.GONE);

        // Bind data to large card
        MaterialCardView cardView = view.findViewById(R.id.largeCard);
        ImageView productImage = cardView.findViewById(R.id.productImageFirst);
        TextView productBranch = cardView.findViewById(R.id.productBranchFirst);
        TextView productName = cardView.findViewById(R.id.productNameFirst);
        TextView productPrice = cardView.findViewById(R.id.productPrice);
        ProgressBar progressBar = cardView.findViewById(R.id.progressBarFirst);

        // Map text data to large card
        productBranch.setText(sneakers_best.get(0).getBrand());
        productName.setText(sneakers_best.get(0).getTitle());
        double temp_price = sneakers_best.get(0).getPrice();
        String text_price = "";
        if (Math.floor(temp_price) == Math.ceil(temp_price)) text_price = String.valueOf((int) temp_price);
        productPrice.setText("$" + text_price);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("pid", sneakers_best.get(0).getId());
                MainActivity.navController.navigate(R.id.action_global_productDetailFragment, bundle);
            }
        });
        fetchLargeCardImage(sneakers_best.get(0).getImage(), sneakers_best.get(0), productImage, progressBar); // Fetch image

        // Bind data to recycler view (if have)
        if (sneakers_best.size() >= 2) {
            ArrayList<SneakerModel> temp = new ArrayList<>();
            for (int i = 1; i < sneakers_best.size(); i++) {
                temp.add(sneakers_best.get(i));
            }
            RecyclerView rV1 = view.findViewById(R.id.rV1);
            MyRecyclerViewAdapter myRecyclerViewAdapter1 = new MyRecyclerViewAdapter(temp, "best_seller");
            LinearLayoutManager layoutManager1
                    = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
            rV1.setAdapter(myRecyclerViewAdapter1);
            rV1.setLayoutManager(layoutManager1);
        }

    }

    private void fetchLargeCardImage(String imageStr, SneakerModel sneaker, ImageView productImage, ProgressBar progressBar) {
        FirebaseStorage db = FirebaseStorage.getInstance();
        if (sneaker.getFigureImage() != null) {
            Picasso.get().load(sneaker.getFigureImage()).into(productImage);
        } else {
            productImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (!imageStr.isEmpty()) {
                db.getReferenceFromUrl(imageStr).listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        productImage.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        sneaker.setFigureImage(uri);
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

    public void fetchAllSneakers() {
        MainActivity.repositoryManager.getFireStore().collection("sneakers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            MainActivity.repositoryManager.getSneakers().clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                MainActivity.repositoryManager.getSneakers().add(document.toObject(SneakerModel.class));
                            }

                            getSneakersInCategory();
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });
    }

    private void getSneakersInCategory() {
        MainActivity.repositoryManager.getBestSellerSneakers().clear();
        MainActivity.repositoryManager.getPopularSneakers().clear();
        MainActivity.repositoryManager.getNewArrivalSneakers().clear();
        MainActivity.repositoryManager.getTrendingSneakers().clear();
        for (SneakerModel sneaker : MainActivity.repositoryManager.getSneakers()) {
            if (sneaker.getCategory() == null) continue;
            if (sneaker.getCategory().contains("bestseller")) {
                MainActivity.repositoryManager.getBestSellerSneakers().add(sneaker);
            }

            if (sneaker.getCategory().contains("popular")) {
                MainActivity.repositoryManager.getPopularSneakers().add(sneaker);
            }

            if (sneaker.getCategory().contains("newarrival")) {
                MainActivity.repositoryManager.getNewArrivalSneakers().add(sneaker);
            }

            if (sneaker.getCategory().contains("trending")) {
                MainActivity.repositoryManager.getTrendingSneakers().add(sneaker);
            }
        }

        setupImageSlider();
        setupRecyclerView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ScrollView scrollView = view.findViewById(R.id.scrollViewHomeFrag);
        outState.putInt("scrollYPosition", scrollView.getScrollY());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            ScrollView scrollView = view.findViewById(R.id.scrollViewHomeFrag);
            scrollView.setScrollY(savedInstanceState.getInt("scrollYPosition"));
            isFirstFetch = false;
        }
    }
}