package com.google.firebase.example.fireeats.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.adapter.RestaurantAdapter;
import com.google.firebase.example.fireeats.databinding.ActivityMainBinding;
import com.google.firebase.example.fireeats.repo.Repository;
import com.google.firebase.example.fireeats.ui.detail.RestaurantDetailActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Collections;

public class MainActivity extends AppCompatActivity implements
        FilterDialogFragment.FilterListener,
        RestaurantAdapter.OnRestaurantSelectedListener {

    private static final int RC_SIGN_IN = 9001;

    private ActivityMainBinding binding;
    private FilterDialogFragment mFilterDialog;
    private RestaurantAdapter mAdapter;
    private RestaurantsViewModel viewModel;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        // View model
        viewModel = ViewModelProviders.of(this).get(RestaurantsViewModel.class);
        repository = new Repository(FirebaseFirestore.getInstance());
        initRecycler();

        binding.setHandler(clear -> {
            if (clear) {
                mFilterDialog.resetFilters();
                onFilter(Filters.getDefault());
            } else {
                // Show the dialog containing filter options
                mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
            }
        });

        viewModel.isSignedIn().observe(this, isSigned -> {
            // Start sign in if necessary
            //noinspection ConstantConditions
            if (! isSigned) {
                startSignIn();
                return;
            }

            // Apply filters
            onFilter(viewModel.getFilters());

            // Start listening for Firestore updates
            if (mAdapter != null) {
                mAdapter.startListening();
            }
        });

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    private void initRecycler() {
        // RecyclerView
        mAdapter = new RestaurantAdapter(repository.restaurants(), this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                binding.setNoData(getItemCount() == 0);
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_items:
                repository.addRestaurants(this);
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
            case R.id.menu_delete:
                repository.deleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                startSignIn();
            }
        }
    }

    @Override
    public void onRestaurantSelected(DocumentSnapshot restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurant.getId());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onFilter(Filters filters) {
        // Update the query
        mAdapter.setQuery(repository.restaurants(filters));

        // Set header
        binding.textCurrentSearch.setText(Html.fromHtml(filters.getSearchDescription(this)));
        binding.textCurrentSortBy.setText(filters.getOrderDescription(this));

        // Save filters
        viewModel.setFilters(filters);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }
}
