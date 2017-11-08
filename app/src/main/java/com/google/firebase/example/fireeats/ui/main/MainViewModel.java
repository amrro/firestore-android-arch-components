package com.google.firebase.example.fireeats.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fireeats.common.Resource;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.repo.MainRepository;

import java.util.List;

/**
 * ViewModel for {@link MainActivity}.
 */

public class MainViewModel extends ViewModel {
    private final LiveData<Boolean> isSignedIn;
    private final MutableLiveData<Filters> filters = new MutableLiveData<>();
    private final LiveData<Resource<List<Restaurant>>> restaurants;


    public MainViewModel() {
        final MainRepository repository = new MainRepository();
        filters.setValue(Filters.getDefault());
        isSignedIn = new LiveData<Boolean>() {
            @Override
            protected void onActive() {
                super.onActive();
                setValue(FirebaseAuth.getInstance().getCurrentUser() != null);
            }
        };

        restaurants = Transformations.switchMap(filters, repository::restaurants);
    }

    LiveData<Boolean> isSignedIn() {
        return isSignedIn;
    }

    void setFilters(final Filters filters) {
        // TODO: 10/30/17 check if
        if (filters == null) {
            return;
        }
        this.filters.setValue(filters);
    }

    LiveData<Resource<List<Restaurant>>> restaurants() {
        return restaurants;
    }
}
