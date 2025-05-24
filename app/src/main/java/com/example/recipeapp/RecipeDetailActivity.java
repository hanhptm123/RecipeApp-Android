package com.example.recipeapp;


import android.content.Intent;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView imageRecipe;
    private TextView textTitle, textTime, textType, textOrigin, textDate, textUser, textInstruction, textDescription;
    private LinearLayout ingredientsContainer;
    private Button btnGoBack, btnEdit;

    private Recipe recipe;
    private ArrayList<DetailRecipeIngredient> detailIngredients;
    private int currentUserId;
    private Button btnGoBack, btnSubmitRating;
    private RatingBar ratingBar;
    private EditText editComment;

    private TextView textRatingCount;
    private RatingBar ratingAverage;
    private TextView textRatingSummary;

    private ImageView avatarImage;  // ImageView để hiển thị avatar user

    private List<Rating> allComments = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private TextView textCountView; // Thêm biến TextView cho lượt xem


    private KET_NOI_CSDL db;
    private int currentRecipeId = -1;

    private LinearLayout commentSection; // Thêm biến này để ẩn toàn bộ phần comment

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Map views
        db = new KET_NOI_CSDL(this);

        // Ánh xạ view
        imageRecipe = findViewById(R.id.image_recipe);
        textTitle = findViewById(R.id.text_title);
        textTime = findViewById(R.id.text_time);
        textType = findViewById(R.id.text_type);
        textOrigin = findViewById(R.id.text_origin);
        textDate = findViewById(R.id.text_date);
        textUser = findViewById(R.id.text_user);
        textInstruction = findViewById(R.id.text_instruction);
        textDescription = findViewById(R.id.text_description);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        btnGoBack = findViewById(R.id.btn_go_back);
        btnEdit = findViewById(R.id.btnEdit);

        // Get data from Intent
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        detailIngredients = (ArrayList<DetailRecipeIngredient>) getIntent().getSerializableExtra("ingredients");
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        if (recipe != null) {
            displayRecipeInfo(recipe);

            // Set visibility of Edit button
            btnEdit.setVisibility(recipe.getUserId() == currentUserId ? View.VISIBLE : View.GONE);

        btnSubmitRating = findViewById(R.id.rating_btn_submit_comment);
        btnGoBack = findViewById(R.id.btn_go_back);
        ratingBar = findViewById(R.id.rating_bar);
        editComment = findViewById(R.id.rating_edit_comment);
        textCountView = findViewById(R.id.text_count_view); // Ánh xạ lượt xem


        textRatingCount = findViewById(R.id.text_rating_count);
        ratingAverage = findViewById(R.id.rating_average);
        textRatingSummary = findViewById(R.id.rating_summary);

        commentSection = findViewById(R.id.comment_section); // ánh xạ phần comment_section

        avatarImage = findViewById(R.id.rating_avatar_image); // ánh xạ avatar image

        RecyclerView recyclerComments = findViewById(R.id.recycler_comments);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        ArrayList<DetailRecipeIngredient> detailIngredients = (ArrayList<DetailRecipeIngredient>) getIntent().getSerializableExtra("ingredients");

        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);

        // Load avatar user
        if (userId != -1) {
            Cursor cursor = db.Doc_bang("SELECT Avatar FROM Users WHERE UserID = " + userId);
            String avatarName = null;
            if (cursor.moveToFirst()) {
                avatarName = cursor.getString(cursor.getColumnIndex("Avatar"));
            }
            cursor.close();

            if (avatarName == null || avatarName.equals("profile.png")) {
                avatarImage.setImageResource(R.drawable.profile); // ảnh mặc định
            } else {
                Glide.with(this)
                        .load(avatarName)  // avatarName có thể là URL hoặc đường dẫn file
                        .placeholder(R.drawable.profile)  // ảnh tạm khi đang load
                        .error(R.drawable.profile)
                        .circleCrop()
                        .into(avatarImage);
            }
        } else {
            avatarImage.setImageResource(R.drawable.profile); // user chưa login, ảnh mặc định
        }

        if (recipe != null) {
            currentRecipeId = recipe.getRecipeId();

            // Hiển thị dữ liệu công thức
            textTitle.setText(recipe.getTitle());
            textTime.setText("Thời gian: " + recipe.getTime());
            textType.setText("Loại: " + recipe.getType());
            textOrigin.setText("Xuất xứ: " + recipe.getOrigin());
            textDate.setText("Ngày đăng: " + recipe.getDate());
            textUser.setText("Người đăng: " + recipe.getUser());
            textDescription.setText(recipe.getDescription());
            textInstruction.setText(recipe.getInstructions());
            imageRecipe.setImageResource(R.drawable.chebamau);
            textCountView.setText("Lượt xem: " + recipe.getCountView());

            // Hiển thị danh sách nguyên liệu
            if (detailIngredients != null && !detailIngredients.isEmpty()) {
                for (DetailRecipeIngredient dri : detailIngredients) {
                    TextView tv = new TextView(this);
                    String ingredientLine = "- " + dri.getIngredientName() + ": " + dri.getAmount();
                    tv.setText(ingredientLine);
                    tv.setTextSize(16);
                    ingredientsContainer.addView(tv);
                }
            }

            // Kiểm tra quyền submit rating và ẩn toàn bộ phần comment_section nếu đã đánh giá hoặc là chủ bài
            boolean hasRatedOrCommented = db.hasUserRated(currentRecipeId, userId) || db.hasUserCommented(currentRecipeId, userId);
            boolean isRecipeOwner = (userId == recipe.getUserId());

            if (isRecipeOwner || hasRatedOrCommented) {
                commentSection.setVisibility(View.GONE);
            } else {
                commentSection.setVisibility(View.VISIBLE);
                btnSubmitRating.setOnClickListener(v -> submitRating(currentRecipeId));
            }

            // Lấy toàn bộ comments cho recipe và hiển thị
            allComments = db.getCommentsByRecipeId(currentRecipeId);

            // Setup RecyclerView và adapter
            commentAdapter = new CommentAdapter(allComments, db);
            recyclerComments.setAdapter(commentAdapter);
            recyclerComments.setLayoutManager(new LinearLayoutManager(this));

            // Cập nhật thông tin rating và số lượng rating
            updateRatingSummary();

            // Nút quay lại
            btnGoBack.setOnClickListener(v -> {
                finish();
            });
            // Filter buttons sự kiện
            findViewById(R.id.button_all).setOnClickListener(v -> filterComments(0));
            findViewById(R.id.button_5_star).setOnClickListener(v -> filterComments(5));
            findViewById(R.id.button_4_star).setOnClickListener(v -> filterComments(4));
            findViewById(R.id.button_3_star).setOnClickListener(v -> filterComments(3));
            findViewById(R.id.button_2_star).setOnClickListener(v -> filterComments(2));
            findViewById(R.id.button_1_star).setOnClickListener(v -> filterComments(1));
        }
    }

        if (detailIngredients != null && !detailIngredients.isEmpty()) {
            displayIngredients(detailIngredients);
            recipe.setDetailIngredients(detailIngredients);
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

    private void submitRating(int recipeId) {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);
        if (userId == -1) {
            Log.e("RatingError", "User not logged in.");
            return; // User not logged in
        }
        int ratingScore = (int) ratingBar.getRating();
        String comment = editComment.getText().toString();

        if (ratingScore < 1 || ratingScore > 5) {
            Log.e("RatingError", "Rating must be between 1 and 5.");
            return;
        }

        long result = db.insertRating(recipeId, userId, ratingScore, comment);
        if (result != -1) {
            Log.d("RatingSuccess", "Rating submitted successfully.");
            // Hiển thị thông báo thành công
            Toast.makeText(this, "Comment submitted successfully!", Toast.LENGTH_SHORT).show();
            // Ẩn toàn bộ phần comment_section sau khi submit
            commentSection.setVisibility(View.GONE);

            // Load lại comment và cập nhật hiển thị
            allComments = db.getCommentsByRecipeId(currentRecipeId);
            commentAdapter.updateData(allComments);

            // Cập nhật lại điểm trung bình và số lượng rating
            updateRatingSummary();
        } else {
            Log.e("RatingError", "Failed to submit rating.");
            Toast.makeText(this, "Failed to submit comment. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
        // Handle Edit button click
        btnEdit.setOnClickListener(v -> {
            Log.d("DEBUG_EDIT", "Edit button clicked");
            if (recipe == null) {
                Toast.makeText(this, "Recipe is null", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(RecipeDetailActivity.this, EditRecipeActivity.class);
            intent.putExtra("RECIPE_TO_EDIT", recipe);
            startActivityForResult(intent, 1001);
        });

        // Handle Go Back button
        btnGoBack.setOnClickListener(v -> finish());
    private void updateRatingSummary() {
        int ratingCount = db.getRatingCountByRecipeId(currentRecipeId);
        double averageRating = calculateAverageRating(allComments);

        // Cập nhật số lượng rating
        textRatingCount.setText("(" + ratingCount + (ratingCount > 1 ? " Ratings)" : " Rating)"));

        // Cập nhật điểm trung bình lên RatingBar
        ratingAverage.setRating((float) averageRating);

        // Cập nhật text summary kiểu "3 out of 5"
        String avgRatingText = String.format("%.1f out of 5", averageRating);
        textRatingSummary.setText(avgRatingText);
    }

    private double calculateAverageRating(List<Rating> comments) {
        if (comments.isEmpty()) return 0.0;
        int totalScore = 0;
        for (Rating rating : comments) {
            totalScore += rating.getRatingScore();
        }
        return (double) totalScore / comments.size();
    }

    private void displayRecipeInfo(Recipe recipe) {
        textTitle.setText(recipe.getTitle());
        textTime.setText("Time: " + recipe.getTime());
        textType.setText("Type: " + recipe.getType());
        textOrigin.setText("Origin: " + recipe.getOrigin());
        textDate.setText("Posted on: " + recipe.getDate());
        textUser.setText("Posted by: " + recipe.getUserId());
        textDescription.setText(recipe.getDescription());
        textInstruction.setText(recipe.getInstructions());

        // Temporary placeholder image
        imageRecipe.setImageResource(R.drawable.chebamau);
    }

    private void displayIngredients(ArrayList<DetailRecipeIngredient> ingredients) {
        ingredientsContainer.removeAllViews();
        for (DetailRecipeIngredient dri : ingredients) {
            TextView tv = new TextView(this);
            tv.setText("- " + dri.getIngredientName() + ": " + dri.getAmount());
            tv.setTextSize(16);
            ingredientsContainer.addView(tv);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null) {
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
}
