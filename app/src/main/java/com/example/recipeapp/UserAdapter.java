package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> userList;
    private KET_NOI_CSDL dbHelper;
    private Context context;
    private String currentStatus = "active";

    public UserAdapter(ArrayList<User> userList, KET_NOI_CSDL dbHelper, Context context) {
        this.userList = userList;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    public void setCurrentStatus(String status) {
        this.currentStatus = status;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.txtName.setText(user.getName());
        holder.txtEmail.setText(user.getEmail());

        List<String> reasons = new ArrayList<>();
        reasons.add(""); // Position 0 = icon
        String[] reasonArray = context.getResources().getStringArray(R.array.ban_reasons);
        for (String reason : reasonArray) {
            reasons.add(reason);
        }

        SpinnerReasonAdapter adapter = new SpinnerReasonAdapter(context, reasons);
        holder.spinnerReason.setAdapter(adapter);

        holder.spinnerReason.setVisibility(currentStatus.equals("active") ? View.VISIBLE : View.GONE);

        String avatarPath = user.getAvatar();
        if (avatarPath != null && !avatarPath.trim().isEmpty()) {
            Glide.with(context)
                    .load(avatarPath.trim())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar);
        }

        holder.btnBan.setImageResource(
                currentStatus.equals("active") ? R.drawable.ban : R.drawable.unban
        );

        holder.btnBan.setOnClickListener(v -> {
            if (currentStatus.equals("active")) {
                String reason = holder.spinnerReason.getSelectedItem().toString();
                if (reason.isEmpty()) {
                    Toast.makeText(context, "Please select a reason for the ban", Toast.LENGTH_SHORT).show();

                    return;
                }
                String update = "UPDATE Users SET IsBanned = 1, BanReason = '" + reason + "' WHERE UserID = " + user.getId();
                dbHelper.Ghi_bang(update);
                Toast.makeText(context, "User " + user.getName() + " has been banned", Toast.LENGTH_SHORT).show();

            } else {
                String update = "UPDATE Users SET IsBanned = 0, BanReason = NULL WHERE UserID = " + user.getId();
                dbHelper.Ghi_bang(update);
                Toast.makeText(context, "User " + user.getName() + " has been unbanned", Toast.LENGTH_SHORT).show();

            }

            if (context instanceof User_management) {
                ((User_management) context).recreate();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtEmail;
        Spinner spinnerReason;
        ImageButton btnBan;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            spinnerReason = itemView.findViewById(R.id.spinnerReason);
            btnBan = itemView.findViewById(R.id.btnBan);
        }
    }
}