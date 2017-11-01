package com.google.firebase.example.fireeats.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Restaurant POJO.
 */
@IgnoreExtraProperties
public class Restaurant extends Model {

    public static final String FIELD_CITY = "city";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    public String name;
    public String city;
    public String category;
    public String photo;
    public int price;
    public int numRatings;
    public double avgRating;

    public Restaurant() {
    }

    public Restaurant(String name, String city, String category, String photo,
                      int price, int numRatings, double avgRating) {
        this.name = name;
        this.city = city;
        this.category = category;
        this.price = price;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
    }
}
