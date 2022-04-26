package com.zohirdev.mystory.activities;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.SearchUserRecyclerAdapter;
import com.zohirdev.mystory.items.ReactStoryItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class ShowLikesListActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    ArrayList<UsersItem> usersItemArrayList;
    ArrayList<String> likesListArrayList;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter searchUserRecyclerAdapter;

    String storyID;

    ShimmerFrameLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_likes_list);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        usersItemArrayList = new ArrayList<>();
        likesListArrayList = new ArrayList<>();

        storyID = getIntent().getStringExtra("storyID");

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        loadLikesList();


    }

    private void loadLikesList() {
        db.collection("STORY_REACT")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (likesListArrayList.size()>0) likesListArrayList.clear();
                        if (usersItemArrayList.size()>0) usersItemArrayList.clear();
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            ReactStoryItem reactStoryItem = new ReactStoryItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("userID"),
                                    snapshot.getString("reactType")
                            );
                            if (reactStoryItem.getStoryID().equals(storyID)) {
                                likesListArrayList.add(reactStoryItem.getUserID());
                            }
                        }

                        db.collection("USERS")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                            UsersItem usersItem = new UsersItem(
                                                    snapshot.getString("fullName"),
                                                    snapshot.getString("username"),
                                                    snapshot.getString("uid"),
                                                    snapshot.getString("token"),
                                                    snapshot.getString("phone"),
                                                    snapshot.getString("imageProfile"),
                                                    snapshot.getString("imageCover"),
                                                    snapshot.getString("links"),
                                                    snapshot.getString("bio"),
                                                    snapshot.getString("language"),
                                                    snapshot.getString("isBlocked"),
                                                    snapshot.getString("deviceID"),
                                                    snapshot.getString("verified")
                                            );
                                            if (likesListArrayList.contains(usersItem.getUid())) {
                                                usersItemArrayList.add(usersItem);
                                            }
                                        }
                                        searchUserRecyclerAdapter = new SearchUserRecyclerAdapter(ShowLikesListActivity.this, usersItemArrayList);
                                        recyclerView.setAdapter(searchUserRecyclerAdapter);

                                        container.stopShimmer();
                                        container.setVisibility(View.GONE);
                                    }
                                });
                    }
                });

    }
}