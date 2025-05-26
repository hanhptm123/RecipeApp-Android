package com.example.recipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecipeDetailHomeActivity extends AppCompatActivity {

    private ImageView imageRecipe, avatarImage;
    private TextView textTitle, textTime, textType, textOrigin, textDate, textUser, textInstruction, textDescription;
    private TextView textRatingCount, textRatingSummary, textCountView;
    private LinearLayout ingredientsContainer, commentSection;
    private Button btnGoBack, btnSubmitRating;
    private RatingBar ratingBar, ratingAverage;
    private EditText editComment;
    private RecyclerView recyclerComments;

    private KET_NOI_CSDL db;
    private Recipe recipe;
    private ArrayList<DetailRecipeIngredient> detailIngredients;
    private List<Rating> allComments = new ArrayList<>();
    private HomeCommentAdapter commentAdapter;

    private int currentUserId, currentRecipeId;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recipe_home);

        db = new KET_NOI_CSDL(this);

        // Ánh xạ view
        mapViews();

        // Nhận dữ liệu từ Intent
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        detailIngredients = (ArrayList<DetailRecipeIngredient>) getIntent().getSerializableExtra("ingredients");
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = sharedPref.getInt("UserID", -1);

        // Load avatar người dùng
        loadUserAvatar(currentUserId);

        if (recipe != null) {
            currentRecipeId = recipe.getRecipeId();
            displayRecipeInfo(recipe);
            displayIngredients(detailIngredients);
            textCountView.setText("Lượt xem: " + recipe.getCountView());


            boolean isLoggedIn = (currentUserId != -1);
            boolean isOwner = (isLoggedIn && currentUserId == recipe.getUserId());
            boolean hasRated = false;
            boolean hasCommented = false;
            if (isLoggedIn) {
                hasRated = db.hasUserRated(currentRecipeId, currentUserId);
                hasCommented = db.hasUserCommented(currentRecipeId, currentUserId);
            }
            boolean hasRatedOrCommented = isLoggedIn && (hasRated || hasCommented);

            Log.d("DEBUG", "isLoggedIn=" + isLoggedIn + ", isOwner=" + isOwner + ", hasRated=" + hasRated + ", hasCommented=" + hasCommented);

            if (!isLoggedIn || isOwner || hasRatedOrCommented) {
                commentSection.setVisibility(View.GONE);
                Log.d("DEBUG", "Comment section GONE");
            } else {
                commentSection.setVisibility(View.VISIBLE);
                Log.d("DEBUG", "Comment section VISIBLE");
            }

            btnSubmitRating.setOnClickListener(v -> submitRating());

            // Xử lý hiển thị bình luận
            setupCommentSection();

            // Gán sự kiện nút
            btnGoBack.setOnClickListener(v -> finish());


            setupFilterButtons();
        }
    }

    private void mapViews() {
        imageRecipe = findViewById(R.id.image_recipe);
        avatarImage = findViewById(R.id.rating_avatar_image);
        textTitle = findViewById(R.id.text_title);
        textTime = findViewById(R.id.text_time);
        textType = findViewById(R.id.text_type);
        textOrigin = findViewById(R.id.text_origin);
        textDate = findViewById(R.id.text_date);
        textUser = findViewById(R.id.text_user);
        textInstruction = findViewById(R.id.text_instruction);
        textDescription = findViewById(R.id.text_description);
        textRatingCount = findViewById(R.id.text_rating_count);
        textRatingSummary = findViewById(R.id.rating_summary);
        ratingBar = findViewById(R.id.rating_bar);
        ratingAverage = findViewById(R.id.rating_average);
        editComment = findViewById(R.id.rating_edit_comment);
        textCountView = findViewById(R.id.text_count_view);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        commentSection = findViewById(R.id.comment_section);
        btnGoBack = findViewById(R.id.btn_go_back);
        btnSubmitRating = findViewById(R.id.rating_btn_submit_comment);
        recyclerComments = findViewById(R.id.recycler_comments);
    }

    private void loadUserAvatar(int userId) {
        if (userId == -1) {
            avatarImage.setImageResource(R.drawable.profile);
            return;
        }
        Cursor cursor = db.Doc_bang("SELECT Avatar FROM Users WHERE UserID = " + userId);
        String avatarName = null;
        if (cursor.moveToFirst()) {
            avatarName = cursor.getString(cursor.getColumnIndexOrThrow("Avatar"));
        }
        cursor.close();

        if (avatarName == null || avatarName.equals("profile.png")) {
            avatarImage.setImageResource(R.drawable.profile);
        } else {
            Glide.with(this)
                    .load(avatarName)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(avatarImage);
        }
    }

    private void displayRecipeInfo(Recipe recipe) {
        textTitle.setText(recipe.getTitle());
        textTime.setText("Thời gian: " + recipe.getTime());
        textType.setText("Loại: " + recipe.getType());
        textOrigin.setText("Xuất xứ: " + recipe.getOrigin());
        textDate.setText("Ngày đăng: " + recipe.getDate());
        String username = db.getUsernameByUserId(recipe.getUserId());
        Log.d("DEBUG_USERNAME", "Username: " + username);

        textUser.setText("Người đăng: " + username);
        textDescription.setText(recipe.getDescription());
        textInstruction.setText(recipe.getInstructions());
        String imagePath = recipe.getImagePath();
        Log.d("RecipeDetailHomeActivity", "Image path: " + imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this)
                    .load(Uri.fromFile(new File(imagePath)))
                    .placeholder(R.drawable.auto)
                    .error(R.drawable.auto)
                    .into(imageRecipe);
        } else {
            imageRecipe.setImageResource(R.drawable.auto);
        }
}

        private void displayIngredients(List<DetailRecipeIngredient> ingredients) {
        ingredientsContainer.removeAllViews();
        for (DetailRecipeIngredient dri : ingredients) {
            TextView tv = new TextView(this);
            tv.setText("- " + dri.getIngredientName() + ": " + dri.getAmount());
            tv.setTextSize(16);
            ingredientsContainer.addView(tv);
        }
    }

    private void submitRating() {
        int ratingScore = (int) ratingBar.getRating();
        String comment = editComment.getText().toString().trim();

        if (ratingScore < 1 || ratingScore > 5) {
            Toast.makeText(this, "Please select a rating from 1 to 5 stars", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = db.insertRating(currentRecipeId, currentUserId, ratingScore, comment);
        if (result != -1) {
            Toast.makeText(this, "Your rating has been submitted!", Toast.LENGTH_SHORT).show();
            commentSection.setVisibility(View.GONE);
            allComments = db.getCommentsByRecipeId(currentRecipeId);
            commentAdapter.updateData(allComments);
            updateRatingSummary();
        } else {
            Toast.makeText(this, "Failed to submit rating!", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupCommentSection() {
        allComments = db.getCommentsByRecipeId(currentRecipeId);
        commentAdapter = new HomeCommentAdapter(allComments, db);
        recyclerComments.setAdapter(commentAdapter);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        updateRatingSummary();
    }

    private void updateRatingSummary() {
        int count = db.getRatingCountByRecipeId(currentRecipeId);
        double average = calculateAverageRating(allComments);
        textRatingCount.setText("(" + count + (count > 1 ? " Ratings)" : " Rating)"));
        ratingAverage.setRating((float) average);
        textRatingSummary.setText(String.format("%.1f out of 5", average));
    }

    private double calculateAverageRating(List<Rating> comments) {
        if (comments.isEmpty()) return 0.0;
        int total = 0;
        for (Rating r : comments) total += r.getRatingScore();
        return (double) total / comments.size();
    }

    private void setupFilterButtons() {
        findViewById(R.id.button_all).setOnClickListener(v -> filterComments(0));
        findViewById(R.id.button_5_star).setOnClickListener(v -> filterComments(5));
        findViewById(R.id.button_4_star).setOnClickListener(v -> filterComments(4));
        findViewById(R.id.button_3_star).setOnClickListener(v -> filterComments(3));
        findViewById(R.id.button_2_star).setOnClickListener(v -> filterComments(2));
        findViewById(R.id.button_1_star).setOnClickListener(v -> filterComments(1));
    }

    private void filterComments(int star) {
        if (star == 0) {
            commentAdapter.updateData(allComments);
        } else {
            List<Rating> filtered = new ArrayList<>();
            for (Rating r : allComments) {
                if ((int) r.getRatingScore() == star) {
                    filtered.add(r);
                }
            }
            commentAdapter.updateData(filtered);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Recipe updatedRecipe = (Recipe) data.getSerializableExtra("UPDATED_RECIPE");
            if (updatedRecipe != null) {
                this.recipe = updatedRecipe;
                displayRecipeInfo(recipe);
                if (recipe.getDetailIngredients() != null) {
                    displayIngredients(recipe.getDetailIngredients());
                }
            }
        }
    }
}
