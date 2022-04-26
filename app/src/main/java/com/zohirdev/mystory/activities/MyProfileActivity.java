package com.zohirdev.mystory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.dashboard.DashBoardActivity;
import com.zohirdev.mystory.activities.dashboard.EditProfileActivity;
import com.zohirdev.mystory.activities.dashboard.StoriesSavedActivity;
import com.zohirdev.mystory.activities.login.CreateNewAccountActivity;
import com.zohirdev.mystory.activities.sff.FollowersActivity;
import com.zohirdev.mystory.activities.sff.FollowingActivity;
import com.zohirdev.mystory.activities.sff.StoriesActivity;
import com.zohirdev.mystory.items.FollowersItem;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ImageView imageProfile;
    ImageView imageCover;
    TextView textFullName;
    TextView textBio;
    TextView textUsername;
    TextView textPhone;
    TextView textLinks;

    String uid;

    CardView cardDashboard;

    LinearLayout linearStories;
    LinearLayout linearFollowers;
    LinearLayout linearFollowing;

    TextView textStories;
    TextView textFollowers;
    TextView textFollowing;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity = null;

    ImageView imageVerified;

    Button buttonBack;

    int posts;
    int followers;
    int following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Objects.requireNonNull(getSupportActionBar()).hide();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        activity = this;

        imageVerified = findViewById(R.id.imageAccountVerified);

        imageProfile = findViewById(R.id.imageProfile);
        imageCover = findViewById(R.id.imageCover);
        textFullName = findViewById(R.id.textFullName);
        textBio = findViewById(R.id.textBio);
        textUsername = findViewById(R.id.textUsername);
        textPhone = findViewById(R.id.textPhone);
        textLinks = findViewById(R.id.textLink);

        textStories = findViewById(R.id.textStories);
        textFollowers = findViewById(R.id.textFollowers);
        textFollowing = findViewById(R.id.textFollowing);

        linearStories = findViewById(R.id.linearStories);
        linearFollowers = findViewById(R.id.linearFollowers);
        linearFollowing = findViewById(R.id.linearFollowing);

        linearStories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MyProfileActivity.this, StoriesActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        linearFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MyProfileActivity.this, FollowingActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        linearFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MyProfileActivity.this, FollowersActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });


        cardDashboard = findViewById(R.id.cardDashboard);
        cardDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MyProfileActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        });

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadUserInfo();



        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                ha.postDelayed(this, 200);
                countPostsNumber();
                countFollowersNumber();
                countFollowingNumber();
            }
        }, 200);
    }

    private void countFollowingNumber() {

        db.collection("FOLLOWERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        following = 0;
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            FollowersItem followersItem = new FollowersItem(
                                    snapshot.getString("user"),
                                    snapshot.getString("follow")
                            );
                            if (followersItem.getUser().equals(uid)) {
                                following = following + 1;
                            }
                        }
                        textFollowing.setText("" + following);
                    }
                });
    }

    private void countFollowersNumber() {

        db.collection("FOLLOWERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        followers = 0;
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            FollowersItem followersItem = new FollowersItem(
                                    snapshot.getString("user"),
                                    snapshot.getString("follow")
                            );
                            if (followersItem.getFollow().equals(uid)) {
                                followers = followers + 1;
                            }
                        }
                        textFollowers.setText("" + followers);
                    }
                });
    }

    private void countPostsNumber() {

        db.collection("STORIES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        posts = 0;
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StoriesItem storiesItem = new StoriesItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("story"),
                                    snapshot.getString("time"),
                                    snapshot.getString("date"),
                                    snapshot.getString("publisher")
                            );
                            if (storiesItem.getPublisher().equals(uid)) {
                                posts = posts + 1;
                            }
                        }
                        textStories.setText("" + posts);
                    }
                });
    }

    public void loadUserInfo() {
        db.collection("USERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
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
                                Picasso.get().load(usersItem.getImageCover()).into(imageCover);
                                textFullName.setText(usersItem.getFullName());
                                textUsername.setText("@" + usersItem.getUserName());
                                textPhone.setText(usersItem.getPhone());

                                if (usersItem.getBio().equals("")) {
                                    textBio.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                                    textBio.setText("إضافة سيرة ذاتية");
                                    textBio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent;
                                            intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    textBio.setText(usersItem.getBio());
                                }
                                if (usersItem.getLinks().equals("")) {
                                    textLinks.setTextColor(getResources().getColor(R.color.holo_blue_dark));
                                    textLinks.setText("إضافة روابط");
                                    textBio.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent;
                                            intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    textLinks.setText(usersItem.getLinks());

                                }

                                if (usersItem.getVerified().equals("YES")) {
                                    imageVerified.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    @Override
    public void finish() {
        super.finish();
        activity = null;
    }
}