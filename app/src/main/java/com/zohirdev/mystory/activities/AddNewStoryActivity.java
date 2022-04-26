package com.zohirdev.mystory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zohirdev.mystory.R;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AddNewStoryActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String uid;


    EditText textStory;

    CardView cardViewPost;
    ProgressBar progressBarPost;
    Button buttonPost;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_story);

        Objects.requireNonNull(getSupportActionBar()).hide();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        cardViewPost = findViewById(R.id.cardPost);
        progressBarPost = findViewById(R.id.progressBarPost);
        buttonPost = findViewById(R.id.buttonPost);
        buttonBack = findViewById(R.id.buttonBack);
        textStory = findViewById(R.id.textStory);

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String story = textStory.getText().toString();
                if (!story.isEmpty()) {
                    buttonPost.setVisibility(View.GONE);
                    progressBarPost.setVisibility(View.VISIBLE);
                    PostStory(story);

                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void PostStory(String story) {

        String storyID = new Date().getTime() + uid;

        HashMap<String, String> dataMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));

        dataMap.put("storyID", storyID);
        dataMap.put("story", story);
        dataMap.put("time", time);
        dataMap.put("date", date);
        dataMap.put("publisher", uid);

        db.collection("STORIES")
                .document("" + storyID)
                .set(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }
}