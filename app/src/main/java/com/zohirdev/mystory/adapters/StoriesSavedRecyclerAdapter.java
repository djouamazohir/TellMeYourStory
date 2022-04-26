package com.zohirdev.mystory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.CommentsActivity;
import com.zohirdev.mystory.items.ReactStoryItem;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.StorySavedItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StoriesSavedRecyclerAdapter extends RecyclerView.Adapter<StoriesSavedRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<StorySavedItem> storiesItemArrayList;
    ArrayList<String> storiesSavedItemArrayList;
    ArrayList<String> storiesLikesItemArrayList;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    public StoriesSavedRecyclerAdapter(Context context, ArrayList<StorySavedItem> storiesItemArrayList) {
        this.context = context;
        this.storiesItemArrayList = storiesItemArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storiesSavedItemArrayList = new ArrayList<>();
        storiesLikesItemArrayList = new ArrayList<>();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.story_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        db.collection("USERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
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
                            if (storiesItemArrayList.get(position).getPublisher().equals(usersItem.getUid())) {
                                Picasso.get().load(usersItem.getImageProfile()).into(holder.imageProfile);
                                holder.textFullName.setText(usersItem.getFullName());
                                holder.textUsername.setText(usersItem.getUserName());
                            }
                        }
                    }
                });

        db.collection("STORY_SAVED")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            StorySavedItem savedItem = new StorySavedItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("userID"),
                                    snapshot.getString("publisher")
                            );
                            storiesSavedItemArrayList.add(savedItem.getUserID() + savedItem.getStoryID());
                        }
                        if (storiesSavedItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                            holder.imageSaveStory.setBackgroundResource(R.drawable.saved);
                            holder.cardSaveStory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    holder.imageSaveStory.setVisibility(View.GONE);
                                    holder.progressBarSaveStory.setVisibility(View.VISIBLE);

                                    db.collection("STORY_SAVED")
                                            .document("" + firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    holder.progressBarSaveStory.setVisibility(View.GONE);
                                                    holder.imageSaveStory.setVisibility(View.VISIBLE);
                                                    holder.imageSaveStory.setBackgroundResource(R.drawable.not_saved);
                                                }
                                            });
                                }
                            });
                        } else {
                            holder.imageSaveStory.setBackgroundResource(R.drawable.not_saved);
                            holder.cardSaveStory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    holder.imageSaveStory.setVisibility(View.GONE);
                                    holder.progressBarSaveStory.setVisibility(View.VISIBLE);

                                    HashMap<String, String> dataMap = new HashMap<>();
                                    dataMap.put("storyID", storiesItemArrayList.get(position).getStoryID());
                                    dataMap.put("userID", firebaseAuth.getUid());
                                    dataMap.put("publisher", storiesItemArrayList.get(position).getPublisher());

                                    db.collection("STORY_SAVED")
                                            .document("" + firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                                            .set(dataMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    holder.progressBarSaveStory.setVisibility(View.GONE);
                                                    holder.imageSaveStory.setVisibility(View.VISIBLE);
                                                    holder.imageSaveStory.setBackgroundResource(R.drawable.saved);
                                                }
                                            });
                                }
                            });
                        }
                    }
                });

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
                            storiesLikesItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                        }
                        if (storiesLikesItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {

                            //holder.textLike.setTextColor(context.getResources().getColor(R.color.like));
                            holder.imageLike.setBackgroundResource(R.drawable.love);

                            holder.cardLike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    db.collection("STORY_LIKES")
                                            .document("" + firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //holder.textLike.setTextColor(context.getResources().getColor(R.color.inlike));
                                                    holder.imageLike.setBackgroundResource(R.drawable.inlike);
                                                }
                                            });
                                }
                            });
                        } else {
                            //holder.textLike.setTextColor(context.getResources().getColor(R.color.inlike));
                            holder.imageLike.setBackgroundResource(R.drawable.inlike);
                            holder.cardLike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    HashMap<String, String> dataMap = new HashMap<>();
                                    dataMap.put("storyID", storiesItemArrayList.get(position).getStoryID());
                                    dataMap.put("userID", firebaseAuth.getUid());

                                    db.collection("STORY_LIKES")
                                            .document("" + firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                                            .set(dataMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //holder.textLike.setTextColor(context.getResources().getColor(R.color.like));
                                                    holder.imageLike.setBackgroundResource(R.drawable.love);
                                                }
                                            });
                                }
                            });
                        }
                    }
                });

        holder.cardComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("storyID", storiesItemArrayList.get(position).getStoryID());
                intent.putExtra("publisher", storiesItemArrayList.get(position).getPublisher());
                context.startActivity(intent);
            }
        });

        db.collection("STORIES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StoriesItem storiesItem = new StoriesItem(
                                    snapshot.getString("storyID"),
                                    snapshot.getString("story"),
                                    snapshot.getString("time"),
                                    snapshot.getString("date"),
                                    snapshot.getString("publisher")
                            );

                            if (storiesItem.getStoryID().equals(storiesItemArrayList.get(position).getStoryID())) {
                                holder.textStory.setText(storiesItem.getStory());
                            }
                        }

                    }
                });

    }

    @Override
    public int getItemCount() {
        return storiesItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProfile;
        TextView textFullName;
        TextView textUsername;
        TextView textStory;

        CardView cardSaveStory;
        ImageView imageSaveStory;
        ProgressBar progressBarSaveStory;

        TextView textLike;
        ImageView imageLike;
        CardView cardLike;

        CardView cardComments;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            textFullName = itemView.findViewById(R.id.textFullName);
            textUsername = itemView.findViewById(R.id.textUsername);
            textStory = itemView.findViewById(R.id.textStory);

            cardSaveStory = itemView.findViewById(R.id.cardSaveStory);
            imageSaveStory = itemView.findViewById(R.id.imageSaveStory);
            progressBarSaveStory = itemView.findViewById(R.id.progressBarSaveStory);

            textLike = itemView.findViewById(R.id.textLike);
            imageLike = itemView.findViewById(R.id.imageLike);
            cardLike = itemView.findViewById(R.id.cardLike);

            cardComments = itemView.findViewById(R.id.cardComment);
        }
    }
}