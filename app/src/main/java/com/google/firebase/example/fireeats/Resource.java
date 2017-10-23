package com.google.firebase.example.fireeats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
public final class Resource<T> {
    @Nullable
    private final T data;
    @Nullable
    private final Exception error;

    public Resource(@NonNull T data) {
        this(data, null);
    }

    public Resource(@NonNull Exception exception) {
        this(null, exception);
    }

    private Resource(@Nullable T value, @Nullable Exception error) {
        this.data = value;
        this.error = error;
    }

    public boolean isSuccessful() {
        return data != null && error == null;
    }

    @NonNull
    public T data() {
        if (error != null) {
            throw new IllegalStateException("error is not null. Call isSuccessful() first.");
        }
        return data;
    }

    @NonNull
    public Exception error() {
        if (data != null) {
            throw new IllegalStateException("data is not null. Call isSuccessful() first.");
        }
        return error;
    }
}
