package com.google.firebase.example.fireeats.repo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.example.fireeats.common.CompletionLiveData;
import com.google.firebase.example.fireeats.common.DocumentLiveData;
import com.google.firebase.example.fireeats.common.QueryLiveData;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public final class RestaurantRepository {
    private final CollectionReference restaurants;

    public RestaurantRepository() {
        FirebaseFirestore.setLoggingEnabled(true);
        this.restaurants = FirebaseFirestore.getInstance().collection("restaurants");

    }

    private Query query(final String id) {
        return restaurants.document(id)
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);
    }

    public QueryLiveData<Rating> ratings(final String id) {
        return new QueryLiveData<>(query(id), Rating.class);
    }

    public DocumentLiveData<Restaurant> restaurant(final String id) {
        if (id == null) {
            return null;
        }
        final DocumentReference restaurantRef = restaurants.document(id);
        DocumentLiveData<Restaurant> data = new DocumentLiveData<>(restaurantRef, Restaurant.class);
        restaurantRef.addSnapshotListener(data);
        return data;
    }

    public CompletionLiveData addRating(final String restaurantId, final Rating rating) {
        final CompletionLiveData completion = new CompletionLiveData();
        addRating(restaurants.document(restaurantId), rating).addOnCompleteListener(completion);
        return completion;
    }

    private Task<Void> addRating(final DocumentReference restaurantRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = restaurantRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return restaurants.getFirestore().runTransaction(transaction -> {
            Restaurant restaurant = transaction.get(restaurantRef).toObject(Restaurant.class);

            // Compute new number of ratings
            int newNumRatings = restaurant.numRatings + 1;

            // Compute new average rating
            double oldRatingTotal = restaurant.avgRating * restaurant.numRatings;
            double newAvgRating = (oldRatingTotal + rating.rating) / newNumRatings;

            // Set new restaurant info
            restaurant.numRatings = newNumRatings;
            restaurant.avgRating = newAvgRating;

            // Commit to Firestore
            transaction.set(restaurantRef, restaurant);
            transaction.set(ratingRef, rating);

            return null;
        });
    }
}
