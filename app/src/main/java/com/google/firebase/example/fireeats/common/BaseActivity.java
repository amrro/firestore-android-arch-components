package com.google.firebase.example.fireeats.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * This Activity is to be inherited by any activity to initiate the injection.
 */

public abstract class BaseActivity extends DaggerAppCompatActivity {

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    protected <T extends ViewModel> T getViewModel(final Class<T> cls) {
        return ViewModelProviders.of(this, viewModelFactory).get(cls);
    }
}
