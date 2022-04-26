package com.zohirdev.mystory.activities.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    EditText textFullName;
    EditText textBio;
    EditText textUsername;
    EditText textLinks;

    Button buttonSaveFullName;
    Button buttonSaveBio;
    Button buttonSaveUsername;
    Button buttonSaveLinks;

    ProgressBar progressBarSaveFullName;
    ProgressBar progressBarSaveBio;
    ProgressBar progressBarSaveUsername;
    ProgressBar progressBarSaveLinks;

    ImageView imageCover;
    ImageView imageProfile;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String uid;

    Button buttonEditProfilePicture;
    Button buttonEditCoverPicture;

    ArrayList<String> usernameArrayList;
    String fullName, newFullName;
    String bio, newBio;
    String username, newUsername;
    String links, newLinks;

    Button buttonBack;
    CardView cardSaveFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).hide();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();


        cardSaveFullName = findViewById(R.id.cardSaveFullName);

        usernameArrayList = new ArrayList<>();
        textFullName = findViewById(R.id.textFullName);

        textBio = findViewById(R.id.textBio);
        textUsername = findViewById(R.id.textUsername);
        textLinks = findViewById(R.id.textLinks);

        buttonSaveFullName = findViewById(R.id.buttonSaveFullName);
        buttonSaveBio = findViewById(R.id.buttonSaveBio);
        buttonSaveUsername = findViewById(R.id.buttonSaveUsername);
        buttonSaveLinks = findViewById(R.id.buttonSaveLinks);

        progressBarSaveFullName = findViewById(R.id.progressBarSaveFullName);
        progressBarSaveBio = findViewById(R.id.progressBarSaveBio);
        progressBarSaveUsername = findViewById(R.id.progressBarSaveUsername);
        progressBarSaveLinks = findViewById(R.id.progressBarSaveLinks);


        imageCover = findViewById(R.id.imageCover);
        imageProfile = findViewById(R.id.imageProfile);

        buttonEditProfilePicture = findViewById(R.id.buttonEditImageProfile);
        buttonEditCoverPicture = findViewById(R.id.buttonEditImageCover);

        buttonEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(EditProfileActivity.this, EditProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        buttonEditCoverPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(EditProfileActivity.this, EditCoverPictureActivity.class);
                startActivity(intent);
            }
        });


        buttonSaveFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newFullName = textFullName.getText().toString();
                if (!fullName.equals(newFullName) && !newFullName.isEmpty()) {
                    buttonSaveFullName.setVisibility(View.GONE);
                    progressBarSaveFullName.setVisibility(View.VISIBLE);
                    db.collection("USERS")
                            .document(uid)
                            .update("fullName", newFullName)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    buttonSaveFullName.setVisibility(View.VISIBLE);
                                    progressBarSaveFullName.setVisibility(View.GONE);
                                }
                            });
                }

            }
        });


        buttonSaveBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBio = textBio.getText().toString();
                if (!bio.equals(newBio) && !newBio.isEmpty()) {
                    buttonSaveBio.setVisibility(View.GONE);
                    progressBarSaveBio.setVisibility(View.VISIBLE);
                    db.collection("USERS")
                            .document(uid)
                            .update("bio", newBio)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    buttonSaveBio.setVisibility(View.VISIBLE);
                                    progressBarSaveBio.setVisibility(View.GONE);
                                }
                            });
                }

            }
        });

        buttonSaveUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUsername = textUsername.getText().toString();
                if (!username.equals(newUsername) && !newUsername.isEmpty()) {

                    if (!usernameArrayList.contains(newUsername)) {
                        buttonSaveUsername.setVisibility(View.GONE);
                        progressBarSaveUsername.setVisibility(View.VISIBLE);
                        db.collection("USERS")
                                .document(uid)
                                .update("username", newUsername)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        buttonSaveUsername.setVisibility(View.VISIBLE);
                                        progressBarSaveUsername.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        Toast.makeText(EditProfileActivity.this, "إسم المستخدم الذي أدخلته محجوز بالفعل", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        buttonSaveLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newLinks = textLinks.getText().toString();
                if (!links.equals(newLinks) && !newLinks.isEmpty()) {
                    buttonSaveLinks.setVisibility(View.GONE);
                    progressBarSaveLinks.setVisibility(View.VISIBLE);
                    db.collection("USERS")
                            .document(uid)
                            .update("links", newLinks)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    buttonSaveLinks.setVisibility(View.VISIBLE);
                                    progressBarSaveLinks.setVisibility(View.GONE);
                                }
                            });
                }

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

        textFullName.setEnabled(false);
        cardSaveFullName.setVisibility(View.GONE);

    }

    public void loadUserInfo() {
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
                            usernameArrayList.add(usersItem.getUserName());

                            if (usersItem.getUid().equals(uid)) {
                                Picasso.get().load(usersItem.getImageCover()).into(imageCover);
                                Picasso.get().load(usersItem.getImageProfile()).into(imageProfile);
                                textFullName.setText(usersItem.getFullName());
                                textBio.setText(usersItem.getBio());
                                textUsername.setText(usersItem.getUserName());
                                textLinks.setText(usersItem.getLinks());

                                fullName = usersItem.getFullName();
                                bio = usersItem.getBio();
                                username = usersItem.getUserName();
                                links = usersItem.getLinks();

                                if (usersItem.getVerified().equals("YES")) {
                                    textFullName.setEnabled(false);
                                    cardSaveFullName.setVisibility(View.GONE);
                                }else {
                                    textFullName.setEnabled(true);
                                    cardSaveFullName.setVisibility(View.VISIBLE);
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
}