package com.zohirdev.mystory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.zohirdev.mystory.activities.UsersProfileActivity;
import com.zohirdev.mystory.items.FollowersItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class FollowingUserRecyclerAdapter extends RecyclerView.Adapter<FollowingUserRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<FollowersItem> followingItemArrayList;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;


    public FollowingUserRecyclerAdapter(Context context, ArrayList<FollowersItem> followingItemArrayList) {
        this.context = context;
        this.followingItemArrayList = followingItemArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, UsersProfileActivity.class);
                intent.putExtra("uid", followingItemArrayList.get(position).getFollow());
                context.startActivity(intent);
            }
        });

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
                            if (usersItem.getUid().equals(followingItemArrayList.get(position).getFollow())) {
                                Picasso.get().load(usersItem.getImageProfile()).into(holder.imageProfile);
                                holder.textFullName.setText(usersItem.getFullName());
                                holder.textUsername.setText(usersItem.getUserName());
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return followingItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageProfile;
        TextView textFullName;
        TextView textUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textFullName = itemView.findViewById(R.id.textFullName);
            textUsername = itemView.findViewById(R.id.textUsername);
        }
    }
}
