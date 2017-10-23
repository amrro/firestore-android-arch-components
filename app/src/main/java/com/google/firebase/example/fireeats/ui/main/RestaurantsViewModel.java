package com.google.firebase.example.fireeats.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

/**
 * ViewModel for {@link MainActivity}.
 */

public class RestaurantsViewModel extends ViewModel {
    private Filters mFilters;

    private final LiveData<Boolean> isSignedIn;

    public RestaurantsViewModel() {
        mFilters = Filters.getDefault();
        isSignedIn = new LiveData<Boolean>() {
            @Override
            protected void onActive() {
                super.onActive();
                setValue(FirebaseAuth.getInstance().getCurrentUser() != null);
            }
        };
    }

    public LiveData<Boolean> isSignedIn() {
        return isSignedIn;
    }

    public Filters getFilters() {
        return mFilters;
    }

    public void setFilters(Filters mFilters) {
        this.mFilters = mFilters;
    }
}
