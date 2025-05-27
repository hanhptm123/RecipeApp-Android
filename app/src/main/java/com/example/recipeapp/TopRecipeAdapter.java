package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopRecipeAdapter extends RecyclerView.Adapter<TopRecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private KET_NOI_CSDL dbHelper;

    public TopRecipeAdapter(Context context, List<Recipe> recipeList, KET_NOI_CSDL dbHelper) {
        this.context = context;
        this.recipeList = recipeList;
        this.dbHelper = dbHelper;
    }

    public void updateData(List<Recipe> newList) {
        this.recipeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(recipe.getTitle());
        holder.textCountView.setText("Views: " + recipe.getCountView());
        // Lấy tên người tạo bài viết
        String username = dbHelper.getUsernameByUserId(recipe.getUserId());
        holder.tvUser.setText("By: " + username);



        // Xem chi tiết
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailHomeActivity.class);
            intent.putExtra("recipe", recipe);
            intent.putExtra("ingredients", new ArrayList<>(dbHelper.getIngredientsByRecipeId(recipe.getRecipeId())));
            context.startActivity(intent);
        });
        // Xem chi tiết
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailHomeActivity.class);
            intent.putExtra("recipe", recipe);
            intent.putExtra("ingredients", new ArrayList<>(dbHelper.getIngredientsByRecipeId(recipe.getRecipeId())));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvTitle, textCountView, tvUser;
        Button btnView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            textCountView = itemView.findViewById(R.id.text_count_view);
            tvUser = itemView.findViewById(R.id.tvUser);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}