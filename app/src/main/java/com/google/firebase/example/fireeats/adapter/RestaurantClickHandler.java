package com.google.firebase.example.fireeats.adapter;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.ui.detail.RestaurantDetailActivity;

/**
 * Created by Ahmed Abd-Elmeged on 2/6/2018.
 */

/**
 * Click handler for the restaurant click in {@link RestaurantAdapter}
 */
public class RestaurantClickHandler {

    private Activity activity;

    RestaurantClickHandler(Activity activity) {
        this.activity = activity;
    }

    public void onRestaurantClicked(Restaurant restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(activity, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurant.id);

        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

}
