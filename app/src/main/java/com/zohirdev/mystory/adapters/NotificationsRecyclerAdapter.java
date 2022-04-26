package com.zohirdev.mystory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.CommentsActivity;
import com.zohirdev.mystory.activities.ShowLikesListActivity;
import com.zohirdev.mystory.activities.UsersProfileActivity;
import com.zohirdev.mystory.activities.sff.FollowersActivity;
import com.zohirdev.mystory.items.NotificationsItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationsItem> notificationsItemArrayList;
    FirebaseFirestore db;
    String name;
    String sex;

    public NotificationsRecyclerAdapter(Context context, ArrayList<NotificationsItem> notificationsItemArrayList) {
        this.context = context;
        this.notificationsItemArrayList = notificationsItemArrayList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

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
                                    snapshot.getString("sex"),
                                    snapshot.getString("isBlocked"),
                                    snapshot.getString("deviceID"),
                                    snapshot.getString("verified")
                            );
                            if (notificationsItemArrayList.get(position).getSender().equals(usersItem.getUid())) {
                                Picasso.get().load(usersItem.getImageProfile()).into(holder.imageUser);
                                name = usersItem.getFullName();
                                sex = usersItem.getSex();
                            }
                        }
                        if (notificationsItemArrayList.get(position).getNotificationType().equals("POST_COMMENT")) {

                            if (sex.equals("MAN")) {
                                holder.textNotification.setText("علق " + name + " على قصتك");
                            } else {
                                holder.textNotification.setText("علقت " + name + " على قصتك");
                            }

                            holder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    makeNotificationSeen(notificationsItemArrayList.get(position).getNotificationID());
                                    Intent intent;
                                    intent = new Intent(context, CommentsActivity.class);
                                    intent.putExtra("storyID", notificationsItemArrayList.get(position).getStoryID());
                                    intent.putExtra("publisher", notificationsItemArrayList.get(position).getReceiver());
                                    context.startActivity(intent);
                                }
                            });
                        }

                        if (notificationsItemArrayList.get(position).getNotificationType().equals("POST_LIKE")) {

                            if (sex.equals("MAN")) {
                                holder.textNotification.setText("تفاعل " + name + " مع قصتك");
                            } else {
                                holder.textNotification.setText("تفاعلت " + name + " مع قصتك");
                            }

                            holder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    makeNotificationSeen(notificationsItemArrayList.get(position).getNotificationID());
                                    Intent intent;
                                    intent = new Intent(context, ShowLikesListActivity.class);
                                    intent.putExtra("storyID", notificationsItemArrayList.get(position).getStoryID());
                                    context.startActivity(intent);
                                }
                            });
                        }

                        if (notificationsItemArrayList.get(position).getNotificationType().equals("FOLLOW")) {

                            if (sex.equals("MAN")) {
                                holder.textNotification.setText("قام " + name + " بمتابعتك");
                            } else {
                                holder.textNotification.setText("قامت " + name + " بمتابعتك");
                            }

                            holder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    makeNotificationSeen(notificationsItemArrayList.get(position).getNotificationID());
                                    Intent intent;
                                    intent = new Intent(context, UsersProfileActivity.class);
                                    intent.putExtra("uid", notificationsItemArrayList.get(position).getSender());
                                    context.startActivity(intent);
                                }
                            });
                        }

                    }
                });

        if (notificationsItemArrayList.get(position).getSeen().equals("NO")) {
            holder.imageSee.setVisibility(View.VISIBLE);
        } else {
            holder.imageSee.setVisibility(View.GONE);
        }

        holder.textDateAndTime.setText("\uD83D\uDCC5  " + notificationsItemArrayList.get(position).getDate() + "  " +
                "\uD83D\uDD54  " + notificationsItemArrayList.get(position).getTime());

    }

    private void makeNotificationSeen(String notificationID) {
        db.collection("NOTIFICATIONS")
                .document(notificationID)
                .update("seen", "YES");
    }

    @Override
    public int getItemCount() {
        return notificationsItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageUser;
        TextView textNotification;
        TextView textDateAndTime;
        ImageView imageSee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageUser = itemView.findViewById(R.id.imageUser);
            textNotification = itemView.findViewById(R.id.textNotification);
            textDateAndTime = itemView.findViewById(R.id.textDateAndTime);
            imageSee = itemView.findViewById(R.id.imageSee);
        }
    }
}
