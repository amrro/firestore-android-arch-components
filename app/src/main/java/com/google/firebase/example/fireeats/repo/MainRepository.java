package com.google.firebase.example.fireeats.repo;


import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.example.fireeats.common.QueryLiveData;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.ui.main.Filters;
import com.google.firebase.example.fireeats.util.RatingUtil;
import com.google.firebase.example.fireeats.util.RestaurantUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public final class MainRepository {

    private final FirebaseFirestore firestore;

    @Inject
    public MainRepository(FirebaseFirestore store) {
        this.firestore = store;
    }

    public QueryLiveData<Restaurant> restaurants(@NonNull final Filters filters) {
        return new QueryLiveData<>(toQuery(filters), Restaurant.class);
    }

    private Query toQuery(final Filters filters) {
        // Construct query basic query
        Query query = firestore.collection("restaurants");

        if (filters == null) {
            query.orderBy("avgRating", Query.Direction.ASCENDING);
        } else {
            // Category (equality filter)
            if (filters.hasCategory()) {
                query = query.whereEqualTo(Restaurant.FIELD_CATEGORY, filters.getCategory());
            }

            // City (equality filter)
            if (filters.hasCity()) {
                query = query.whereEqualTo(Restaurant.FIELD_CITY, filters.getCity());
            }

            // Price (equality filter)
            if (filters.hasPrice()) {
                query = query.whereEqualTo(Restaurant.FIELD_PRICE, filters.getPrice());
            }

            // Sort by (orderBy with direction)
            if (filters.hasSortBy()) {
                query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
            }
        }

        /* query could be limited like: query.limit(5) */
        return query;
    }

    public void addRestaurants(final Context context) {
        // Add a bunch of random restaurants
        WriteBatch batch = firestore.batch();
        for (int i = 0; i < 10; i++) {
            DocumentReference restRef = firestore.collection("restaurants").document();

            // Create random restaurant / ratings
            Restaurant randomRestaurant = RestaurantUtil.getRandom(context);
            List<Rating> randomRatings = RatingUtil.getRandomList(randomRestaurant.numRatings);
            randomRestaurant.avgRating = RatingUtil.getAverageRating(randomRatings);

            // Add restaurant
            batch.set(restRef, randomRestaurant);

            // Add ratings to sub-collection
            for (Rating rating : randomRatings) {
                batch.set(restRef.collection("ratings").document(), rating);
            }
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("Write batch succeeded.");
            } else {
                Timber.w("write batch failed.", task.getException());
            }
        });
    }

    public void deleteAll() {
        RestaurantUtil.deleteAll(firestore.collection("restaurants"));
    }
}
