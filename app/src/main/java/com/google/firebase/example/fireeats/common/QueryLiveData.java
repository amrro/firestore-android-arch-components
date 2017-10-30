package com.google.firebase.example.fireeats.common;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.google.firebase.example.fireeats.model.Model;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public final class QueryLiveData<T extends Model>
        extends LiveData<Resource<List<T>>> implements EventListener<QuerySnapshot> {

    private final Query query;
    private final Class<T> type;
    private ListenerRegistration registration;

    public QueryLiveData(Query query, Class<T> type) {
        this.query = query;
        this.type = type;
    }

    @Override
    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
        if (e != null) {
            setValue(new Resource<>(e));
            return;
        }
        setValue(new Resource<>(documentToList(snapshots)));
    }

    @Override
    protected void onActive() {
        super.onActive();
        registration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    @NonNull
    private List<T> documentToList(QuerySnapshot snapshots) {
        final List<T> retList = new ArrayList<>();
        if (snapshots.isEmpty()) {
            return retList;
        }

        for (DocumentSnapshot document : snapshots.getDocuments()) {
            retList.add(document.toObject(type).withId(document.getId()));
        }

        return retList;
    }
}
