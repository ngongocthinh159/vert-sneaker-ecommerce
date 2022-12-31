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

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.fragment.ArFragment;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.RepositoryManager;


public class MainActivity extends AppCompatActivity {

    public static NavController navController;
    public static Toolbar toolbar;
    public static BottomNavigationView bottomNav;
    public static Context context;
    public static RepositoryManager repositoryManager = new RepositoryManager();
    public static boolean isARAvailable = false;

    public static float device_height_pxl;
    public static float device_width_pxl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        // Fetch database
        repositoryManager.signInAnonymously();
        repositoryManager.fetchAllSneakers();

        // Set view
        setContentView(R.layout.activity_main);

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
        // Set toolbar with main activity
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        // Set toolbar with navcontroller
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration); // Set toolbar with nav, with setting of appBarConfiguration
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
}