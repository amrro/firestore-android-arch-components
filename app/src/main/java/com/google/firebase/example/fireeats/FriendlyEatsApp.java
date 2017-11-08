package com.google.firebase.example.fireeats;

import android.app.Activity;
import android.app.Application;

import com.google.firebase.example.fireeats.di.AppComponent;
import com.google.firebase.example.fireeats.di.AppModule;
import com.google.firebase.example.fireeats.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;


public final class FriendlyEatsApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        component().inject(this);
    }

    protected AppComponent component() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }
}
