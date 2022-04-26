package com.zohirdev.mystory.activities.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.NotesRecyclerAdapter;
import com.zohirdev.mystory.adapters.StoriesSavedRecyclerAdapter;
import com.zohirdev.mystory.items.NotesItem;
import com.zohirdev.mystory.items.StorySavedItem;

import java.util.ArrayList;
import java.util.Objects;

public class StoriesSavedActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String uid;

    RecyclerView recyclerViewStories;
    ArrayList<StorySavedItem> storiesItemArrayList;
    StoriesSavedRecyclerAdapter storiesSavedRecyclerAdapter;

    Button buttonBack;

    ShimmerFrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_saved);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = firebaseAuth.getUid();

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerViewStories = findViewById(R.id.recyclerView);
        recyclerViewStories.setHasFixedSize(false);
        recyclerViewStories.setLayoutManager(new LinearLayoutManager(this));
        storiesItemArrayList = new ArrayList<>();


        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        loadStoriesSaved();
    }

    private void loadStoriesSaved() {
        if (storiesItemArrayList.size() > 0) storiesItemArrayList.clear();
        db.collection("STORY_SAVED")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            StorySavedItem storiesItem = new StorySavedItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("userID"),
                                    snapshot.getString("publisher")
                            );
                            if (storiesItem.getUserID().equals(uid)) {
                                storiesItemArrayList.add(storiesItem);
                            }

                        }
                        storiesSavedRecyclerAdapter = new StoriesSavedRecyclerAdapter(StoriesSavedActivity.this, storiesItemArrayList);
                        recyclerViewStories.setAdapter(storiesSavedRecyclerAdapter);

                        container.stopShimmer();
                        container.setVisibility(View.GONE);
                    }
                });
    }
}