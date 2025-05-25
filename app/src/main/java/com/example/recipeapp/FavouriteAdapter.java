package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private ArrayList<Recipe> favouriteList;
    private Context context;
    private KET_NOI_CSDL dbHelper;

    public FavouriteAdapter(Context context, ArrayList<Recipe> favouriteList) {
        this.context = context;
        this.favouriteList = favouriteList;
        dbHelper = new KET_NOI_CSDL(context);
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Recipe recipe = favouriteList.get(position);

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvTime.setText("Cook Time: " + recipe.getTime());
        holder.tvType.setText("Category: " + recipe.getType());
        holder.tvOrigin.setText("Origin: " + recipe.getOrigin());
        holder.tvDate.setText("Posted On: " + recipe.getDate());
        holder.tvUser.setText(String.valueOf(recipe.getUserId())); // ✅ OK


        // Load ảnh từ imagePath
        String imagePath = recipe.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri uri = Uri.parse(imagePath);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    holder.imgRecipe.setImageBitmap(bitmap);
                } else {
                    holder.imgRecipe.setImageResource(R.drawable.pho);
                }
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                holder.imgRecipe.setImageResource(R.drawable.pho);
            }
        } else {
            holder.imgRecipe.setImageResource(R.drawable.pho);
        }

        holder.btnFavorite.setImageResource(R.drawable.heart_color);

        // Mở chi tiết recipe khi nhấn vào item
        holder.itemView.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Recipe selectedRecipe = favouriteList.get(pos);
                int recipeId = selectedRecipe.getId();
                Log.d("DB_LOG", "Clicked recipe id = " + recipeId);
                dbHelper.increaseCountView(recipeId);

                ArrayList<DetailRecipeIngredient> selectedIngredients = selectedRecipe.getDetailIngredients();
                if (selectedIngredients == null || selectedIngredients.isEmpty()) {
                    selectedIngredients = dbHelper.getIngredientsByRecipeId(recipeId);
                }

                Intent intent = new Intent(view.getContext(), RecipeDetailActivity.class);
                intent.putExtra("recipe", selectedRecipe);
                intent.putExtra("ingredients", selectedIngredients);
                view.getContext().startActivity(intent);
            }
        });

        // Xử lý hủy yêu thích
        holder.btnFavorite.setOnClickListener(v -> {
            SharedPreferences sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            int userId = sharedPref.getInt("UserID", -1);
            dbHelper.removeFavourite(userId, recipe.getId());
            favouriteList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, favouriteList.size());
        });
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvType, tvOrigin, tvDate, tvUser;
        ImageView imgRecipe;
        ImageButton btnFavorite;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle_favourite);
            tvTime = itemView.findViewById(R.id.tvTime_favourite);
            tvType = itemView.findViewById(R.id.tvType_favourite);
            tvOrigin = itemView.findViewById(R.id.tvOrigin_favourite);
            tvDate = itemView.findViewById(R.id.tvDate_favourite);
            tvUser = itemView.findViewById(R.id.tvUser_favourite);
            imgRecipe = itemView.findViewById(R.id.imgRecipe_favourite);
            btnFavorite = itemView.findViewById(R.id.btnFavorite_favourite);
        }
    }
}
