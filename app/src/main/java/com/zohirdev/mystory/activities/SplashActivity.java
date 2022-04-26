package com.zohirdev.mystory.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.login.CreateNewAccountActivity;

import java.util.Locale;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesLanguage;
    boolean night;
    String language;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Objects.requireNonNull(getSupportActionBar()).hide();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        night = sharedPreferences.getBoolean("night", false);

        sharedPreferencesLanguage = getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        language = sharedPreferencesLanguage.getString("language", "ar");

        if (night) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (language.equals("ar")) {
            setLocale(SplashActivity.this, "ar");
        } else {
            if (language.equals("en")) {
                setLocale(SplashActivity.this, "en");
            } else {
                if (language.equals("fr")) {
                    setLocale(SplashActivity.this, "fr");
                }
            }
        }

        if (firebaseUser == null) {
            Intent intent;
            intent = new Intent(SplashActivity.this, CreateNewAccountActivity.class);
            startActivity(intent);
        } else {
            Intent intent;
            intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("uid", firebaseAuth.getUid());
            startActivity(intent);
        }
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}