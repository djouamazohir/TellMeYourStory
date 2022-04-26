package com.zohirdev.mystory.activities.dashboard;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.NotificationsRecyclerAdapter;
import com.zohirdev.mystory.items.NotificationsItem;
import com.zohirdev.mystory.items.StoriesItem;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String uid;

    RecyclerView recyclerView;
    ArrayList<NotificationsItem> notificationsItemArrayList;
    NotificationsRecyclerAdapter notificationsRecyclerAdapter;

    ShimmerFrameLayout container;

    Button buttonNotifyLikes;
    Button buttonNotifyComments;
    Button buttonNotifyFollowings;

    String Selected = "LIKES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Objects.requireNonNull(getSupportActionBar()).hide();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        container = findViewById(R.id.shimmer_view_container);
        container.startShimmer();

        buttonNotifyLikes = findViewById(R.id.buttonNotifyLikes);
        buttonNotifyComments = findViewById(R.id.buttonNotifyComments);
        buttonNotifyFollowings = findViewById(R.id.buttonNotifyFollowings);

        buttonNotifyLikes.setBackgroundResource(R.drawable.button_change_tab_selected_bg);
        buttonNotifyComments.setBackgroundResource(R.drawable.button_change_tab_bg);
        buttonNotifyFollowings.setBackgroundResource(R.drawable.button_change_tab_bg);

        buttonNotifyLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selected = "LIKES";
                buttonNotifyLikes.setBackgroundResource(R.drawable.button_change_tab_selected_bg);
                buttonNotifyComments.setBackgroundResource(R.drawable.button_change_tab_bg);
                buttonNotifyFollowings.setBackgroundResource(R.drawable.button_change_tab_bg);
                loadNotification(Selected);
            }
        });
        buttonNotifyComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selected = "COMMENTS";
                buttonNotifyLikes.setBackgroundResource(R.drawable.button_change_tab_bg);
                buttonNotifyComments.setBackgroundResource(R.drawable.button_change_tab_selected_bg);
                buttonNotifyFollowings.setBackgroundResource(R.drawable.button_change_tab_bg);
                loadNotification(Selected);
            }
        });
        buttonNotifyFollowings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selected = "FOLLOWINGS";
                buttonNotifyLikes.setBackgroundResource(R.drawable.button_change_tab_bg);
                buttonNotifyComments.setBackgroundResource(R.drawable.button_change_tab_bg);
                buttonNotifyFollowings.setBackgroundResource(R.drawable.button_change_tab_selected_bg);
                loadNotification(Selected);
            }
        });
        notificationsItemArrayList = new ArrayList<>();
        loadNotification(Selected);
    }

    private void loadNotification(String selected) {
        container.setVisibility(View.VISIBLE);
        container.startShimmer();
        if (selected.equals("LIKES")) {
            db.collection("NOTIFICATIONS")
                    .orderBy("order", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (notificationsItemArrayList.size() > 0)
                                notificationsItemArrayList.clear();
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

                                if (notificationsItem.getReceiver().equals(uid) && notificationsItem.getNotificationType().equals("POST_LIKE")) {
                                    notificationsItemArrayList.add(notificationsItem);
                                }

                            }
                            notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(NotificationsActivity.this, notificationsItemArrayList);
                            recyclerView.setAdapter(notificationsRecyclerAdapter);
                            container.stopShimmer();
                            container.setVisibility(View.GONE);
                        }
                    });
        }

        if (selected.equals("COMMENTS")) {
            db.collection("NOTIFICATIONS")
                    .orderBy("order", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (notificationsItemArrayList.size() > 0)
                                notificationsItemArrayList.clear();
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

                                if (notificationsItem.getReceiver().equals(uid) && notificationsItem.getNotificationType().equals("POST_COMMENT")) {
                                    notificationsItemArrayList.add(notificationsItem);
                                }

                            }

                            notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(NotificationsActivity.this, notificationsItemArrayList);
                            recyclerView.setAdapter(notificationsRecyclerAdapter);

                            container.stopShimmer();
                            container.setVisibility(View.GONE);
                        }
                    });
        }

        if (selected.equals("FOLLOWINGS")) {
            db.collection("NOTIFICATIONS")
                    .orderBy("notificationID", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (notificationsItemArrayList.size() > 0)
                                notificationsItemArrayList.clear();
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

                                if (notificationsItem.getReceiver().equals(uid) && notificationsItem.getNotificationType().equals("FOLLOW")) {
                                    notificationsItemArrayList.add(notificationsItem);
                                }

                            }

                            notificationsRecyclerAdapter = new NotificationsRecyclerAdapter(NotificationsActivity.this, notificationsItemArrayList);
                            recyclerView.setAdapter(notificationsRecyclerAdapter);

                            container.stopShimmer();
                            container.setVisibility(View.GONE);
                        }
                    });
        }


    }
}