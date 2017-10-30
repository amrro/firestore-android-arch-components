package com.google.firebase.example.fireeats.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.common.DataListAdapter;
import com.google.firebase.example.fireeats.common.OnItemClickedListener;
import com.google.firebase.example.fireeats.databinding.ItemRestaurantBinding;
import com.google.firebase.example.fireeats.model.Restaurant;


public final class RestaurantAdapter extends DataListAdapter<Restaurant, ItemRestaurantBinding> {
    public RestaurantAdapter(OnItemClickedListener<Restaurant> listener) {
        super(listener);
    }

    @Override
    protected ItemRestaurantBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        final ItemRestaurantBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_restaurant, parent, false);
        binding.getRoot().setOnClickListener(v -> {
            final Restaurant chosen = binding.getRestaurant();
            if (chosen != null) {
                listener.onClicked(chosen);
            }
        });
        return binding;
    }

    @Override
    protected void bind(ItemRestaurantBinding binding, Restaurant item) {
        binding.setRestaurant(item);
    }
}
