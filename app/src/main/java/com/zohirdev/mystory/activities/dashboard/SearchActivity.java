package com.zohirdev.mystory.activities.dashboard;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.adapters.HomeStoriesRecyclerAdapter;
import com.zohirdev.mystory.adapters.SearchUserRecyclerAdapter;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    Button buttonBack;
    Button buttonSearch;
    AutoCompleteTextView textSearch;

    String word;

    RecyclerView recyclerViewUsers;
    ArrayList<UsersItem> usersItemArrayList;
    SearchUserRecyclerAdapter searchUserRecyclerAdapter;

    RecyclerView recyclerViewStories;
    ArrayList<StoriesItem> storiesItemArrayList;
    HomeStoriesRecyclerAdapter storiesRecyclerAdapter;

    ProgressBar progressBarSearch;

    ArrayList<String> allUsersSuggestionArrayList;
    ArrayAdapter<String> adapterSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Objects.requireNonNull(getSupportActionBar()).hide();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        buttonBack = findViewById(R.id.buttonBack);
        buttonSearch = findViewById(R.id.buttonSearch);
        textSearch = findViewById(R.id.textSearch);



        allUsersSuggestionArrayList = new ArrayList<>();

        progressBarSearch = findViewById(R.id.progressBarSearch);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setHasFixedSize(false);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        usersItemArrayList = new ArrayList<>();

        recyclerViewStories = findViewById(R.id.recyclerViewStories);
        recyclerViewStories.setHasFixedSize(false);
        recyclerViewStories.setLayoutManager(new LinearLayoutManager(this));

        storiesItemArrayList = new ArrayList<>();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                word = textSearch.getText().toString();
                if (word.length() > 3) {
                    progressBarSearch.setVisibility(View.VISIBLE);
                    buttonSearch.setVisibility(View.GONE);
                    searchUser(word);
                    searchStories(word);
                }

            }
        });

        loadAllSuggestion();

    }

    private void loadAllSuggestion() {
        db.collection("USERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            UsersItem suggestion = new UsersItem(
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
                            if (!allUsersSuggestionArrayList.contains(suggestion.getFullName())) {
                                allUsersSuggestionArrayList.add(suggestion.getFullName());
                            }
                            adapterSuggestion = new ArrayAdapter<>(SearchActivity.this, R.layout.drop_down, allUsersSuggestionArrayList);
                            textSearch.setAdapter(adapterSuggestion);
                        }
                    }
                });
    }

    private void searchStories(String word) {
        if (storiesItemArrayList.size() > 0) storiesItemArrayList.clear();
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
                            if (storiesItem.getStory().contains(word)) {
                                storiesItemArrayList.add(storiesItem);
                            }
                        }
                        storiesRecyclerAdapter = new HomeStoriesRecyclerAdapter(SearchActivity.this, storiesItemArrayList);
                        recyclerViewStories.setAdapter(storiesRecyclerAdapter);
                        buttonSearch.setVisibility(View.VISIBLE);
                        progressBarSearch.setVisibility(View.GONE);
                    }
                });
    }

    private void searchUser(String word) {
        if (usersItemArrayList.size() > 0) usersItemArrayList.clear();
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
                            if (usersItem.getFullName().contains(word) || usersItem.getUserName().contains(word)) {
                                usersItemArrayList.add(usersItem);
                            }
                        }
                        searchUserRecyclerAdapter = new SearchUserRecyclerAdapter(SearchActivity.this, usersItemArrayList);
                        recyclerViewUsers.setAdapter(searchUserRecyclerAdapter);

                        buttonSearch.setVisibility(View.VISIBLE);
                        progressBarSearch.setVisibility(View.GONE);
                    }
                });
    }
}