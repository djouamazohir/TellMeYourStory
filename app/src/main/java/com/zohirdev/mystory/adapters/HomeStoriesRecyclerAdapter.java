package com.zohirdev.mystory.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;
import com.zohirdev.mystory.R;
import com.zohirdev.mystory.activities.CommentsActivity;
import com.zohirdev.mystory.activities.MyProfileActivity;
import com.zohirdev.mystory.activities.ShowHashTagResultActivity;
import com.zohirdev.mystory.activities.ShowLikesListActivity;
import com.zohirdev.mystory.activities.UsersProfileActivity;
import com.zohirdev.mystory.activities.sff.StoriesActivity;
import com.zohirdev.mystory.items.CommentsItem;
import com.zohirdev.mystory.items.ReactStoryItem;
import com.zohirdev.mystory.items.StoriesItem;
import com.zohirdev.mystory.items.StorySavedItem;
import com.zohirdev.mystory.items.UsersItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class HomeStoriesRecyclerAdapter extends RecyclerView.Adapter<HomeStoriesRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<StoriesItem> storiesItemArrayList;
    ArrayList<String> storiesSavedItemArrayList;

    ArrayList<String> storiesCommentsItemArrayList;


    ArrayList<String> reactLOVEItemArrayList;
    ArrayList<String> reactHAHAItemArrayList;
    ArrayList<String> reactWOWItemArrayList;
    ArrayList<String> reactSADItemArrayList;
    ArrayList<String> reactANGRYItemArrayList;


    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    int[] LOVE;
    int[] HAHA;
    int[] WOW;
    int[] SAD;
    int[] ANGRY;

    int[] comments;
    private HashTagHelper mTextHashTagHelper;

    public HomeStoriesRecyclerAdapter(Context context, ArrayList<StoriesItem> storiesItemArrayList) {
        this.context = context;
        this.storiesItemArrayList = storiesItemArrayList;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storiesSavedItemArrayList = new ArrayList<>();
        storiesCommentsItemArrayList = new ArrayList<>();


        reactLOVEItemArrayList = new ArrayList<>();
        reactHAHAItemArrayList = new ArrayList<>();
        reactWOWItemArrayList = new ArrayList<>();
        reactSADItemArrayList = new ArrayList<>();
        reactANGRYItemArrayList = new ArrayList<>();

    }

    @NonNull
    @Override
    public HomeStoriesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.story_item, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull HomeStoriesRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        LOVE = new int[storiesItemArrayList.size()];
        HAHA = new int[storiesItemArrayList.size()];
        WOW = new int[storiesItemArrayList.size()];
        SAD = new int[storiesItemArrayList.size()];
        ANGRY = new int[storiesItemArrayList.size()];

        comments = new int[storiesItemArrayList.size()];

        Linkify.addLinks(holder.textStory, Linkify.ALL);

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
                        storiesSavedItemArrayList.clear();
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            StorySavedItem savedItem =
                                    new StorySavedItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("userID"),
                                            snapshot.getString("publisher")
                                    );
                            storiesSavedItemArrayList.add(savedItem.getUserID() + savedItem.getStoryID());
                        }

                        if (storiesSavedItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                            holder.imageSaveStory.setBackgroundResource(R.drawable.saved);
                        } else {
                            holder.imageSaveStory.setBackgroundResource(R.drawable.not_saved);
                        }
                    }
                });

        holder.cardSaveStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storiesSavedItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                    holder.imageSaveStory.setVisibility(View.GONE);
                    holder.progressBarSaveStory.setVisibility(View.VISIBLE);
                    db.collection("STORY_SAVED")
                            .document(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.progressBarSaveStory.setVisibility(View.GONE);
                                    holder.imageSaveStory.setVisibility(View.VISIBLE);
                                    holder.imageSaveStory.setBackgroundResource(R.drawable.not_saved);
                                    storiesSavedItemArrayList.remove(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID());
                                }
                            });
                }
                if (!storiesSavedItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                    holder.imageSaveStory.setVisibility(View.GONE);
                    holder.progressBarSaveStory.setVisibility(View.VISIBLE);

                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storiesItemArrayList.get(position).getStoryID());
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("publisher", storiesItemArrayList.get(position).getPublisher());

                    db.collection("STORY_SAVED")
                            .document(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                            .set(dataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.progressBarSaveStory.setVisibility(View.GONE);
                                    holder.imageSaveStory.setVisibility(View.VISIBLE);
                                    holder.imageSaveStory.setBackgroundResource(R.drawable.saved);
                                    storiesSavedItemArrayList.add(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID());
                                }
                            });
                }
            }
        });

        final Handler H1 = new Handler();
        H1.postDelayed(new Runnable() {
            @Override
            public void run() {
                H1.postDelayed(this, 1000);
                db.collection("STORY_REACT")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                reactLOVEItemArrayList.clear();
                                reactHAHAItemArrayList.clear();
                                reactWOWItemArrayList.clear();
                                reactSADItemArrayList.clear();
                                reactANGRYItemArrayList.clear();
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    ReactStoryItem reactStoryItem = new ReactStoryItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("userID"),
                                            snapshot.getString("reactType")
                                    );
                                    if (reactStoryItem.getReactType().equals("LOVE")) {
                                        reactLOVEItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("HAHA")) {
                                        reactHAHAItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("WOW")) {
                                        reactWOWItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("SAD")) {
                                        reactSADItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("ANGRY")) {
                                        reactANGRYItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                }

                                if (reactLOVEItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                                    holder.textLike.setText("أحببته");
                                    holder.textLike.setTextColor(context.getResources().getColor(R.color.love));
                                    holder.imageLike.setBackgroundResource(R.drawable.love);
                                } else {
                                    if (reactHAHAItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                                        holder.textLike.setText("أضحكني");
                                        holder.textLike.setTextColor(context.getResources().getColor(R.color.haha));
                                        holder.imageLike.setBackgroundResource(R.drawable.haha);
                                    } else {
                                        if (reactWOWItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                                            holder.textLike.setText("وااو");
                                            holder.textLike.setTextColor(context.getResources().getColor(R.color.wow));
                                            holder.imageLike.setBackgroundResource(R.drawable.wow);
                                        } else {
                                            if (reactSADItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                                                holder.textLike.setText("أحزنني");
                                                holder.textLike.setTextColor(context.getResources().getColor(R.color.sad));
                                                holder.imageLike.setBackgroundResource(R.drawable.sad);
                                            } else {
                                                if (reactANGRYItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())) {
                                                    holder.textLike.setText("أغضبني");
                                                    holder.textLike.setTextColor(context.getResources().getColor(R.color.angry));
                                                    holder.imageLike.setBackgroundResource(R.drawable.angry);
                                                } else {
                                                    holder.textLike.setText("أحببته");
                                                    holder.textLike.setTextColor(context.getResources().getColor(R.color.no_react));
                                                    holder.imageLike.setBackgroundResource(R.drawable.no_react);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
        }, 1000);

        final Handler H2 = new Handler();
        H2.postDelayed(new Runnable() {
            @Override
            public void run() {
                H2.postDelayed(this, 1000);
                db.collection("STORY_REACT")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                LOVE[position] = 0;
                                HAHA[position] = 0;
                                WOW[position] = 0;
                                SAD[position] = 0;
                                ANGRY[position] = 0;
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    ReactStoryItem reactStoryItem = new ReactStoryItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("userID"),
                                            snapshot.getString("reactType")
                                    );

                                    if (reactStoryItem.getReactType().equals("LOVE")) {
                                        reactLOVEItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("HAHA")) {
                                        reactHAHAItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("WOW")) {
                                        reactWOWItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("SAD")) {
                                        reactSADItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }
                                    if (reactStoryItem.getReactType().equals("ANGRY")) {
                                        reactANGRYItemArrayList.add(reactStoryItem.getUserID() + reactStoryItem.getStoryID());
                                    }

                                    if (reactStoryItem.getStoryID().equals(storiesItemArrayList.get(position).getStoryID())) {
                                        if (reactStoryItem.getReactType().equals("LOVE")) {
                                            LOVE[position] = LOVE[position] + 1;
                                        }
                                        if (reactStoryItem.getReactType().equals("HAHA")) {
                                            HAHA[position] = HAHA[position] + 1;
                                        }
                                        if (reactStoryItem.getReactType().equals("WOW")) {
                                            WOW[position] = WOW[position] + 1;
                                        }
                                        if (reactStoryItem.getReactType().equals("SAD")) {
                                            SAD[position] = SAD[position] + 1;
                                        }
                                        if (reactStoryItem.getReactType().equals("ANGRY")) {
                                            ANGRY[position] = ANGRY[position] + 1;
                                        }
                                    }
                                }

                                if (LOVE[position] != 0) {
                                    holder.cardLOVE.setVisibility(View.VISIBLE);
                                    holder.textLOVE.setText("" + LOVE[position]);
                                } else {
                                    holder.cardLOVE.setVisibility(View.GONE);
                                }

                                if (HAHA[position] != 0) {
                                    holder.cardHAHA.setVisibility(View.VISIBLE);
                                    holder.textHAHA.setText("" + HAHA[position]);
                                } else {
                                    holder.cardHAHA.setVisibility(View.GONE);
                                }

                                if (WOW[position] != 0) {
                                    holder.cardWOW.setVisibility(View.VISIBLE);
                                    holder.textWOW.setText("" + WOW[position]);
                                } else {
                                    holder.cardWOW.setVisibility(View.GONE);
                                }

                                if (SAD[position] != 0) {
                                    holder.cardSAD.setVisibility(View.VISIBLE);
                                    holder.textSAD.setText("" + SAD[position]);
                                } else {
                                    holder.cardSAD.setVisibility(View.GONE);
                                }

                                if (ANGRY[position] != 0) {
                                    holder.cardANGRY.setVisibility(View.VISIBLE);
                                    holder.textANGRY.setText("" + ANGRY[position]);
                                } else {
                                    holder.cardANGRY.setVisibility(View.GONE);
                                }
                            }
                        });
                db.collection("STORY_COMMENTS")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                comments[position] = 0;
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    CommentsItem commentsItem = new CommentsItem(
                                            snapshot.getString("storyID"),
                                            snapshot.getString("publisher"),
                                            snapshot.getString("commentID"),
                                            snapshot.getString("userID"),
                                            snapshot.getString("comment"),
                                            snapshot.getString("time"),
                                            snapshot.getString("date")
                                    );
                                    storiesCommentsItemArrayList.add(commentsItem.getStoryID());

                                    if (commentsItem.getStoryID().equals(storiesItemArrayList.get(position).getStoryID())) {
                                        comments[position] = comments[position] + 1;
                                    }
                                }
                                if (comments[position] != 0) {
                                    holder.cardCOMMENT.setVisibility(View.VISIBLE);
                                    holder.textCOMMENT.setText("" + comments[position]);
                                } else {
                                    holder.cardCOMMENT.setVisibility(View.GONE);
                                }
                            }
                        });

            }
        }, 1000);

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


        holder.cardLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reactLOVEItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID()) ||
                        reactHAHAItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID()) ||
                        reactWOWItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID()) ||
                        reactSADItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID()) ||
                        reactANGRYItemArrayList.contains(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())

                ) {
                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid()
                                    + storiesItemArrayList.get(position).getStoryID())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.textLike.setText("أحببته");
                                    holder.textLike.setTextColor(context.getResources().getColor(R.color.no_react));
                                    holder.imageLike.setBackgroundResource(R.drawable.no_react);
                                    reactLOVEItemArrayList.clear();
                                    reactHAHAItemArrayList.clear();
                                    reactWOWItemArrayList.clear();
                                    reactSADItemArrayList.clear();
                                    reactANGRYItemArrayList.clear();

                                    db.collection("NOTIFICATIONS")
                                            .document(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID())
                                            .delete();
                                }
                            });
                } else {
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storiesItemArrayList.get(position).getStoryID());
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("reactType", "LOVE");

                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid() +
                                    storiesItemArrayList.get(position).getStoryID())
                            .set(dataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    holder.textLike.setText("أحببته");
                                    holder.textLike.setTextColor(context.getResources().getColor(R.color.love));
                                    holder.imageLike.setBackgroundResource(R.drawable.love);
                                    reactLOVEItemArrayList.clear();
                                    reactHAHAItemArrayList.clear();
                                    reactWOWItemArrayList.clear();
                                    reactSADItemArrayList.clear();
                                    reactANGRYItemArrayList.clear();

                                    reactLOVEItemArrayList.add(firebaseAuth.getUid() + storiesItemArrayList.get(position).getStoryID());

                                    if (!storiesItemArrayList.get(position).getPublisher().equals(firebaseAuth.getUid())) {
                                        long id = new Date().getTime();
                                        LocalDate today = LocalDate.now();
                                        LocalTime now = LocalTime.now();

                                        String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                                        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));

                                        HashMap<String, String> dataMap = new HashMap<>();
                                        dataMap.put("notificationID", firebaseAuth.getUid()
                                                + storiesItemArrayList.get(position).getStoryID());
                                        dataMap.put("notificationType", "POST_LIKE");
                                        dataMap.put("order", "" + id);
                                        dataMap.put("storyID", storiesItemArrayList.get(position).getStoryID());
                                        dataMap.put("sender", firebaseAuth.getUid());
                                        dataMap.put("receiver", storiesItemArrayList.get(position).getPublisher());
                                        dataMap.put("seen", "NO");
                                        dataMap.put("time", time);
                                        dataMap.put("date", date);

                                        db.collection("NOTIFICATIONS")
                                                .document(firebaseAuth.getUid() +
                                                        storiesItemArrayList.get(position).getStoryID())
                                                .set(dataMap);
                                    }
                                }
                            });
                }
            }
        });

        holder.cardLike.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ViewDialogREACT viewDialogREACT = new ViewDialogREACT();
                viewDialogREACT.showDialog(context, storiesItemArrayList.get(position).getStoryID());
                return false;
            }
        });
        holder.textStory.setText(storiesItemArrayList.get(position).getStory());

        holder.textTimeAgo.setText("\uD83D\uDCC5  " + storiesItemArrayList.get(position).getDate() + "  " +
                "\uD83D\uDD54  " + storiesItemArrayList.get(position).getTime());


        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storiesItemArrayList.get(position).getPublisher().equals(firebaseAuth.getUid())) {
                    Intent intent;
                    intent = new Intent(context, MyProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent;
                    intent = new Intent(context, UsersProfileActivity.class);
                    intent.putExtra("uid", storiesItemArrayList.get(position).getPublisher());
                    context.startActivity(intent);
                }
            }
        });

        holder.textFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storiesItemArrayList.get(position).getPublisher().equals(firebaseAuth.getUid())) {
                    Intent intent;
                    intent = new Intent(context, MyProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent;
                    intent = new Intent(context, UsersProfileActivity.class);
                    intent.putExtra("uid", storiesItemArrayList.get(position).getPublisher());
                    context.startActivity(intent);
                }
            }
        });

        holder.textUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storiesItemArrayList.get(position).getPublisher().equals(firebaseAuth.getUid())) {
                    Intent intent;
                    intent = new Intent(context, MyProfileActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent;
                    intent = new Intent(context, UsersProfileActivity.class);
                    intent.putExtra("uid", storiesItemArrayList.get(position).getPublisher());
                    context.startActivity(intent);
                }
            }
        });

        holder.cardMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu(context, holder.cardMore);
                if (storiesItemArrayList.get(position).getPublisher().equals("" + firebaseAuth.getUid())) {
                    popUp.getMenuInflater().inflate(R.menu.popup_menu_my_story, popUp.getMenu());
                } else {
                    popUp.getMenuInflater().inflate(R.menu.popup_menu_other_story, popUp.getMenu());
                }

                popUp.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case (R.id.delete):
                            db.collection("STORIES")
                                    .document(storiesItemArrayList.get(position).getStoryID())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ((StoriesActivity) context).loadStories();
                                            Toast.makeText(context, "تم مسح القصة", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            break;
                        case (R.id.copy):
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("copy", storiesItemArrayList.get(position).getStory());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "تم النسخ في الحافظة", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return false;
                });
                popUp.show();
            }
        });

        holder.buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = holder.textComment.getText().toString();
                postComment(comment, storiesItemArrayList.get(position).getStoryID(), storiesItemArrayList.get(position).getPublisher());
                holder.textComment.setText("");
            }
        });

        mTextHashTagHelper = HashTagHelper.Creator.create(context.getResources().getColor(R.color.holo_blue_dark),
                new HashTagHelper.OnHashTagClickListener() {
                    @Override
                    public void onHashTagClicked(String hashTag) {

                        Intent intent;
                        intent = new Intent(context, ShowHashTagResultActivity.class);
                        intent.putExtra("hashTag", hashTag);
                        context.startActivity(intent);

                    }
                });

        mTextHashTagHelper.handle(holder.textStory);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postComment(String comment, String storyID, String publisher) {
        long id = new Date().getTime();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));


        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("storyID", storyID);
        dataMap.put("publisher", publisher);
        dataMap.put("commentID", id + firebaseAuth.getUid() + storyID);
        dataMap.put("userID", firebaseAuth.getUid());
        dataMap.put("comment", comment);
        dataMap.put("time", time);
        dataMap.put("date", date);


        db.collection("STORY_COMMENTS")
                .document("" + id + firebaseAuth.getUid() + storyID)
                .set(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NewApi")
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!publisher.equals(firebaseAuth.getUid())) {
                            addNotification(storyID, publisher);
                        }

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification(String storyID, String publisher) {

        long id = new Date().getTime();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        String date = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));


        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("notificationID", id + firebaseAuth.getUid());
        dataMap.put("notificationType", "POST_COMMENT");
        dataMap.put("order", "" + id);
        dataMap.put("storyID", storyID);
        dataMap.put("sender", firebaseAuth.getUid());
        dataMap.put("receiver", publisher);
        dataMap.put("seen", "NO");
        dataMap.put("time", time);
        dataMap.put("date", date);

        db.collection("NOTIFICATIONS")
                .document(id + firebaseAuth.getUid())
                .set(dataMap);
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

        CardView cardView;
        TextView textTimeAgo;

        CardView cardMore;
        TextView textLikes;

        EditText textComment;
        Button buttonPostComment;

        CardView cardLOVE;
        CardView cardHAHA;
        CardView cardWOW;
        CardView cardSAD;
        CardView cardANGRY;
        CardView cardCOMMENT;

        TextView textLOVE;
        TextView textHAHA;
        TextView textWOW;
        TextView textSAD;
        TextView textANGRY;
        TextView textCOMMENT;

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
            cardView = itemView.findViewById(R.id.cardView);
            textTimeAgo = itemView.findViewById(R.id.textTimeAndDate);

            cardMore = itemView.findViewById(R.id.cardViewMore);
            textLikes = itemView.findViewById(R.id.textCountLikes);

            textComment = itemView.findViewById(R.id.textComment);
            buttonPostComment = itemView.findViewById(R.id.buttonPostComment);


            cardLOVE = itemView.findViewById(R.id.cardLOVE);
            cardLOVE.setVisibility(View.GONE);
            cardHAHA = itemView.findViewById(R.id.cardHAHA);
            cardHAHA.setVisibility(View.GONE);
            cardWOW = itemView.findViewById(R.id.cardWOW);
            cardWOW.setVisibility(View.GONE);
            cardSAD = itemView.findViewById(R.id.cardSAD);
            cardSAD.setVisibility(View.GONE);
            cardANGRY = itemView.findViewById(R.id.cardANGRY);
            cardANGRY.setVisibility(View.GONE);
            cardCOMMENT = itemView.findViewById(R.id.cardCOMMENT);
            cardCOMMENT.setVisibility(View.GONE);

            textLOVE = itemView.findViewById(R.id.textLOVE);
            textHAHA = itemView.findViewById(R.id.textHAHA);
            textWOW = itemView.findViewById(R.id.textWOW);
            textSAD = itemView.findViewById(R.id.textSAD);
            textANGRY = itemView.findViewById(R.id.textANGRY);
            textCOMMENT = itemView.findViewById(R.id.textCOMMENT);

        }
    }

    public class ViewDialogREACT {
        @SuppressLint({"SetTextI18n", "ResourceType"})
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void showDialog(Context context, String storyID) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alert_dialog_reacts);

            ImageView imageLOVE = dialog.findViewById(R.id.reactLOVE);
            ImageView imageHAHA = dialog.findViewById(R.id.reactHAHA);
            ImageView imageWOW = dialog.findViewById(R.id.reactWOW);
            ImageView imageSAD = dialog.findViewById(R.id.reactSAD);
            ImageView imageANGRY = dialog.findViewById(R.id.reactANGRY);


            imageLOVE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storyID);
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("reactType", "LOVE");

                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid() +
                                    storyID)
                            .set(dataMap);
                }
            });

            imageHAHA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storyID);
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("reactType", "HAHA");

                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid() +
                                    storyID)
                            .set(dataMap);
                }
            });

            imageWOW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storyID);
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("reactType", "WOW");

                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid() +
                                    storyID)
                            .set(dataMap);
                }
            });

            imageSAD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storyID);
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("reactType", "SAD");

                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid() +
                                    storyID)
                            .set(dataMap);
                }
            });

            imageANGRY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    HashMap<String, String> dataMap = new HashMap<>();
                    dataMap.put("storyID", storyID);
                    dataMap.put("userID", firebaseAuth.getUid());
                    dataMap.put("reactType", "ANGRY");

                    db.collection("STORY_REACT")
                            .document("" + firebaseAuth.getUid() +
                                    storyID)
                            .set(dataMap);
                }
            });
            Objects.requireNonNull(dialog.getWindow()).

                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }

}
