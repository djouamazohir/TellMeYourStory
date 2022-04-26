package com.zohirdev.mystory.activities.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.items.NotificationsItem;

import java.util.Objects;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class DashBoardActivity extends AppCompatActivity {

    CardView cardStoriesSaved, cardEditProfile, cardConfirmName,cardAllUsers, cardNote, cardNotification, cardSearch, cardLanguage, cardLogOut;
    Switch switchNightMode;
    TextView textCountNotification;

    boolean night;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    int notifications;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        sharedPref = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        night = sharedPref.getBoolean("night", false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activity = this;

        Objects.requireNonNull(getSupportActionBar()).hide();

        cardStoriesSaved = findViewById(R.id.cardStoriesSaved);
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardConfirmName = findViewById(R.id.cardConfirmName);
        cardAllUsers = findViewById(R.id.cardAllUsers);
        cardNote = findViewById(R.id.cardStoriesNotes);
        cardNotification = findViewById(R.id.cardNotification);
        cardSearch = findViewById(R.id.cardSearch);
        cardLanguage = findViewById(R.id.cardLanguage);
        cardLogOut = findViewById(R.id.cardLogOut);

        switchNightMode = findViewById(R.id.switchNightMode);

        textCountNotification = findViewById(R.id.textCountNotifications);
        textCountNotification.setVisibility(View.GONE);
        if (night) {
            switchNightMode.setChecked(true);
        }

        cardStoriesSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, StoriesSavedActivity.class);
                startActivity(intent);
            }
        });

        cardEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        cardConfirmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, NameConfirmationActivity.class);
                startActivity(intent);
            }
        });

        cardAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, AllUsersActivity.class);
                startActivity(intent);
            }
        });

        cardNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, StoriesNoteActivity.class);
                startActivity(intent);
            }
        });

        cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        cardLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, LanguageActivity.class);
                startActivity(intent);
            }
        });

        cardLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(DashBoardActivity.this, LogOutActivity.class);
                startActivity(intent);
            }
        });

        switchNightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (night) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPref.edit();
                    editor.putBoolean("night", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPref.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();


            }
        });

        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                ha.postDelayed(this, 3000);
                countNotifications();
            }
        }, 2000);

        countNotifications();
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
                        if (notifications != 0){
                            textCountNotification.setVisibility(View.VISIBLE);
                            textCountNotification.setText(notifications + "");
                        }


                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        countNotifications();
    }

    @Override
    public void finish() {
        super.finish();
        activity = null;
    }
}