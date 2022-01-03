package com.meditradent.meditradent.di.sharepreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MyPref {
    private static SharedPreferences mSharedPref;

    public MyPref() {
    }

    public static SharedPreferences initial(Context context) {
        if (mSharedPref == null){
            return mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        }
        return mSharedPref;
    }

    public static  String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static  void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static  float read(String key, Float defValue) {
        return mSharedPref.getFloat(key, defValue);
    }

    public static  void write(String key, Float value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putFloat(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static  void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static  Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static  void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }

    public static  void clearAll() {
        mSharedPref.edit().clear().apply();
    }


}

