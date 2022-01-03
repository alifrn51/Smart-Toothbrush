package com.meditradent.meditradent.di;

import android.app.Application;

import com.meditradent.meditradent.di.sharepreferences.MyPref;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        MyPref.initial(getApplicationContext());

    }
}
