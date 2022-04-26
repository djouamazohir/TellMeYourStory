package com.zohirdev.mystory.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.CommentsRecyclerAdapter;
import com.zohirdev.mystory.items.CommentsItem;
import com.zohirdev.mystory.items.UsersItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String storyID;
    String publisher;
    Intent intent;

    RecyclerView recyclerViewComments;
    ArrayList<CommentsItem> commentsItemArrayList;
    CommentsRecyclerAdapter commentsRecyclerAdapter;

    EditText textComment;
    Button buttonPostComment;
    Button buttonBack;

    String publisherToken;
    String myName;

    ShimmerFrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Objects.requireNonNull(getSupportActionBar()).hide();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        commentsItemArrayList = new ArrayList<>();
        intent = getIntent();
        storyID = intent.getStringExtra("storyID");
        publisher = intent.getStringExtra("publisher");

        recyclerViewComments = findViewById(R.id.recyclerView);
        recyclerViewComments.setHasFixedSize(false);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        buttonPostComment = findViewById(R.id.buttonPostComment);
        textComment = findViewById(R.id.textComment);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                String comment = textComment.getText().toString();
                long id = new Date().getTime();

                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();

                String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));

                if (!comment.isEmpty()) {
                    commentsItemArrayList.add(new CommentsItem(storyID, publisher, id + firebaseAuth.getUid() + storyID, firebaseAuth.getUid(), comment, time, date));
                    commentsRecyclerAdapter = new CommentsRecyclerAdapter(CommentsActivity.this, commentsItemArrayList);
                    recyclerViewComments.setAdapter(commentsRecyclerAdapter);
                    recyclerViewComments.scrollToPosition(commentsItemArrayList.size() - 1);
                    textComment.setText("");
                    PostComment(comment);
                }
            }
        });

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        loadComments();
        loadPublisherInfo();

    }

    private void loadPublisherInfo() {
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
                            if (usersItem.getUid().equals(publisher)) {
                                publisherToken = usersItem.getToken();
                            }
                            if (usersItem.getUid().equals(firebaseAuth.getUid())) {
                                myName = usersItem.getFullName();
                            }
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void PostComment(String comment) {
        long id = new Date().getTime();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));


        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("storyID", storyID);
        dataMap.put("publisher", publisher);
        dataMap.put("commentID", id + firebaseAuth.getUid() + storyID);
        dataMap.put("userID", firebaseAuth.getUid());
        dataMap.put("comment", comment);
        dataMap.put("time", time);
        dataMap.put("date", date);

        db.collection("STORY_COMMENTS")
                .document("" + id + firebaseAuth.getUid() + storyID)
                .set(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NewApi")
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!publisher.equals(firebaseAuth.getUid())){
                            addNotification();
                        }

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification() {
        long id = new Date().getTime();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));


        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("notificationID", id + firebaseAuth.getUid());
        dataMap.put("order", id + "");
        dataMap.put("notificationType", "POST_COMMENT");
        dataMap.put("storyID", storyID);
        dataMap.put("sender", firebaseAuth.getUid());
        dataMap.put("receiver", publisher);
        dataMap.put("seen", "NO");
        dataMap.put("time", time);
        dataMap.put("date", date);

        db.collection("NOTIFICATIONS")
                .document(id + firebaseAuth.getUid())
                .set(dataMap);
    }

    public void loadComments() {
        if (commentsItemArrayList.size() > 0) commentsItemArrayList.clear();
        db.collection("STORY_COMMENTS")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            CommentsItem commentsItem = new CommentsItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("publisher"),
                                    snapshot.getString("commentID"),
                                    snapshot.getString("userID"),
                                    snapshot.getString("comment"),
                                    snapshot.getString("time"),
                                    snapshot.getString("date")
                            );
                            if (commentsItem.getStoryID().equals(storyID)) {
                                commentsItemArrayList.add(commentsItem);
                            }
                        }
                        commentsRecyclerAdapter = new CommentsRecyclerAdapter(CommentsActivity.this, commentsItemArrayList);
                        recyclerViewComments.setAdapter(commentsRecyclerAdapter);

                        container.stopShimmer();
                        container.setVisibility(View.GONE);
                    }
                });
    }
}