package com.google.firebase.example.fireeats.ui.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.adapter.RestaurantAdapter;
import com.google.firebase.example.fireeats.common.BaseActivity;
import com.google.firebase.example.fireeats.databinding.ActivityMainBinding;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.example.fireeats.repo.MainRepository;
import com.google.firebase.example.fireeats.ui.detail.RestaurantDetailActivity;

import java.util.Collections;

public class MainActivity extends BaseActivity implements
        FilterDialogFragment.FilterListener {

    private static final int RC_SIGN_IN = 9001;

    private RestaurantAdapter adapter;
    private ActivityMainBinding binding;
    private FilterDialogFragment mFilterDialog;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        // View model
        viewModel = getViewModel(MainViewModel.class);
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
            if (! isSigned) startSignIn();
        });

        viewModel.restaurants().observe(this, listResource -> {
            if (listResource.isSuccessful()) {
                adapter.replace(listResource.data());
            }
        });

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    private void initRecycler() {
        adapter = new RestaurantAdapter(this::onRestaurantSelected);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: 10/30/17 respect the design remove repository from here.
        MainRepository repository = viewModel.getRepository();
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

    public void onRestaurantSelected(Restaurant restaurant) {
        // Go to the details page for the selected restaurant
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurant.id);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    @Override
    public void onFilter(Filters filters) {
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
