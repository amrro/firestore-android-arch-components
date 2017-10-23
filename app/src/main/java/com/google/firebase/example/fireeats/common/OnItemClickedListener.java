package com.google.firebase.example.fireeats.common;

import android.support.v7.widget.RecyclerView;

/**
 * Created by amrro <amr.elghobary@gmail.com> on 9/15/17.
 * General interface callback for handling clicks inside {@link RecyclerView}
 */

public interface OnItemClickedListener<T> {
    void onClicked(T item);
}
