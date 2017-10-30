package com.google.firebase.example.fireeats.repo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.example.fireeats.CompletionLiveData;
import com.google.firebase.example.fireeats.DocumentLiveData;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public final class RestaurantRepository {
    private final CollectionReference restaurants;

    public RestaurantRepository(final FirebaseFirestore firestore) {
        FirebaseFirestore.setLoggingEnabled(true);
        this.restaurants = firestore.collection("restaurants");

    }

    public Query ratings(final String id) {
        return restaurants.document(id)
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);
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
            int newNumRatings = restaurant.getNumRatings() + 1;

            // Compute new average rating
            double oldRatingTotal = restaurant.getAvgRating() * restaurant.getNumRatings();
            double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

            // Set new restaurant info
            restaurant.setNumRatings(newNumRatings);
            restaurant.setAvgRating(newAvgRating);

            // Commit to Firestore
            transaction.set(restaurantRef, restaurant);
            transaction.set(ratingRef, rating);

            return null;
        });
    }
}
