package com.google.firebase.example.fireeats.di;

import com.google.firebase.example.fireeats.FriendlyEatsApp;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;


@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        FirebaseModule.class,
        ActivitiesModule.class,
})
public interface AppComponent {
    void inject(FriendlyEatsApp app);
}
