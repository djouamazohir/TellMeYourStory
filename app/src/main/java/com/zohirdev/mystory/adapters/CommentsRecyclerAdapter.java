package com.zohirdev.mystory.adapters;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.zohirdev.mystory.items.CommentsItem;
import com.zohirdev.mystory.items.UsersItem;

import java.util.ArrayList;
import java.util.Objects;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<CommentsItem> commentsItemArrayList;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    public CommentsRecyclerAdapter(Context context, ArrayList<CommentsItem> commentsItemArrayList) {
        this.context = context;
        this.commentsItemArrayList = commentsItemArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
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
                                    snapshot.getString("language"),
                                    snapshot.getString("isBlocked"),
                                    snapshot.getString("deviceID"),
                                    snapshot.getString("verified")
                            );
                            if (commentsItemArrayList.get(position).getUserID().equals(usersItem.getUid())) {
                                Picasso.get().load(usersItem.getImageProfile()).into(holder.imageProfile);
                                holder.textFullName.setText(usersItem.getFullName());

                                if (usersItem.getVerified().equals("YES")) {
                                    holder.imageAccountVerified.setVisibility(View.VISIBLE);
                                } else {
                                    holder.imageAccountVerified.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
        holder.textComment.setText(commentsItemArrayList.get(position).getComment());
        holder.textDateAndTime.setText("\uD83D\uDCC5  " + commentsItemArrayList.get(position).getDate() + "  " +
                "\uD83D\uDD54  " + commentsItemArrayList.get(position).getTime());

        holder.buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu(context, holder.buttonMore);
                if (commentsItemArrayList.get(position).getUserID().equals(firebaseAuth.getUid()) ||
                        commentsItemArrayList.get(position).getPublisher().equals(firebaseAuth.getUid())) {
                    popUp.getMenuInflater().inflate(R.menu.popup_menu_my_comment, popUp.getMenu());
                } else {
                    popUp.getMenuInflater().inflate(R.menu.popup_menu_other_comment, popUp.getMenu());
                }

                popUp.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case (R.id.delete):
                            db.collection("STORY_COMMENTS")
                                    .document(commentsItemArrayList.get(position).getCommentID())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ((CommentsActivity) context).loadComments();
                                            Toast.makeText(context, "تم مسح التعليق", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;
                        case (R.id.copy):
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("copy", commentsItemArrayList.get(position).getComment());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "تم النسخ في الحافظة", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });
                popUp.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProfile;
        ImageView imageAccountVerified;
        TextView textFullName;
        TextView textComment;
        TextView textDateAndTime;
        Button buttonMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            imageAccountVerified = itemView.findViewById(R.id.imageAccountVerified);
            textFullName = itemView.findViewById(R.id.textFullName);
            textComment = itemView.findViewById(R.id.textComment);
            textDateAndTime = itemView.findViewById(R.id.textDateAndTime);
            buttonMore = itemView.findViewById(R.id.buttonMore);

        }
    }
}
