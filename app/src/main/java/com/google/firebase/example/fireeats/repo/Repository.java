package com.google.firebase.example.fireeats.repo;


import android.content.Context;
import android.util.Log;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Repository {

    public static final int LIMIT = 10;
    public static final String TAG = "Repository";
    private final FirebaseFirestore firestore;

    public Repository(FirebaseFirestore firestore) {
        this(firestore, true);
    }

    public Repository(FirebaseFirestore firebaseFirestore, boolean loggingEnabled) {
        FirebaseFirestore.setLoggingEnabled(loggingEnabled);
        this.firestore = firebaseFirestore;
    }

    public Query restaurants() {
        return restaurants(null);
    }

    public Query restaurants(final Filters filters) {
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

        // Limit items
        return query.limit(LIMIT);
    }

    public void addRestaurants(final Context context) {
        // Add a bunch of random restaurants
        WriteBatch batch = firestore.batch();
        for (int i = 0; i < 10; i++) {
            DocumentReference restRef = firestore.collection("restaurants").document();

            // Create random restaurant / ratings
            Restaurant randomRestaurant = RestaurantUtil.getRandom(context);
            List<Rating> randomRatings = RatingUtil.getRandomList(randomRestaurant.getNumRatings());
            randomRestaurant.setAvgRating(RatingUtil.getAverageRating(randomRatings));

            // Add restaurant
            batch.set(restRef, randomRestaurant);

            // Add ratings to subcollection
            for (Rating rating : randomRatings) {
                batch.set(restRef.collection("ratings").document(), rating);
            }
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Write batch succeeded.");
            } else {
                Log.w(TAG, "write batch failed.", task.getException());
            }
        });
    }

    public void deleteAll() {
        final ThreadPoolExecutor executor =
                new ThreadPoolExecutor(2, 4, 60,
                        TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        RestaurantUtil.deleteAll(firestore.collection("restaurants"));
    }
}
