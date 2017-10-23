package com.google.firebase.example.fireeats.common;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public final class BindingAdapters {
    private BindingAdapters() {
    }

    @BindingAdapter("showView")
    public static void showView(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("hideView")
    public static void hideView(View view, boolean hide) {
        showView(view, ! hide);
    }

    @BindingAdapter("imageUrl")
    public static void setImage(ImageView image, final String url) {
        // Load image
        Glide.with(image.getContext())
                .load(url)
                .into(image);
    }

    @BindingAdapter("setRating")
    public static void setRating(MaterialRatingBar ratingBar, double rating) {
        ratingBar.setRating((float) rating);
    }
}
