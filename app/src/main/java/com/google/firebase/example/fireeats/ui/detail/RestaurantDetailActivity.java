package com.google.firebase.example.fireeats.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.adapter.RatingAdapter;
import com.google.firebase.example.fireeats.databinding.ActivityRestaurantDetailBinding;
import com.google.firebase.example.fireeats.model.Rating;

import timber.log.Timber;

public class RestaurantDetailActivity extends AppCompatActivity
        implements RatingDialogFragment.RatingListener {
    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";

    private ActivityRestaurantDetailBinding binding;
    private RatingDialogFragment mRatingDialog;
    private RatingAdapter adapter;
    private RatingViewModel viewModel;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_detail);

        // Get restaurant ID from extras
        final String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID, null);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }
        initRecycler();

        viewModel = ViewModelProviders.of(this).get(RatingViewModel.class);
        viewModel.setRestaurantId(restaurantId).ratings().observe(this, listResource -> {
            if (listResource.isSuccessful()) {
                adapter.replace(listResource.data());
            }
        });
        viewModel.restaurant().observe(this, response -> {
            if (response.isSuccessful()) {
                binding.setRestaurant(response.data());
            } else {
                Toast.makeText(this, response.error().getMessage(), Toast.LENGTH_SHORT).show();
                Timber.e(response.error());
            }
        });

        binding.setHandler(backPressed -> {
            if (backPressed) {
                onBackPressed();
                return;
            }
            mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
        });

        mRatingDialog = new RatingDialogFragment();
    }

    private void initRecycler() {
        adapter = new RatingAdapter();
        binding.recyclerRatings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRatings.setAdapter(adapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        viewModel.addRating(rating).observe(this, resource -> {
            hideKeyboard();
            if (resource.isSuccessful()) {
                // scroll to top
                Timber.d("Rating added");
                binding.recyclerRatings.smoothScrollToPosition(0);
            } else {
                Timber.e(resource.error());
                Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            final InputMethodManager service = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (service != null) {
                service.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
