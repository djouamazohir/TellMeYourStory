package com.zohirdev.mystory.activities.sff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.HomeStoriesRecyclerAdapter;
import com.zohirdev.mystory.items.StoriesItem;

import java.util.ArrayList;
import java.util.Objects;

public class StoriesActivity extends AppCompatActivity {

    FirebaseFirestore db;

    String uid;
    Intent intent;

    RecyclerView recyclerView;
    ArrayList<StoriesItem> storiesItemArrayList;
    HomeStoriesRecyclerAdapter homeStoriesRecyclerAdapter;

    ShimmerFrameLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        Objects.requireNonNull(getSupportActionBar()).hide();
        db = FirebaseFirestore.getInstance();

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        intent = getIntent();
        uid = intent.getStringExtra("uid");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        storiesItemArrayList = new ArrayList<>();

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        loadStories();


    }

    public void loadStories() {
        db.collection("STORIES")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (storiesItemArrayList.size() > 0) storiesItemArrayList.clear();
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StoriesItem storiesItem = new StoriesItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("story"),
                                    snapshot.getString("time"),
                                    snapshot.getString("date"),
                                    snapshot.getString("publisher")
                            );
                            if (storiesItem.getPublisher().equals(uid)) {
                                storiesItemArrayList.add(storiesItem);
                            }
                        }
                        homeStoriesRecyclerAdapter = new HomeStoriesRecyclerAdapter(StoriesActivity.this, storiesItemArrayList);
                        recyclerView.setAdapter(homeStoriesRecyclerAdapter);

                        container.stopShimmer();
                        container.setVisibility(View.GONE);
                    }
                });
    }
}