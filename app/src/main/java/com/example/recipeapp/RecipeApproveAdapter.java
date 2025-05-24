package com.example.recipeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeApproveAdapter extends RecyclerView.Adapter<RecipeApproveAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private OnRecipeActionListener listener;

    public interface OnRecipeActionListener {
        void onApprove(Recipe recipe);
        void onReject(Recipe recipe);
    }

    public RecipeApproveAdapter(List<Recipe> recipeList, OnRecipeActionListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_approve, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeTitle.setText(recipe.getTitle());
        holder.tvRecipeUser.setText("By: " + recipe.getUserId());
        holder.tvRecipeDate.setText(recipe.getDate());

        Integer isApproved = recipe.getIsApproved();

        if (isApproved == null) {
            // Pending - hiển thị nút
            holder.llButtons.setVisibility(View.VISIBLE);
            holder.tvApprovedStatus.setVisibility(View.GONE);
            holder.tvRejectReason.setVisibility(View.GONE);
            holder.etRejectReasonInput.setVisibility(View.GONE);
            holder.btnConfirmReject.setVisibility(View.GONE);
        } else if (isApproved == 1) {
            holder.llButtons.setVisibility(View.GONE);
            holder.tvApprovedStatus.setVisibility(View.VISIBLE);
            holder.tvApprovedStatus.setText("Approved");
            holder.tvRejectReason.setVisibility(View.GONE);
            holder.etRejectReasonInput.setVisibility(View.GONE);
            holder.btnConfirmReject.setVisibility(View.GONE);
        } else {
            holder.llButtons.setVisibility(View.GONE);
            holder.tvApprovedStatus.setVisibility(View.GONE);
            holder.tvRejectReason.setVisibility(View.VISIBLE);
            holder.tvRejectReason.setText("Rejected: " + (recipe.getRejectReason() != null ? recipe.getRejectReason() : ""));
            holder.etRejectReasonInput.setVisibility(View.GONE);
            holder.btnConfirmReject.setVisibility(View.GONE);
        }

        holder.btnApprove.setOnClickListener(v -> {
            recipe.setIsApproved(1);
            if (listener != null) listener.onApprove(recipe);
        });

        holder.btnReject.setOnClickListener(v -> {
            holder.etRejectReasonInput.setVisibility(View.VISIBLE);
            holder.btnConfirmReject.setVisibility(View.VISIBLE);
        });

        holder.btnConfirmReject.setOnClickListener(v -> {
            String reason = holder.etRejectReasonInput.getText().toString().trim();
            if (!reason.isEmpty()) {
                recipe.setIsApproved(0);
                recipe.setRejectReason(reason);
                if (listener != null) listener.onReject(recipe);
            } else {
                holder.etRejectReasonInput.setError("Please enter a reason");
            }
        });
    }


    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeTitle, tvRecipeUser, tvRecipeDate, tvApprovedStatus, tvRejectReason;
        LinearLayout llButtons;
        Button btnApprove, btnReject;
        EditText etRejectReasonInput;
        Button btnConfirmReject;


        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeUser = itemView.findViewById(R.id.tvRecipeUser);
            tvRecipeDate = itemView.findViewById(R.id.tvRecipeDate);
            tvApprovedStatus = itemView.findViewById(R.id.tvApprovedStatus);
            tvRejectReason = itemView.findViewById(R.id.tvRejectReason);
            llButtons = itemView.findViewById(R.id.llButtons);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            etRejectReasonInput = itemView.findViewById(R.id.etRejectReasonInput);
            btnConfirmReject = itemView.findViewById(R.id.btnConfirmReject);

        }
    }

    public void updateList(List<Recipe> newList) {
        recipeList = newList;
        notifyDataSetChanged();
    }
}
