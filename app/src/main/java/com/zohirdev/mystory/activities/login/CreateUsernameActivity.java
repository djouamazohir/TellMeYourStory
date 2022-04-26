package com.zohirdev.mystory.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class CreateUsernameActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    EditText editTextUsername;

    Button buttonSkip;
    Button buttonNext;
    ProgressBar progressBarNext;
    String uid;
    ArrayList<String> usersStringArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_username);

        Objects.requireNonNull(getSupportActionBar()).hide();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        editTextUsername = findViewById(R.id.textUsername);
        editTextUsername.setFilters(new InputFilter[]{new LoginFilter.UsernameFilterGeneric()});


        buttonSkip = findViewById(R.id.buttonSkip);
        buttonNext = findViewById(R.id.buttonNext);
        progressBarNext = findViewById(R.id.progressNext);

        usersStringArrayList = new ArrayList<>();

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(CreateUsernameActivity.this, AddProfilePictureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loadAllUsers();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                if (!usersStringArrayList.contains(username)) {
                    createUsername(username);
                } else {
                    Toast.makeText(CreateUsernameActivity.this, "إسم المستخدم الذي أدخلته محجوز", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUsername(String username) {
        buttonNext.setVisibility(View.GONE);
        progressBarNext.setVisibility(View.VISIBLE);
        db.collection("USERS")
                .document("" + firebaseAuth.getUid())
                .update("username", username)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent;
                        intent = new Intent(CreateUsernameActivity.this, AddProfilePictureActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void loadAllUsers() {
        if (usersStringArrayList.size() > 0) usersStringArrayList.clear();
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
                            usersStringArrayList.add(usersItem.getUserName());
                        }
                        buttonNext.setClickable(true);
                    }
                });
    }
}