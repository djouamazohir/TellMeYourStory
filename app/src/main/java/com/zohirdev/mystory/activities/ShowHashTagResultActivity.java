package com.zohirdev.mystory.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.HomeStoriesRecyclerAdapter;
import com.zohirdev.mystory.items.StoriesItem;

import java.util.ArrayList;
import java.util.Objects;

public class ShowHashTagResultActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    ArrayList<StoriesItem> storiesItemArrayList;
    HomeStoriesRecyclerAdapter homeStoriesRecyclerAdapter;

    TextView textHashTag;

    String hashTag;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_hash_tag_result);

        Objects.requireNonNull(getSupportActionBar()).hide();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storiesItemArrayList = new ArrayList<>();

        textHashTag = findViewById(R.id.textHashTag);
        hashTag = getIntent().getStringExtra("hashTag");
        textHashTag.setText("#" + hashTag);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadStories();


    }

    private void loadStories() {
        db.collection("STORIES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (storiesItemArrayList.size() > 0) storiesItemArrayList.clear();
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StoriesItem storiesItem =
                                    new StoriesItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("story"),
                                            snapshot.getString("time"),
                                            snapshot.getString("date"),
                                            snapshot.getString("publisher")
                                    );
                            if (storiesItem.getStory().contains(hashTag)) {
                                storiesItemArrayList.add(storiesItem);
                            }

                        }
                        homeStoriesRecyclerAdapter = new HomeStoriesRecyclerAdapter(ShowHashTagResultActivity.this, storiesItemArrayList);
                        recyclerView.setAdapter(homeStoriesRecyclerAdapter);

//                        container.stopShimmer();
//                        container.setVisibility(View.GONE);
                    }
                });
    }
}