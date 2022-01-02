package com.toothbrush.smarttoothbrush.di;

import android.app.Application;
import android.os.Environment;

import com.toothbrush.smarttoothbrush.R;
import com.toothbrush.smarttoothbrush.di.sharepreferences.MyPref;

import java.io.File;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        MyPref.initial(getApplicationContext());

    }
}
