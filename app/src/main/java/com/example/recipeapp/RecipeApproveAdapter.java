package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecipeApproveAdapter extends RecyclerView.Adapter<RecipeApproveAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private KET_NOI_CSDL dbHelper;

    public RecipeApproveAdapter(List<Recipe> recipes, Context context, KET_NOI_CSDL dbHelper) {
        this.recipes = recipes;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_approve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.tvRecipeTitle.setText(recipe.getTitle());

        // Hiển thị tên người dùng từ DB
        String userName = dbHelper.getUserNameById(recipe.getUserId());
        holder.tvRecipeUser.setText("By: " + userName);

        holder.tvRecipeDate.setText(recipe.getDate());

        // Load ảnh món ăn bằng Glide (như RecipeAdapter)
        String imagePath = recipe.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.auto)
                    .error(R.drawable.auto)
                    .into(holder.imgRecipe);
        } else {
            holder.imgRecipe.setImageResource(R.drawable.auto);
        }

        // Reset UI trạng thái
        holder.llButtons.setVisibility(View.GONE);
        holder.tvApprovedStatus.setVisibility(View.GONE);
        holder.tvRejectReason.setVisibility(View.GONE);
        holder.etRejectReasonInput.setVisibility(View.GONE);
        holder.btnConfirmReject.setVisibility(View.GONE);

        Integer status = recipe.getIsApproved();

        if (status == null) {
            // Chờ duyệt
            holder.llButtons.setVisibility(View.VISIBLE);
        } else if (status == 1) {
            // Đã duyệt
            holder.tvApprovedStatus.setVisibility(View.VISIBLE);
            holder.tvApprovedStatus.setText("Approved");
        } else if (status == 0) {
            // Đã từ chối
            holder.tvRejectReason.setVisibility(View.VISIBLE);
            holder.tvRejectReason.setText("Rejected: " + recipe.getRejectReason());
        }

        // Xử lý duyệt
        holder.btnApprove.setOnClickListener(v -> {
            recipe.setIsApproved(1);
            recipe.setRejectReason("");
            dbHelper.updateRecipeStatus(recipe);
            notifyDataSetChanged();
        });

        // Xử lý từ chối (hiện input lý do)
        holder.btnReject.setOnClickListener(v -> {
            holder.etRejectReasonInput.setVisibility(View.VISIBLE);
            holder.btnConfirmReject.setVisibility(View.VISIBLE);
        });

        // Xác nhận từ chối với lý do
        holder.btnConfirmReject.setOnClickListener(v -> {
            String reason = holder.etRejectReasonInput.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(context, "Please give reject reason!", Toast.LENGTH_SHORT).show();
                return;
            }
            recipe.setIsApproved(0);
            recipe.setRejectReason(reason);
            dbHelper.updateRecipeStatus(recipe);
            notifyDataSetChanged();
        });

        // Xem chi tiết qua Intent
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("recipe", recipe);
            intent.putExtra("ingredients", new ArrayList<>(dbHelper.getIngredientsByRecipeId(recipe.getRecipeId())));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeTitle, tvApprovedStatus, tvRejectReason, tvRecipeUser, tvRecipeDate;
        EditText etRejectReasonInput;
        Button btnApprove, btnReject, btnConfirmReject, btnView;
        ImageView imgRecipe;
        LinearLayout llButtons;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvApprovedStatus = itemView.findViewById(R.id.tvApprovedStatus);
            tvRejectReason = itemView.findViewById(R.id.tvRejectReason);
            etRejectReasonInput = itemView.findViewById(R.id.etRejectReasonInput);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnConfirmReject = itemView.findViewById(R.id.btnConfirmReject);
            btnView = itemView.findViewById(R.id.btnViewDetail);
            tvRecipeUser = itemView.findViewById(R.id.tvRecipeUser);
            tvRecipeDate = itemView.findViewById(R.id.tvRecipeDate);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            llButtons = itemView.findViewById(R.id.llButtons);
        }
    }
}
