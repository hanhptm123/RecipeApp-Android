package com.example.recipeapp;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeCommentAdapter extends RecyclerView.Adapter<HomeCommentAdapter.CommentViewHolder> {

    private List<Rating> commentList;
    private KET_NOI_CSDL db;

    public HomeCommentAdapter(List<Rating> commentList, KET_NOI_CSDL db) {
        this.commentList = commentList;
        this.db = db;
    }

    public void updateData(List<Rating> newData) {
        this.commentList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Rating comment = commentList.get(position);

        holder.userName.setText(comment.getUserName());
        holder.ratingBar.setRating(comment.getRatingScore());

        Log.d("DEBUG_DATE", "Original date: " + comment.getCreatedAt());
        holder.commentDate.setText(formatDate(comment.getCreatedAt()));

        holder.commentText.setText(comment.getComment());

        int userId = comment.getUserId();
        String avatarName = null;
        Cursor cursor = db.Doc_bang("SELECT Avatar FROM Users WHERE UserID = " + userId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int colIndex = cursor.getColumnIndex("Avatar");
                if (colIndex != -1) {
                    avatarName = cursor.getString(colIndex);
                }
            }
            cursor.close();
        }

        if (avatarName == null || avatarName.equals("profile.png")) {
            holder.userAvatar.setImageResource(R.drawable.profile);
        } else {
            Glide.with(holder.userAvatar.getContext())
                    .load(avatarName)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(holder.userAvatar);
        }
    }

    private String formatDate(String originalDate) {
        try {
            long timestamp = Long.parseLong(originalDate); // createdAt là dạng milliseconds
            Date date = new Date(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return originalDate;
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        RatingBar ratingBar;
        TextView commentDate;
        TextView commentText;

        public CommentViewHolder(View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.image_user_avatar);
            userName = itemView.findViewById(R.id.text_user_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            commentDate = itemView.findViewById(R.id.text_rating_date);
            commentText = itemView.findViewById(R.id.text_rating_comment);
        }
    }
}
