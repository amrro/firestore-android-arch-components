package com.google.firebase.example.fireeats.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.example.fireeats.common.CompletionLiveData;
import com.google.firebase.example.fireeats.common.Resource;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.repo.RestaurantRepository;

import java.util.List;


public final class RatingViewModel extends ViewModel {
    private final RestaurantRepository repository;
    private final MutableLiveData<String> id = new MutableLiveData<>();
    private final LiveData<Resource<Restaurant>> restaurant;
    private final LiveData<Resource<List<Rating>>> ratings;

    public RatingViewModel() {
        repository = new RestaurantRepository();
        restaurant = Transformations.switchMap(id, input -> repository.restaurant(input));
        ratings = Transformations.switchMap(id, input -> repository.ratings(input));
    }

    RatingViewModel setRestaurantId(final String id) {
        if (id == null) {
            return null;
        }
        this.id.setValue(id);
        return this;
    }

    public LiveData<Resource<Restaurant>> restaurant() {
        return restaurant;
    }

    LiveData<Resource<List<Rating>>> ratings() {
        return ratings;
    }

    CompletionLiveData addRating(Rating rating) {
        return repository.addRating(id.getValue(), rating);
    }
}
