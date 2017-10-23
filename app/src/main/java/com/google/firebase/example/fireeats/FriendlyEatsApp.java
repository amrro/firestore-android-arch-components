package com.google.firebase.example.fireeats;

import android.app.Application;

import timber.log.Timber;


public final class FriendlyEatsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
