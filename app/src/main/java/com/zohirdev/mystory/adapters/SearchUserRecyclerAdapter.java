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
import com.zohirdev.mystory.activities.MyProfileActivity;
import com.zohirdev.mystory.activities.UsersProfileActivity;
import com.zohirdev.mystory.items.FollowersItem;
import com.zohirdev.mystory.items.UsersItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<UsersItem> usersItemArrayList;
    ArrayList<String> followersItemArrayList;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    public SearchUserRecyclerAdapter(Context context, ArrayList<UsersItem> usersItemArrayList) {
        this.context = context;
        this.usersItemArrayList = usersItemArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        followersItemArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_search_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Picasso.get().load(usersItemArrayList.get(position).getImageProfile()).into(holder.imageProfile);
        holder.textFullName.setText("" + usersItemArrayList.get(position).getFullName());
        holder.textUsername.setText("" + usersItemArrayList.get(position).getUserName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usersItemArrayList.get(position).getUid().equals(firebaseAuth.getUid())) {
                    Intent intent;
                    intent = new Intent(context, MyProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent;
                    intent = new Intent(context, UsersProfileActivity.class);
                    intent.putExtra("uid", usersItemArrayList.get(position).getUid());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersItemArrayList.size();
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
