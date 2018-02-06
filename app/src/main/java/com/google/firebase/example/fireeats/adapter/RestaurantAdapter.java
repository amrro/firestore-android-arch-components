package com.google.firebase.example.fireeats.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.common.DataListAdapter;
import com.google.firebase.example.fireeats.databinding.ItemRestaurantBinding;
import com.google.firebase.example.fireeats.model.Restaurant;


public final class RestaurantAdapter extends DataListAdapter<Restaurant, ItemRestaurantBinding> {

    private RestaurantClickHandler restaurantClickHandler;

    public RestaurantAdapter(Activity activity) {
        this.restaurantClickHandler = new RestaurantClickHandler(activity);
    }

    @Override
    protected ItemRestaurantBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return DataBindingUtil.inflate(inflater, R.layout.item_restaurant, parent, false);
    }

    @Override
    protected void bind(ItemRestaurantBinding binding, Restaurant item) {
        binding.setRestaurant(item);
        binding.setRestaurantClickHandler(restaurantClickHandler);
    }

}
