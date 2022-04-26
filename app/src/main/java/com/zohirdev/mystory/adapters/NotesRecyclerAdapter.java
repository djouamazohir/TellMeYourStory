package com.zohirdev.mystory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zohirdev.mystory.R;
import com.zohirdev.mystory.items.NotesItem;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<NotesItem> notesItemArrayList;

    public NotesRecyclerAdapter(Context context, ArrayList<NotesItem> notesItemArrayList) {
        this.context = context;
        this.notesItemArrayList = notesItemArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.notes_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textStory.setText(notesItemArrayList.get(position).getStory());
    }

    @Override
    public int getItemCount() {
        return notesItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textStory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            textStory = itemView.findViewById(R.id.textStory);
        }
    }
}
