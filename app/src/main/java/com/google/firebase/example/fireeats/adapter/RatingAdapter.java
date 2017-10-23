package com.google.firebase.example.fireeats.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.ItemRatingBinding;
import com.google.firebase.example.fireeats.model.Rating;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * RecyclerView adapter for a list of {@link Rating}.
 */
public class RatingAdapter extends FirestoreAdapter<RatingAdapter.ViewHolder> {
    protected RatingAdapter(Query query) {
        super(query);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ItemRatingBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_rating, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Rating.class));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        private final ItemRatingBinding binding;

        ViewHolder(ItemRatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Rating rating) {
            this.binding.setRate(rating);
            if (rating.getTimestamp() != null) {
                binding.ratingItemDate.setText(FORMAT.format(rating.getTimestamp()));
            }
        }
    }

}
