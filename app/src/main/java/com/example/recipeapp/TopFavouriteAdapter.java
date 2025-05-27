package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public class TopFavouriteAdapter extends RecyclerView.Adapter<TopFavouriteAdapter.ViewHolder> {
    private Context context;
    private KET_NOI_CSDL dbHelper;
    private int currentUserId;
    private ArrayList<Recipe> recipeList;

    // Constructor
    public TopFavouriteAdapter(Context context, ArrayList<Recipe> recipeList, KET_NOI_CSDL dbHelper, int currentUserId) {
        this.context = context;
        this.recipeList = recipeList;
        this.dbHelper = dbHelper;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.txtTitle.setText(recipe.getTitle());
        holder.txtDescription.setText(recipe.getDescription());
        holder.txtFavouriteCount.setText("❤ " + recipe.getTotalFavourites() + " favourites");
        holder.txtType.setText("Type: " + recipe.getType());
        holder.txtOrigin.setText("Origin: " + recipe.getOrigin());

        // Load ảnh
        Glide.with(context)
                .load(recipe.getImagePath())
                .placeholder(R.drawable.auto)
                .into(holder.imgRecipe);

        // Sự kiện click item
        holder.itemView.setOnClickListener(view -> {
            int recipeId = recipe.getId();
            Log.d("DB_LOG", "Clicked recipe id = " + recipeId);
            dbHelper.increaseCountView(recipeId);

            ArrayList<DetailRecipeIngredient> selectedIngredients = recipe.getDetailIngredients();
            if (selectedIngredients == null || selectedIngredients.isEmpty()) {
                selectedIngredients = dbHelper.getIngredientsByRecipeId(recipeId);
            }

            Intent intent = new Intent(view.getContext(), RecipeDetailHomeActivity.class);
            intent.putExtra("recipe", recipe);
            intent.putExtra("ingredients", selectedIngredients);
            intent.putExtra("currentUserId", currentUserId);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipe;
        TextView txtTitle, txtDescription, txtFavouriteCount, txtType, txtOrigin;
        // Bỏ txtUserName vì không còn dùng nữa

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtFavouriteCount = itemView.findViewById(R.id.txtFavouriteCount);
            txtType = itemView.findViewById(R.id.txtType);
            txtOrigin = itemView.findViewById(R.id.txtOrigin);
            // Không khởi tạo txtUserName nữa
        }
    }
}
