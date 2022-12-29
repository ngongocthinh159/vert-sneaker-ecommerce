package com.rmit.ecommerce;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.NumberFormat;
import java.util.Locale;

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

    public static String getFormatedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    public static void popBackStackAll() {
        while (MainActivity.navController.getCurrentDestination() != null) MainActivity.navController.popBackStack();
    }
}
