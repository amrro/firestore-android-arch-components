package com.google.firebase.example.fireeats.ui.detail;

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
import com.google.firebase.example.fireeats.repo.RestaurantRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import timber.log.Timber;

public class RestaurantDetailActivity extends AppCompatActivity
        implements /*EventListener<DocumentSnapshot>,*/ RatingDialogFragment.RatingListener {
    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";

    private ActivityRestaurantDetailBinding binding;
    private RatingDialogFragment mRatingDialog;

    private RatingAdapter mRatingAdapter;
    private RestaurantRepository repository;
    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_detail);

        // Get restaurant ID from extras
        //noinspection ConstantConditions
        restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID, null);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }
        repository = new RestaurantRepository(FirebaseFirestore.getInstance());

        binding.setHandler(backPressed -> {
            if (backPressed) {
                onBackPressed();
                return;
            }
            mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
        });

        initRecycler(repository.ratings(restaurantId));

        repository.restaurant(restaurantId).observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccessful()) {
                    binding.setRestaurant(resource.data());
                } else {
                    Toast.makeText(this, resource.error().getMessage(), Toast.LENGTH_SHORT).show();
                    Timber.e(resource.error());
                }
            }
        });

        mRatingDialog = new RatingDialogFragment();
    }

    private void initRecycler(Query ratingsQuery) {
        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                binding.setNoReviews(getItemCount() == 0);
            }
        };
        binding.recyclerRatings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRatings.setAdapter(mRatingAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mRatingAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRatingAdapter.stopListening();
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
        repository.addRating(restaurantId, rating).observe(this, resource -> {
            hideKeyboard();
            if (resource.isSuccessful()) {
                // scroll to top
                Timber.d("Rating added");
                binding.recyclerRatings.smoothScrollToPosition(0);
            } else {
                // Show failure message
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
