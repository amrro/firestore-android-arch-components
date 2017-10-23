package com.google.firebase.example.fireeats.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.ItemRestaurantBinding;
import com.google.firebase.example.fireeats.model.Restaurant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class RestaurantAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder> {
    public interface OnRestaurantSelectedListener {
        void onRestaurantSelected(DocumentSnapshot restaurant);
    }

    private OnRestaurantSelectedListener mListener;

    protected RestaurantAdapter(Query query, @NonNull OnRestaurantSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemRestaurantBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_restaurant, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemRestaurantBinding binding;

        ViewHolder(ItemRestaurantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final DocumentSnapshot snapshot,
                  final OnRestaurantSelectedListener listener) {
            this.binding.setRestaurant(snapshot.toObject(Restaurant.class));
            // Click listener
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onRestaurantSelected(snapshot);
                }
            });
        }
    }
}
