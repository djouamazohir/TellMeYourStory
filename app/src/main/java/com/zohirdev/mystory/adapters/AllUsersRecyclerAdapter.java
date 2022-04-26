package com.zohirdev.mystory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.zohirdev.mystory.activities.UsersProfileActivity;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.UsersItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AllUsersRecyclerAdapter extends RecyclerView.Adapter<AllUsersRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<UsersItem> usersItemArrayList;
    int[] stories;
    boolean[] follow;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    public AllUsersRecyclerAdapter(Context context, ArrayList<UsersItem> usersItemArrayList) {
        this.context = context;
        this.usersItemArrayList = usersItemArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.users_suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso.get().load(usersItemArrayList.get(position).getImageCover()).into(holder.imageCover);
        Picasso.get().load(usersItemArrayList.get(position).getImageProfile()).into(holder.imageProfile);

        holder.textName.setText(usersItemArrayList.get(position).getFullName());

        stories = new int[usersItemArrayList.size()];
        follow = new boolean[usersItemArrayList.size()];

        db.collection("STORIES")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (stories[position] > 0) stories[position] = 0;
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StoriesItem storiesItem =
                                    new StoriesItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("story"),
                                            snapshot.getString("time"),
                                            snapshot.getString("date"),
                                            snapshot.getString("publisher")
                                    );
                            if (storiesItem.getPublisher().equals(usersItemArrayList.get(position).getUid())) {
                                stories[position] = stories[position] + 1;
                            }
                        }
                        holder.textStoriesCount.setText("" + stories[position] + " قصة");
                    }
                });

        holder.buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (follow[position]) {
                    holder.progressBarFollow.setVisibility(View.VISIBLE);
                    holder.buttonFollow.setVisibility(View.GONE);
                    db.collection("FOLLOWERS")
                            .document(firebaseAuth.getUid() + usersItemArrayList.get(position).getUid())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.buttonFollow.setVisibility(View.VISIBLE);
                                    holder.progressBarFollow.setVisibility(View.GONE);
                                    holder.buttonFollow.setText("متابعة");
                                    holder.cardViewFollow.setCardBackgroundColor(context.getResources().getColor(R.color.not_follow));
                                    follow[position] = false;
                                }
                            });
                } else {
                    holder.progressBarFollow.setVisibility(View.VISIBLE);
                    holder.buttonFollow.setVisibility(View.GONE);

                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("user", firebaseAuth.getUid());
                    dataMap.put("follow", usersItemArrayList.get(position).getUid());

                    db.collection("FOLLOWERS")
                            .document("" + firebaseAuth.getUid() + usersItemArrayList.get(position).getUid())
                            .set(dataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @SuppressLint("ResourceAsColor")
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.buttonFollow.setVisibility(View.VISIBLE);
                                    holder.progressBarFollow.setVisibility(View.GONE);
                                    holder.buttonFollow.setText("إلغاء المتابعة");
                                    holder.cardViewFollow.setCardBackgroundColor(context.getResources().getColor(R.color.follow));
                                    follow[position] = true;

                                    long id = new Date().getTime();
                                    LocalDate today = LocalDate.now();
                                    LocalTime now = LocalTime.now();

                                    String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                                    String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));


                                    HashMap<String, String> dataMap = new HashMap<>();
                                    dataMap.put("notificationID", id + firebaseAuth.getUid());
                                    dataMap.put("notificationType", "FOLLOW");
                                    dataMap.put("storyID", "");
                                    dataMap.put("sender", firebaseAuth.getUid());
                                    dataMap.put("receiver", usersItemArrayList.get(position).getUid());
                                    dataMap.put("seen", "NO");
                                    dataMap.put("time", time);
                                    dataMap.put("date", date);

                                    db.collection("NOTIFICATIONS")
                                            .document(id + firebaseAuth.getUid())
                                            .set(dataMap);
                                }
                            });
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, UsersProfileActivity.class);
                intent.putExtra("uid", usersItemArrayList.get(position).getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCover;
        ImageView imageProfile;

        TextView textName;
        TextView textStoriesCount;

        CardView cardViewFollow;
        Button buttonFollow;
        ProgressBar progressBarFollow;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageCover = itemView.findViewById(R.id.imageCover);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textStoriesCount = itemView.findViewById(R.id.textStoriesCount);
            cardViewFollow = itemView.findViewById(R.id.cardFollow);
            buttonFollow = itemView.findViewById(R.id.buttonFollow);
            progressBarFollow = itemView.findViewById(R.id.progressBarFollow);

            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
