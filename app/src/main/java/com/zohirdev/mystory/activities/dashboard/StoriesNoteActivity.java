package com.zohirdev.mystory.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.NotesRecyclerAdapter;
import com.zohirdev.mystory.items.NotesItem;

import java.util.ArrayList;
import java.util.Objects;

public class StoriesNoteActivity extends AppCompatActivity {

    Button buttonAddNewNote;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String uid;


    RecyclerView recyclerView;
    ArrayList<NotesItem> notesItemArrayList;
    NotesRecyclerAdapter notesRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_note);

        Objects.requireNonNull(getSupportActionBar()).hide();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();

        buttonAddNewNote = findViewById(R.id.buttonAddNewNote);
        buttonAddNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(StoriesNoteActivity.this, WriteNoteActivity.class);
                startActivity(intent);
            }
        });
        notesItemArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        loadNotes();
    }

    private void loadNotes() {
        db.collection("NOTES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (notesItemArrayList.size() > 0) notesItemArrayList.clear();
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            NotesItem notesItem = new NotesItem(
                                    snapshot.getString("noteID"),
                                    snapshot.getString("user"),
                                    snapshot.getString("story")
                            );
                            if (notesItem.getUser().equals(uid)) {
                                notesItemArrayList.add(notesItem);
                            }
                        }
                        notesRecyclerAdapter = new NotesRecyclerAdapter(StoriesNoteActivity.this, notesItemArrayList);
                        recyclerView.setAdapter(notesRecyclerAdapter);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }
}