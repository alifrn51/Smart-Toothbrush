package com.meditradent.meditradent.di;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtils {

    /**
     * Show soft keyboard, Dialog uses
     *
     * @param activity Current Activity
     */
    public static void showSoftInput(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


}