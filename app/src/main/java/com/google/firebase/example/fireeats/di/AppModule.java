package com.google.firebase.example.fireeats.di;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by amrro <amr.elghobary@gmail.com> on 7/22/17.
 * <p>
 * Needed dependencies for some android components.
 */
@SuppressWarnings("WeakerAccess")
@Module(includes = ViewModelModule.class)
public class AppModule {

    @NonNull
    private Application application;

    public AppModule(@NonNull Application app) {
        this.application = app;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return application;
    }

/*    @Singleton
    @Provides
    public SharedPreferences providesSharedPreferences(@NonNull Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }*/

}
