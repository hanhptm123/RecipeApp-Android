package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

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

    public RecipeAdapter(Context context, List<Recipe> recipeList, KET_NOI_CSDL dbHelper) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_recipe, parent, false);
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
        // Lấy username của chủ công thức
        int recipeOwnerId = recipe.getUserId();
        Cursor userCursor = dbHelper.Doc_bang("SELECT UserName FROM Users WHERE UserID = " + recipeOwnerId);
        if (userCursor != null && userCursor.moveToFirst()) {
            int colIndex = userCursor.getColumnIndex("UserName");
            if (colIndex != -1) {
                String username = userCursor.getString(colIndex);
                holder.tvUser.setText(username);
            } else {
                holder.tvUser.setText("Unknown");
            }
            userCursor.close();
        } else {
            holder.tvUser.setText("Unknown");
        }



        // Load ảnh món ăn bằng Glide
        String imagePath = recipe.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.pho)
                    .error(R.drawable.pho)
                    .into(holder.imgRecipe);
        } else {
            holder.imgRecipe.setImageResource(R.drawable.pho);
        }

        // Lấy avatar của chủ công thức từ CSDL

        Cursor cursor = dbHelper.Doc_bang("SELECT Avatar FROM Users WHERE UserID = " + recipeOwnerId);
        String avatarName = null;
        if (cursor.moveToFirst()) {
            int colIndex = cursor.getColumnIndex("Avatar");
            if (colIndex != -1) {
                avatarName = cursor.getString(colIndex);
            }
        }
        cursor.close();

        if (avatarName == null || avatarName.equals("profile.png")) {
            holder.imgUser.setImageResource(R.drawable.profile); // Ảnh mặc định
        } else {
            Glide.with(context)
                    .load(avatarName) // Đường dẫn hoặc URL ảnh
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(holder.imgUser);
        }


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
                boolean currentFav = dbHelper.isRecipeFavourited(userId, recipe.getId());
                if (currentFav) {
                    dbHelper.removeFavourite(userId, recipe.getId());
                    holder.btnFavorite.setImageResource(R.drawable.heart_no_color);
                    Toast.makeText(context, "Removed from favourites: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addFavourite(userId, recipe.getId());
                    holder.btnFavorite.setImageResource(R.drawable.heart_color);
                    Toast.makeText(context, "Added to favourites: " + recipe.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Xử lý nhấn item
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            } else {
                // Nếu không có listener, mở trực tiếp detail
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvType, tvOrigin, tvDate, tvUser;
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
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }

    public void updateData(List<Recipe> newRecipeList) {
        this.recipeList = newRecipeList;
        notifyDataSetChanged();
    }
}
