package com.google.firebase.example.fireeats.di;

import com.google.firebase.example.fireeats.ui.detail.RestaurantDetailActivity;
import com.google.firebase.example.fireeats.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by amrro <amr.elghobary@gmail.com> on 7/22/17.
 * <p>
 * Injects {@link android.app.Activity}s
 */

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector
    abstract MainActivity contributesMainActivity();

    @ContributesAndroidInjector
    abstract RestaurantDetailActivity contributesRestaurantDetailActivity();

}
