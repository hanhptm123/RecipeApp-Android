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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecipeUserAdapter extends RecyclerView.Adapter<RecipeUserAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    private int currentUserId;
    private KET_NOI_CSDL dbHelper;
    private int userId;
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private OnRecipeClickListener listener;

    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public RecipeUserAdapter(Context context, List<Recipe> recipeList, KET_NOI_CSDL dbHelper) {
        this.recipeList = recipeList;
        this.context = context;
        this.dbHelper = dbHelper;
        SharedPreferences sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = sharedPref.getInt("UserID", -1);
        this.userId = this.currentUserId;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_recipe_user, parent, false);
        return new RecipeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvTime.setText("Cook Time: " + recipe.getTime());
        holder.tvType.setText("Category: " + recipe.getType());
        holder.tvOrigin.setText("Origin: " + recipe.getOrigin());
        holder.tvDate.setText("Posted On: " + recipe.getDate());
        holder.tvUser.setText(String.valueOf(recipe.getUserId()));
        if (recipe.getIsApproved() != null && recipe.getIsApproved() == 0) {
            holder.tvRejectReason.setVisibility(View.VISIBLE);
            holder.tvRejectReason.setText("Reason reject: " + recipe.getRejectReason()); // giả sử bạn có field lý do
        } else {
            holder.tvRejectReason.setVisibility(View.GONE);
        }
        // Load ảnh món ăn
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


        holder.imgUser.setImageResource(recipe.getUserImage());

        // Lấy điểm trung bình số sao từ CSDL
        float avgRating = dbHelper.getAverageRatingByRecipeId(recipe.getId());
        holder.ratingBar.setRating(avgRating);

        // Kiểm tra đã yêu thích chưa
        boolean isFavourite = dbHelper.isRecipeFavourited(userId, recipe.getId());
        holder.btnFavorite.setImageResource(isFavourite ? R.drawable.heart_color : R.drawable.heart_no_color);

        // Xử lý nhấn nút yêu thích
        holder.btnFavorite.setOnClickListener(view -> {
            if (userId == -1) {
                // Chưa đăng nhập
                Toast.makeText(context, "Please login to add favourites.", Toast.LENGTH_SHORT).show();
            } else {
                // Đã đăng nhập → xử lý yêu thích
                boolean currentFav = dbHelper.isRecipeFavourited(userId, recipe.getId());
                if (currentFav) {
                    dbHelper.removeFavourite(userId, recipe.getId());
                    holder.btnFavorite.setImageResource(R.drawable.heart_no_color);
                } else {
                    dbHelper.addFavourite(userId, recipe.getId());
                    holder.btnFavorite.setImageResource(R.drawable.heart_color);
                }
            }
        });


        // Chuyển sang chi tiết công thức
        holder.itemView.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Recipe selectedRecipe = recipeList.get(pos);
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
                intent.putExtra("currentUserId", currentUserId);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvType, tvOrigin, tvDate, tvUser, tvRejectReason;
        ImageView imgRecipe, imgUser, btnFavorite;
        RatingBar ratingBar;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvType = itemView.findViewById(R.id.tvType);
            tvOrigin = itemView.findViewById(R.id.tvOrigin);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUser = itemView.findViewById(R.id.tvUser);
            imgRecipe = itemView.findViewById(R.id.imgRecipe);
            imgUser = itemView.findViewById(R.id.imgUser);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvRejectReason = itemView.findViewById(R.id.tvRejectReason);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }


    public void updateData(List<Recipe> newRecipeList) {
        this.recipeList = newRecipeList;
        notifyDataSetChanged();
    }

}
