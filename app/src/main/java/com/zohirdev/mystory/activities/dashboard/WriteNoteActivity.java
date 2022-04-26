package com.zohirdev.mystory.activities.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zohirdev.mystory.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class WriteNoteActivity extends AppCompatActivity {

    EditText textWriteNote;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    String uid;
    String story;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

        Objects.requireNonNull(getSupportActionBar()).hide();

        textWriteNote = findViewById(R.id.textWriteNote);
        textWriteNote.setEnabled(false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        uid = firebaseAuth.getUid();
        time = new Date().getTime();

        addNoteToDatabase();


        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textWriteNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                db.collection("NOTES")
                        .document(time + uid)
                        .update("story", textWriteNote.getText().toString());
            }
        });

    }

    private void addNoteToDatabase() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("noteID", time + uid);
        dataMap.put("story", "");
        dataMap.put("user", uid);

        db.collection("NOTES")
                .document(time + uid)
                .set(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        textWriteNote.setEnabled(true);
                    }
                });
    }
}