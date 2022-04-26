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
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.FollowingUserRecyclerAdapter;
import com.zohirdev.mystory.items.FollowersItem;

import java.util.ArrayList;
import java.util.Objects;

public class FollowingActivity extends AppCompatActivity {

    FirebaseFirestore db;

    String uid;
    Intent intent;

    RecyclerView recyclerView;
    ArrayList<FollowersItem> followingItemArrayList;
    FollowingUserRecyclerAdapter followingUserRecyclerAdapter;

    ShimmerFrameLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follwing);

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

        followingItemArrayList = new ArrayList<>();

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        loadFollowing();

    }

    private void loadFollowing() {
        if (followingItemArrayList.size() > 0) followingItemArrayList.clear();
        db.collection("FOLLOWERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            FollowersItem followingItem = new FollowersItem(
                                    snapshot.getString("user"),
                                    snapshot.getString("follow")
                            );
                            if (followingItem.getUser().equals(uid)) {
                                followingItemArrayList.add(followingItem);
                            }
                        }
                        followingUserRecyclerAdapter = new FollowingUserRecyclerAdapter(FollowingActivity.this, followingItemArrayList);
                        recyclerView.setAdapter(followingUserRecyclerAdapter);

                        container.stopShimmer();
                        container.setVisibility(View.GONE);
                    }
                });
    }
}