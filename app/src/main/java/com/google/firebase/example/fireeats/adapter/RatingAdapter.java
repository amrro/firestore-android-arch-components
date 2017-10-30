package com.google.firebase.example.fireeats.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.common.DataListAdapter;
import com.google.firebase.example.fireeats.databinding.ItemRatingBinding;
import com.google.firebase.example.fireeats.model.Rating;


public final class RatingAdapter extends DataListAdapter<Rating, ItemRatingBinding> {
    public RatingAdapter() {
        super(item -> {
        });
    }

    @Override
    protected ItemRatingBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return DataBindingUtil.inflate(inflater, R.layout.item_rating, parent, false);
    }

    @Override
    protected void bind(ItemRatingBinding binding, Rating item) {
        binding.setRate(item);
    }
}
