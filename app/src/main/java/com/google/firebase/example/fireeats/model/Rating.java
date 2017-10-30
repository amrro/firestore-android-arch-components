package com.google.firebase.example.fireeats.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Model POJO for a rating.
 */
public class Rating extends Model {

    public String userId;
    public String userName;
    public double rating;
    public String text;
    public @ServerTimestamp
    Date timestamp;

    public Rating() {
    }

    public Rating(FirebaseUser user, double rating, String text) {
        this.userId = user.getUid();
        this.userName = user.getDisplayName();
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.getEmail();
        }

        this.rating = rating;
        this.text = text;
    }
}
