package com.zohirdev.mystory.activities.dashboard;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.HomeActivity;
import com.zohirdev.mystory.activities.MyProfileActivity;
import com.zohirdev.mystory.activities.SplashActivity;

import java.util.Locale;
import java.util.Objects;

public class LanguageActivity extends AppCompatActivity {

    CardView cardArabic;
    CardView cardEnglish;
    CardView cardFrench;

    String language;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferences = getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        language = sharedPreferences.getString("language", "ar");

        cardArabic = findViewById(R.id.cardArabic);
        cardEnglish = findViewById(R.id.cardEnglish);
        cardFrench = findViewById(R.id.cardFrench);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cardArabic.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                changeLanguage("ar");
            }
        });

        cardEnglish.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                changeLanguage("en");
            }
        });

        cardFrench.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                changeLanguage("fr");
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("CommitPrefEdits")
    private void changeLanguage(String language) {
        editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
        try {
            DashBoardActivity.activity.finish();
            MyProfileActivity.activity.finish();
            HomeActivity.activity.finish();
        } catch (Exception ignored) {
        }
        Intent intent;
        intent = new Intent(LanguageActivity.this, SplashActivity.class);
        startActivity(intent);

        finish();

    }
}