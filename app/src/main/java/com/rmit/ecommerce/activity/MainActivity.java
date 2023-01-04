package com.rmit.ecommerce.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.fragment.ArFragment;
import com.rmit.ecommerce.fragment.GoogleMapFragment;
import com.rmit.ecommerce.fragment.PersonalSettingFragment;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.AdminCrudService;
import com.rmit.ecommerce.repository.AssetManager;
import com.rmit.ecommerce.repository.RepositoryManager;
import com.rmit.ecommerce.repository.UserManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    public static NavController navController;
    public static Toolbar toolbar;
    public static BottomNavigationView bottomNav;
    public static Context context;
    public static RepositoryManager repositoryManager = new RepositoryManager();
    public static UserManager userManager = new UserManager();
    public static AssetManager assetManager = new AssetManager();
    public static AdminCrudService adminCrudService = new AdminCrudService();
    public static boolean isARAvailable = false;
    private ProgressDialog pd;


    public static float device_height_pxl;
    public static float device_width_pxl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        // Fetch database
        repositoryManager.fetchAllSneakers();
        if (userManager.isLoggedIn()) repositoryManager.fetchUserInformation();

        // Set view
        setContentView(R.layout.activity_main);

        // Network checking
        networkChecking();

        // Check AR availability
        Helper.checkARAvailability();

        // Get references
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_nav);

        // Retrieve navController
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Setup toolbar
        setupToolbar();

        // Set bottom nav with navcontroller
        NavigationUI.setupWithNavController(bottomNav, navController);

        getRuntimeDisplayWidthAndHeight();
    }

    private void setupToolbar() {
        // Set toolbar with navcontroller
        toolbar.setVisibility(View.GONE);
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.shoppingCartFragment);
        topLevelDestinations.add(R.id.notificationFragment);
        topLevelDestinations.add(R.id.personalSettingFragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(topLevelDestinations)
                .build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // Hide and show toolbar/bottomNavBar
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                int currentNavId = navDestination.getId();
                if (currentNavId == R.id.loginFragment ||
                        currentNavId == R.id.signUpFragment) {
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    toolbar.setVisibility(View.GONE);
                }

                if (currentNavId == R.id.homeFragment ||
                        currentNavId == R.id.shoppingCartFragment ||
                        currentNavId == R.id.notificationFragment ||
                        currentNavId == R.id.personalSettingFragment) {
                    bottomNav.animate().translationY(0).setDuration(300);
                    bottomNav.setVisibility(View.VISIBLE);
                } else {
                    bottomNav.animate().translationY(bottomNav.getHeight() + 200).setDuration(300);
//                    bottomNav.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getRuntimeDisplayWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_height_pxl = displayMetrics.heightPixels;
        device_width_pxl = displayMetrics.widthPixels;
    }

    private void animateBackGround() {
        // android:background="@drawable/gradient_list"

        // Animate background
        LinearLayout linearLayout = findViewById(R.id.mainLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }

    @Override
    public void onBackPressed() {
        if (MainActivity.navController.getCurrentDestination().getId() == R.id.arFragment) {
            ArFragment.cleanArScene();
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        adminCrudService.getInstance().handlePhotosPick(requestCode, resultCode, data, getContentResolver());

        // Pick user image
        if (requestCode == PersonalSettingFragment.OPEN_DOCUMENT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                // this is the image selected by the user
                Uri imageUri = data.getData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void networkChecking() {
        if (pd == null) {
            pd = new ProgressDialog(MainActivity.this);
            pd.setCancelable(false);
            pd.setTitle("Waiting for internet connection");
            pd.show();
        }

        // Create network callback
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // network available
                if (pd != null) pd.dismiss();
            }

            @Override
            public void onLost(Network network) {
                // network unavailable
                pd = new ProgressDialog(MainActivity.this);
                pd.setCancelable(false);
                pd.setTitle("Waiting for internet connection");
                pd.show();
            }
        };

        // Register network callback with connectivity manager
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }
}