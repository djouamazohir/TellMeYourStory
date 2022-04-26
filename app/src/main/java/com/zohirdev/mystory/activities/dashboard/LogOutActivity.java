package com.zohirdev.mystory.activities.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.HomeActivity;
import com.zohirdev.mystory.activities.MyProfileActivity;
import com.zohirdev.mystory.activities.SplashActivity;
import com.zohirdev.mystory.activities.login.CreateNewAccountActivity;
import com.zohirdev.mystory.items.CommentsItem;
import com.zohirdev.mystory.items.FollowersItem;
import com.zohirdev.mystory.items.ReactStoryItem;
import com.zohirdev.mystory.items.NotesItem;
import com.zohirdev.mystory.items.NotificationsItem;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.StorySavedItem;

import java.util.ArrayList;
import java.util.Objects;

public class LogOutActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    Button buttonLogOut, buttonDeleteAccount;

    RelativeLayout relativeLayout;
    CardView cardDeleteNotifications,
            cardDeleteLikes,
            cardDeleteComments,
            cardDeleteNotes,
            cardDeleteStoriesSaved,
            cardDeleteFollowers,
            cardDeleteStories;

    TextView textDeleteNotifications,
            textDeleteLikes,
            textDeleteComments,
            textDeleteNotes,
            textDeleteStoriesSaved,
            textDeleteFollowers,
            textDeleteStories;

    ProgressBar progressBarDeleteNotifications,
            progressBarDeleteLikes,
            progressBarDeleteComments,
            progressBarDeleteNotes,
            progressBarDeleteStoriesSaved,
            progressBarDeleteFollowers,
            progressBarDeleteStories;

    ArrayList<String> notificationsArrayList;
    ArrayList<String> likesArrayList;
    ArrayList<String> commentsArrayList;
    ArrayList<String> notesArrayList;
    ArrayList<String> storySavedArrayList;
    ArrayList<String> followersArrayList;
    ArrayList<String> storiesArrayList;

    float progressNotification = 0;
    float progressLikes = 0;
    float progressComments = 0;
    float progressNotes = 0;
    float progressStorySaved = 0;
    float progressFollowers = 0;
    float progressStories = 0;

    boolean notificationsDeleted = false;
    boolean likesDeleted = false;
    boolean commentsDeleted = false;
    boolean notesDeleted = false;
    boolean storySavedDeleted = false;
    boolean followersDeleted = false;
    boolean storiesDeleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        notificationsArrayList = new ArrayList<>();
        likesArrayList = new ArrayList<>();
        commentsArrayList = new ArrayList<>();
        notesArrayList = new ArrayList<>();
        storySavedArrayList = new ArrayList<>();
        followersArrayList = new ArrayList<>();
        storiesArrayList = new ArrayList<>();

        buttonLogOut = findViewById(R.id.buttonLogOut);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        relativeLayout = findViewById(R.id.relativeLayout);

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ViewDialogConfirmLogOut viewDialogConfirmLogOut = new ViewDialogConfirmLogOut();
                viewDialogConfirmLogOut.showDialog(LogOutActivity.this);
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonDeleteAccount.setVisibility(View.GONE);
                buttonLogOut.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                deleteNotifications();
                deleteLikes();
                deleteComments();
                deleteNotes();
                deleteStorySaved();
                deleteFollowers();
                deleteStories();
            }
        });


        cardDeleteNotifications = findViewById(R.id.cardDeleteNotifications);
        cardDeleteLikes = findViewById(R.id.cardDeleteLikes);
        cardDeleteComments = findViewById(R.id.cardDeleteComments);
        cardDeleteNotes = findViewById(R.id.cardDeleteNotes);
        cardDeleteStoriesSaved = findViewById(R.id.cardDeleteStoriesSaved);
        cardDeleteFollowers = findViewById(R.id.cardDeleteFollowers);
        cardDeleteStories = findViewById(R.id.cardDeleteStories);


        textDeleteNotifications = findViewById(R.id.textDeleteNotifications);
        textDeleteLikes = findViewById(R.id.textDeleteLikes);
        textDeleteComments = findViewById(R.id.textDeleteComments);
        textDeleteNotes = findViewById(R.id.textDeleteNotes);
        textDeleteStoriesSaved = findViewById(R.id.textDeleteStoriesSaved);
        textDeleteFollowers = findViewById(R.id.textDeleteFollowers);
        textDeleteStories = findViewById(R.id.textDeleteStories);

        progressBarDeleteNotifications = findViewById(R.id.progressBarDeleteNotifications);
        progressBarDeleteLikes = findViewById(R.id.progressBarDeleteLikes);
        progressBarDeleteComments = findViewById(R.id.progressBarDeleteComments);
        progressBarDeleteNotes = findViewById(R.id.progressBarDeleteNotes);
        progressBarDeleteStoriesSaved = findViewById(R.id.progressBarDeleteStoriesSaved);
        progressBarDeleteFollowers = findViewById(R.id.progressBarDeleteFollowers);
        progressBarDeleteStories = findViewById(R.id.progressBarDeleteStories);


    }

    private void deleteNotifications() {
        db.collection("NOTIFICATIONS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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

                            if (notificationsItem.getReceiver().equals(firebaseAuth.getUid()) || notificationsItem.getSender().equals(firebaseAuth.getUid())) {
                                notificationsArrayList.add(notificationsItem.getNotificationID());
                            }
                        }

                        float size = notificationsArrayList.size();
                        if (size == 0) {
                            cardDeleteNotifications.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteNotifications.setText("تم حذف الإشعارات بنجاح");
                            progressBarDeleteNotifications.setVisibility(View.GONE);
                            notificationsDeleted = true;
                            if (likesDeleted && commentsDeleted && notesDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {
                                db.collection("NOTIFICATIONS")
                                        .document(notificationsArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressNotification = progressNotification + step;
                                                textDeleteNotifications.setText("جار حذف الإشعارات   " + progressNotification + " %");
                                                if (progressNotification > 99) {
                                                    cardDeleteNotifications.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteNotifications.setText("تم حذف الإشعارات بنجاح");
                                                    progressBarDeleteNotifications.setVisibility(View.GONE);
                                                    notificationsDeleted = true;
                                                    if (likesDeleted && commentsDeleted && notesDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });

                            }
                        }
                    }
                });
    }

    private void deleteLikes() {
        db.collection("STORY_REACT")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            ReactStoryItem reactStoryItem = new ReactStoryItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("userID"),
                                    snapshot.getString("reactType")
                            );
                            if (reactStoryItem.getUserID().equals(firebaseAuth.getUid())) {
                                likesArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                            }
                        }

                        float size = likesArrayList.size();
                        if (size == 0) {
                            cardDeleteLikes.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteLikes.setText("تم حذف الإعجابات بنجاح");
                            progressBarDeleteLikes.setVisibility(View.GONE);
                            likesDeleted = true;
                            if (notificationsDeleted && commentsDeleted && notesDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {

                                db.collection("STORY_LIKES")
                                        .document(likesArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressLikes = progressLikes + step;
                                                textDeleteNotifications.setText("جار حذف الإعجابات   " + progressLikes + " %");
                                                if (progressLikes > 99) {
                                                    cardDeleteLikes.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteLikes.setText("تم حذف الإعجابات بنجاح");
                                                    progressBarDeleteLikes.setVisibility(View.GONE);
                                                    likesDeleted = true;
                                                    if (notificationsDeleted && commentsDeleted && notesDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void deleteComments() {
        db.collection("STORY_COMMENTS")
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
                            if (commentsItem.getUserID().equals(firebaseAuth.getUid())) {
                                commentsArrayList.add(commentsItem.getCommentID());
                            }
                        }

                        float size = commentsArrayList.size();
                        if (size == 0) {
                            cardDeleteComments.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteComments.setText("تم حذف التعليقات بنجاح");
                            progressBarDeleteComments.setVisibility(View.GONE);
                            commentsDeleted = true;
                            if (notificationsDeleted && likesDeleted && notesDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {

                                db.collection("STORY_COMMENTS")
                                        .document(commentsArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressComments = progressComments + step;
                                                textDeleteComments.setText("جار حذف التعليقات   " + progressComments + " %");
                                                if (progressComments > 99) {
                                                    cardDeleteComments.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteComments.setText("تم حذف التعليقات بنجاح");
                                                    progressBarDeleteComments.setVisibility(View.GONE);
                                                    commentsDeleted = true;
                                                    if (notificationsDeleted && likesDeleted && notesDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void deleteNotes() {
        db.collection("NOTES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            NotesItem notesItem = new NotesItem(
                                    snapshot.getString("noteID"),
                                    snapshot.getString("user"),
                                    snapshot.getString("story")
                            );
                            if (notesItem.getUser().equals(firebaseAuth.getUid())) {
                                notesArrayList.add(notesItem.getNoteID());
                            }
                        }


                        float size = notesArrayList.size();
                        if (size == 0) {
                            cardDeleteNotes.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteNotes.setText("تم حذف حافظة القصص بنجاح");
                            progressBarDeleteNotes.setVisibility(View.GONE);
                            notesDeleted = true;
                            if (notificationsDeleted && likesDeleted && commentsDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {

                                db.collection("NOTES")
                                        .document(notesArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressNotes = progressNotes + step;
                                                textDeleteNotes.setText("جار حذف حافظة القصص   " + progressNotes + " %");
                                                if (progressNotes > 99) {
                                                    cardDeleteNotes.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteNotes.setText("تم حذف حافظة القصص بنجاح");
                                                    progressBarDeleteNotes.setVisibility(View.GONE);
                                                    notesDeleted = true;
                                                    if (notificationsDeleted && likesDeleted && commentsDeleted && storySavedDeleted && followersDeleted && storiesDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void deleteStorySaved() {
        db.collection("STORY_SAVED")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StorySavedItem savedItem =
                                    new StorySavedItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("userID"),
                                            snapshot.getString("publisher")
                                    );
                            if (savedItem.getUserID().equals(firebaseAuth.getUid())) {
                                storySavedArrayList.add(savedItem.getUserID() + savedItem.getStoryID());
                            }
                        }


                        float size = storySavedArrayList.size();
                        if (size == 0) {
                            cardDeleteStoriesSaved.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteStoriesSaved.setText("تم حذف القصص المحفوظة بنجاح");
                            progressBarDeleteStoriesSaved.setVisibility(View.GONE);
                            storySavedDeleted = true;
                            if (notificationsDeleted && likesDeleted && commentsDeleted && notesDeleted && followersDeleted && storiesDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {

                                db.collection("STORY_SAVED")
                                        .document(storySavedArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressStorySaved = progressStorySaved + step;
                                                textDeleteStoriesSaved.setText("جار حذف حافظة القصص   " + progressStorySaved + " %");
                                                if (progressStorySaved > 99) {
                                                    cardDeleteStoriesSaved.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteStoriesSaved.setText("تم حذف القصص المحفوظة بنجاح");
                                                    progressBarDeleteStoriesSaved.setVisibility(View.GONE);
                                                    storySavedDeleted = true;
                                                    if (notificationsDeleted && likesDeleted && commentsDeleted && notesDeleted && followersDeleted && storiesDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void deleteFollowers() {
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
                            if (followersItem.getUser().equals(firebaseAuth.getUid()) || followersItem.getFollow().equals(firebaseAuth.getUid())) {
                                followersArrayList.add(followersItem.getUser() + followersItem.getFollow());
                            }
                        }

                        float size = followersArrayList.size();
                        if (size == 0) {
                            cardDeleteFollowers.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteFollowers.setText("تم حذف المتابعين بنجاح");
                            progressBarDeleteFollowers.setVisibility(View.GONE);
                            followersDeleted = true;
                            if (notificationsDeleted && likesDeleted && commentsDeleted && notesDeleted && storySavedDeleted && storiesDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {

                                db.collection("FOLLOWERS")
                                        .document(followersArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressFollowers = progressFollowers + step;
                                                textDeleteFollowers.setText("جار حذف حافظة القصص   " + progressFollowers + " %");
                                                if (progressFollowers > 99) {
                                                    cardDeleteFollowers.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteFollowers.setText("تم حذف المتابعين بنجاح");
                                                    progressBarDeleteFollowers.setVisibility(View.GONE);
                                                    followersDeleted = true;
                                                    if (notificationsDeleted && likesDeleted && commentsDeleted && notesDeleted && storySavedDeleted && storiesDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void deleteStories() {
        db.collection("STORIES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StoriesItem storiesItem =
                                    new StoriesItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("story"),
                                            snapshot.getString("time"),
                                            snapshot.getString("date"),
                                            snapshot.getString("publisher")
                                    );
                            if (storiesItem.getPublisher().equals(firebaseAuth.getUid())) {
                                storiesArrayList.add(storiesItem.getStoryID());
                            }

                        }

                        float size = storiesArrayList.size();
                        if (size == 0) {
                            cardDeleteStories.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                            textDeleteStories.setText("تم حذف القصص بنجاح");
                            progressBarDeleteStories.setVisibility(View.GONE);
                            storiesDeleted = true;
                            if (notificationsDeleted && likesDeleted && commentsDeleted && notesDeleted && storySavedDeleted && followersDeleted) {
                                deleteUserAndLogOut();
                            }
                        } else {
                            float step = 100 / size;
                            for (int i = 0; i < size; i++) {

                                db.collection("STORIES")
                                        .document(storiesArrayList.get(i))
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressStories = progressStories + step;
                                                textDeleteStories.setText("جار حذف القصص   " + progressStories + " %");
                                                if (progressStories > 99) {
                                                    cardDeleteStories.setCardBackgroundColor(getResources().getColor(R.color.not_follow));
                                                    textDeleteStories.setText("تم حذف القصص بنجاح");
                                                    progressBarDeleteStories.setVisibility(View.GONE);
                                                    storiesDeleted = true;
                                                    if (notificationsDeleted && likesDeleted && commentsDeleted && notesDeleted && storySavedDeleted && followersDeleted) {
                                                        deleteUserAndLogOut();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }

                    }
                });

    }

    private void deleteUserAndLogOut() {
        db.collection("USERS")
                .document(Objects.requireNonNull(firebaseAuth.getUid()))
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseAuth.signOut();

                        try {
                            DashBoardActivity.activity.finish();
                            MyProfileActivity.activity.finish();
                            HomeActivity.activity.finish();
                        } catch (Exception ignored) {
                        }
                        Intent intent;
                        intent = new Intent(LogOutActivity.this, SplashActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
    }

    public class ViewDialogConfirmLogOut {
        @SuppressLint({"SetTextI18n", "ResourceType"})
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void showDialog(Context context) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_confirm_logout);

            Button buttonYes = dialog.findViewById(R.id.buttonYes);
            Button buttonNo = dialog.findViewById(R.id.buttonNo);

            buttonNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signOut();
                    Intent intent;
                    intent = new Intent(LogOutActivity.this, CreateNewAccountActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }

}