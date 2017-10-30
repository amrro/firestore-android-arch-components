package com.google.firebase.example.fireeats.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.example.fireeats.CompletionLiveData;
import com.google.firebase.example.fireeats.Resource;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.repo.RestaurantRepository;

import java.util.List;


public final class RatingViewModel extends ViewModel {
    private RestaurantRepository repository;
    final private MutableLiveData<String> id = new MutableLiveData<>();
    final private LiveData<Resource<Restaurant>> restaurant;
    final private LiveData<Resource<List<Rating>>> ratings;

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
