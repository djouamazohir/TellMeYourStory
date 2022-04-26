package com.zohirdev.mystory.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.sff.FollowersActivity;
import com.zohirdev.mystory.activities.sff.FollowingActivity;
import com.zohirdev.mystory.activities.sff.StoriesActivity;
import com.zohirdev.mystory.items.FollowersItem;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.UsersItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class UsersProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ImageView imageProfile;
    ImageView imageCover;
    TextView textFullName;
    TextView textBio;
    TextView textUsername;
    TextView textLinks;

    String myUid;
    String userUid;
    String userToken;

    Button buttonBack;

    Button buttonFollow;
    ProgressBar progressBarFollow;
    CardView cardFollow;

    boolean follow;


    LinearLayout linearStories;
    LinearLayout linearFollowers;
    LinearLayout linearFollowing;

    TextView textStories;
    TextView textFollowers;
    TextView textFollowing;

    int posts;
    int followers;
    int following;

    ImageView imageAccountVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        Objects.requireNonNull(getSupportActionBar()).hide();
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
        userUid = getIntent().getStringExtra("uid");

        db = FirebaseFirestore.getInstance();


        imageAccountVerified = findViewById(R.id.imageAccountVerified);

        imageProfile = findViewById(R.id.imageProfile);
        imageCover = findViewById(R.id.imageCover);
        textFullName = findViewById(R.id.textFullName);
        textBio = findViewById(R.id.textBio);
        textUsername = findViewById(R.id.textUsername);
        textLinks = findViewById(R.id.textLink);

        Linkify.addLinks(textBio, Linkify.ALL);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        buttonFollow = findViewById(R.id.buttonFollow);
        progressBarFollow = findViewById(R.id.progressBarFollow);
        cardFollow = findViewById(R.id.cardFollow);

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
                intent = new Intent(UsersProfileActivity.this, StoriesActivity.class);
                intent.putExtra("uid", userUid);
                startActivity(intent);
            }
        });

        linearFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(UsersProfileActivity.this, FollowingActivity.class);
                intent.putExtra("uid", userUid);
                startActivity(intent);
            }
        });

        linearFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(UsersProfileActivity.this, FollowersActivity.class);
                intent.putExtra("uid", userUid);
                startActivity(intent);
            }
        });


        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow) {
                    progressBarFollow.setVisibility(View.VISIBLE);
                    buttonFollow.setVisibility(View.GONE);
                    db.collection("FOLLOWERS")
                            .document(myUid + userUid)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(UsersProfileActivity.this, "تم إلغاء المتابعة", Toast.LENGTH_SHORT).show();
                                    buttonFollow.setVisibility(View.VISIBLE);
                                    progressBarFollow.setVisibility(View.GONE);
                                    buttonFollow.setText("متابعة");
                                    cardFollow.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                    follow = false;
                                }
                            });
                } else {
                    progressBarFollow.setVisibility(View.VISIBLE);
                    buttonFollow.setVisibility(View.GONE);

                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("user", myUid);
                    dataMap.put("follow", userUid);

                    db.collection("FOLLOWERS")
                            .document("" + myUid + userUid)
                            .set(dataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(UsersProfileActivity.this, "متابع", Toast.LENGTH_SHORT).show();
                                    buttonFollow.setVisibility(View.VISIBLE);
                                    progressBarFollow.setVisibility(View.GONE);
                                    buttonFollow.setText("إلغاء المتابعة");
                                    cardFollow.setCardBackgroundColor(getResources().getColor(R.color.follow));
                                    follow = true;

                                    long id = new Date().getTime();

                                    LocalDate today = LocalDate.now();
                                    LocalTime now = LocalTime.now();

                                    String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                                    String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));


                                    HashMap<String, String> dataMap = new HashMap<>();
                                    dataMap.put("notificationID", id + firebaseAuth.getUid());
                                    dataMap.put("notificationType", "FOLLOW");
                                    dataMap.put("order", "" + id);
                                    dataMap.put("storyID", "");
                                    dataMap.put("sender", firebaseAuth.getUid());
                                    dataMap.put("receiver", userUid);
                                    dataMap.put("seen", "NO");
                                    dataMap.put("time", time);
                                    dataMap.put("date", date);

                                    db.collection("NOTIFICATIONS")
                                            .document(id + firebaseAuth.getUid())
                                            .set(dataMap);
                                }
                            });
                }

                countFollowersNumber();

            }
        });
        loadUserInfo();
        loadFollowing();

        countPostsNumber();
        countFollowersNumber();
        countFollowingNumber();


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
                            if (followersItem.getUser().equals(userUid)) {
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
                            if (followersItem.getFollow().equals(userUid)) {
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
                            if (storiesItem.getPublisher().equals(userUid)) {
                                posts = posts + 1;
                            }
                        }
                        textStories.setText("" + posts);
                    }
                });
    }


    private void loadFollowing() {
        db.collection("FOLLOWERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            FollowersItem followersItem = new FollowersItem(
                                    snapshot.getString("user"),
                                    snapshot.getString("follow")
                            );
                            if (followersItem.getUser().equals(myUid) && followersItem.getFollow().equals(userUid)) {
                                follow = true;
                            }
                        }
                        if (follow) {
                            cardFollow.setCardBackgroundColor(getResources().getColor(R.color.follow));
                            buttonFollow.setText("إلغاء المتابعة");
                        } else {
                            cardFollow.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            buttonFollow.setText("متابعة");
                        }
                    }
                });
    }

    public void loadUserInfo() {
        db.collection("USERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
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
                            if (usersItem.getUid().equals(userUid)) {
                                Picasso.get().load(usersItem.getImageProfile()).into(imageProfile);
                                Picasso.get().load(usersItem.getImageCover()).into(imageCover);
                                textFullName.setText(usersItem.getFullName());
                                textUsername.setText("@" + usersItem.getUserName());
                                userToken = usersItem.getToken();

                                if (usersItem.getBio().equals("")) {
                                    textBio.setVisibility(View.GONE);
                                } else {
                                    textBio.setText(usersItem.getBio());
                                }
                                if (usersItem.getLinks().equals("")) {
                                    textLinks.setVisibility(View.GONE);
                                } else {
                                    textLinks.setText(usersItem.getLinks());

                                }

                                if (usersItem.getVerified().equals("YES")) {
                                    imageAccountVerified.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }
}