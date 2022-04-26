package com.zohirdev.mystory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.dashboard.AllUsersActivity;
import com.zohirdev.mystory.activities.dashboard.DashBoardActivity;
import com.zohirdev.mystory.activities.dashboard.NotificationsActivity;
import com.zohirdev.mystory.activities.dashboard.SearchActivity;
import com.zohirdev.mystory.adapters.HomeStoriesRecyclerAdapter;
import com.zohirdev.mystory.items.FollowersItem;
import com.zohirdev.mystory.items.NotificationsItem;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ImageView imageProfile;
    String uid;

    CardView cardProfile;
    TextView textAddNewStory;
    CardView cardSearch;
    CardView cardNotification;
    CardView cardMenu;

    ArrayList<String> followingArrayList;
    RecyclerView recyclerViewStories;
    ArrayList<StoriesItem> storiesItemArrayList;
    HomeStoriesRecyclerAdapter homeStoriesRecyclerAdapter;

    ShimmerFrameLayout container;

    Button buttonFindUsers;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity = null;

    TextView textNotifications;
    int notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Objects.requireNonNull(getSupportActionBar()).hide();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = firebaseAuth.getUid();

        activity = this;

        buttonFindUsers = findViewById(R.id.buttonFindUsers);
        buttonFindUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(HomeActivity.this, AllUsersActivity.class);
                startActivity(intent);
            }
        });
        cardProfile = findViewById(R.id.cardProfile);
        textAddNewStory = findViewById(R.id.textAddNewStory);
        cardSearch = findViewById(R.id.cardSearch);
        cardNotification = findViewById(R.id.cardNotification);
        cardMenu = findViewById(R.id.cardMenu);
        followingArrayList = new ArrayList<>();

        cardProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });

        cardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(HomeActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        });

        textAddNewStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(HomeActivity.this, AddNewStoryActivity.class);
                startActivity(intent);
            }
        });

        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        textNotifications = findViewById(R.id.textNotification);
        textNotifications.setVisibility(View.GONE);

        imageProfile = findViewById(R.id.imageProfile);

        recyclerViewStories = findViewById(R.id.recyclerView);
        recyclerViewStories.setHasFixedSize(false);
        recyclerViewStories.setLayoutManager(new LinearLayoutManager(this));
        storiesItemArrayList = new ArrayList<>();

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        loadImageProfile();
        loadStories();
        loadFollowing();
        countNotifications();

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                ha.postDelayed(this, 200);
                countNotifications();
            }
        }, 200);
    }

    private void countNotifications() {
        db.collection("NOTIFICATIONS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        notifications = 0;
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            NotificationsItem notificationsItem =
                                    new NotificationsItem(
                                            snapshot.getString("notificationID"),
                                            snapshot.getString("notificationType"),
                                            snapshot.getString("order"),
                                            snapshot.getString("storyID"),
                                            snapshot.getString("sender"),
                                            snapshot.getString("receiver"),
                                            snapshot.getString("seen"),
                                            snapshot.getString("time"),
                                            snapshot.getString("date")
                                    );
                            if (notificationsItem.getReceiver().equals(firebaseAuth.getUid()) &&
                                    notificationsItem.getSeen().equals("NO")) {
                                notifications = notifications + 1;
                            }
                        }
                        if (notifications != 0) {
                            textNotifications.setVisibility(View.VISIBLE);
                            textNotifications.setText(notifications + "");
                        } else {
                            textNotifications.setVisibility(View.GONE);
                        }

                    }
                });
    }

    private void loadFollowing() {
        db.collection("FOLLOWERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (followingArrayList.size() > 0) followingArrayList.clear();
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            FollowersItem followersItem = new FollowersItem(
                                    snapshot.getString("user"),
                                    snapshot.getString("follow")
                            );
                            if (followersItem.getUser().equals(uid)) {
                                followingArrayList.add(followersItem.getFollow());
                            }
                        }
                        loadStories();
                    }
                });
    }

    public void loadStories() {

        db.collection("STORIES")
                .orderBy("storyID", Query.Direction.DESCENDING)
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
                            if (followingArrayList.contains(storiesItem.getPublisher())) {
                                storiesItemArrayList.add(storiesItem);
                            }
                        }
                        homeStoriesRecyclerAdapter = new HomeStoriesRecyclerAdapter(HomeActivity.this, storiesItemArrayList);
                        recyclerViewStories.setAdapter(homeStoriesRecyclerAdapter);

                        container.stopShimmer();
                        container.setVisibility(View.GONE);

                    }
                });
    }

    private void loadImageProfile() {
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
                            if (usersItem.getUid().equals(uid)) {
                                Picasso.get().load(usersItem.getImageProfile()).into(imageProfile);
                            }
                        }
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        activity = null;

    }
}