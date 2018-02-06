package com.google.firebase.example.fireeats.ui.detail;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.DialogRatingBinding;
import com.google.firebase.example.fireeats.model.Rating;

/**
 * Dialog Fragment containing rating form.
 */
public class RatingDialogFragment extends DialogFragment {

    public static final String TAG = "RatingDialog";

    interface RatingListener {
        void onRating(Rating rating);
    }

    public interface RatingDialogHandler {
        void addRate(boolean cancel, float rate, CharSequence text);
    }

    private RatingListener mRatingListener;
    private DialogRatingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_rating, container, false);
        binding.setHandler((cancel, rate, text) -> {
            if (! cancel) {
                final Rating rating = new Rating(FirebaseAuth.getInstance().getCurrentUser(), rate, text.toString());
                if (mRatingListener != null) {
                    mRatingListener.onRating(rating);
                }
            }
            dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            mRatingListener = (RatingListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
