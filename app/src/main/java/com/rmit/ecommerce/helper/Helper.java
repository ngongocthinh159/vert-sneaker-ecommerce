package com.rmit.ecommerce.helper;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

//import com.google.ar.core.ArCoreApk;
import androidx.annotation.Nullable;

import com.google.ar.core.ArCoreApk;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.fragment.SearchFragment;
import com.rmit.ecommerce.repository.SneakerModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void toggleKeyboard() {
        InputMethodManager imm = (InputMethodManager) MainActivity.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyBoard(View view) {
        InputMethodManager manager
                = (InputMethodManager)
                MainActivity.context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getFormattedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    public static void popBackStackAll() {
        while (MainActivity.navController.getCurrentDestination() != null) MainActivity.navController.popBackStack();
    }

    public static void popBackStackUntilHome() {
        while (MainActivity.navController.getCurrentDestination().getId() != R.id.homeFragment) MainActivity.navController.popBackStack();
    }

    public static void checkARAvailability() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(MainActivity.context);
        if (availability.isTransient()) {
            // Continue to query availability at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkARAvailability();
                }
            }, 200);
        }

        // The device is unsupported or unknown.
        MainActivity.isARAvailable = availability.isSupported();
    }

    public static boolean isValidEmail(String email) {
        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static ArrayList<SneakerModel> getFilterList(int sortType, double minValue, double maxValue, String keyword, ArrayList<SneakerModel> sneakers) {
        ArrayList<SneakerModel> filterList= new ArrayList<>();
        keyword = keyword.toLowerCase();

        // Match keyword
        if (!keyword.isEmpty()) {
            for (SneakerModel sneaker : sneakers) {
                if ((sneaker.getTitle() != null && sneaker.getTitle().toLowerCase().contains(keyword))
                        || (sneaker.getBrand() != null && sneaker.getBrand().toLowerCase().contains(keyword))) {
                    filterList.add(sneaker);
                }
            }
        } else {
            filterList = new ArrayList<>(sneakers);
        }

        // Match range
        Iterator<SneakerModel> itr = filterList.iterator();
        while (itr.hasNext()) {
            SneakerModel sneaker = (SneakerModel) itr.next();
            if (!(minValue <= sneaker.getPrice() && sneaker.getPrice() <= maxValue)) {
                itr.remove();
            }
        }

        // Match type
        switch (sortType) {
            case SearchFragment.SORT_TYPE_LTH:
                filterList.sort(new Comparator<SneakerModel>() {
                    @Override
                    public int compare(SneakerModel o1, SneakerModel o2) {
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                });
                break;
            case SearchFragment.SORT_TYPE_HTL:
                filterList.sort(new Comparator<SneakerModel>() {
                    @Override
                    public int compare(SneakerModel o1, SneakerModel o2) {
                        return Double.compare(o2.getPrice(), o1.getPrice());
                    }
                });
                break;
            case SearchFragment.SORT_TYPE_ATZ:
                filterList.sort(new Comparator<SneakerModel>() {
                    @Override
                    public int compare(SneakerModel o1, SneakerModel o2) {
                        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
                    }
                });
                break;
            case SearchFragment.SORT_TYPE_ZTA:
                filterList.sort(new Comparator<SneakerModel>() {
                    @Override
                    public int compare(SneakerModel o1, SneakerModel o2) {
                        return o2.getTitle().compareToIgnoreCase(o1.getTitle());
                    }
                });
                break;
        }

        return filterList;
    }
}
