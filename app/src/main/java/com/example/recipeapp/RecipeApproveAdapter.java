package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        String userName = dbHelper.getUserNameById(recipe.getUserId());
        holder.tvRecipeUser.setText("By: " + userName);        holder.tvRecipeDate.setText(recipe.getDate());
        // Reset trạng thái UI
        holder.tvApprovedStatus.setVisibility(View.GONE);
        holder.tvRejectReason.setVisibility(View.GONE);
        holder.etRejectReasonInput.setVisibility(View.GONE);
        holder.btnConfirmReject.setVisibility(View.GONE);
        holder.btnApprove.setVisibility(View.VISIBLE);
        holder.btnReject.setVisibility(View.VISIBLE);

        // Kiểm tra trạng thái duyệt với kiểm tra null an toàn
        Integer status = recipe.getIsApproved();

        if (status != null && status == 1) {
            holder.tvApprovedStatus.setVisibility(View.VISIBLE);
            holder.tvApprovedStatus.setText("Đã duyệt");
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        } else if (status != null && status == 0) {
            holder.tvRejectReason.setVisibility(View.VISIBLE);
            holder.tvRejectReason.setText("Từ chối: " + recipe.getRejectReason());
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        // Duyệt
        holder.btnApprove.setOnClickListener(v -> {
            recipe.setIsApproved(1);
            recipe.setRejectReason("");
            dbHelper.updateRecipeStatus(recipe);
            notifyDataSetChanged();
        });

        // Từ chối - hiển thị ô nhập lý do
        holder.btnReject.setOnClickListener(v -> {
            holder.etRejectReasonInput.setVisibility(View.VISIBLE);
            holder.btnConfirmReject.setVisibility(View.VISIBLE);
        });

        // Xác nhận từ chối
        holder.btnConfirmReject.setOnClickListener(v -> {
            String reason = holder.etRejectReasonInput.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập lý do từ chối", Toast.LENGTH_SHORT).show();
                return;
            }
            recipe.setIsApproved(0);
            recipe.setRejectReason(reason);
            dbHelper.updateRecipeStatus(recipe);
            notifyDataSetChanged();
        });

        // Xem chi tiết
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
        }
    }
}
