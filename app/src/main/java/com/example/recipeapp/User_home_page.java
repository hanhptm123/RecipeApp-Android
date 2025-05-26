package com.example.recipeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class User_home_page extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_home_page);
        ImageButton btnlogout = (ImageButton)findViewById(R.id.userbtnLogout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(User_home_page.this)
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Xóa session SharedPreferences
                            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

                            // Chuyển về màn hình đăng nhập
                            Intent intent = new Intent(User_home_page.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", null) // Không làm gì nếu chọn "No"
                        .show();
            }
        });
        ImageButton btnhome = (ImageButton) findViewById(R.id.userBtnHome);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(User_home_page.this, User_page.class);
                startActivity(it);
                finish();
            }
        });
        ImageButton btn_my_profile = (ImageButton) findViewById(R.id.userProfileIcon);
        btn_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(User_home_page.this, User_profile.class);
                startActivity(it);
            }
        });
        ImageButton btn_add_recipe = (ImageButton) findViewById(R.id.createRecipeIcon);
        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(User_home_page.this, AddRecipeActivity.class);
                startActivity(it);
            }
        });
        ImageButton btnManage = findViewById(R.id.btn_manage_my_recipes);
        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int userId = prefs.getInt("UserID", -1); // Lấy userId từ SharedPreferences

                Intent intent = new Intent(User_home_page.this, MyRecipesActivity.class);
                intent.putExtra("userId", userId); // Sử dụng biến đúng đã lấy ở trên
                startActivity(intent);
            }
        });
        ImageButton btnTopFavourite = findViewById(R.id.topFavouriteIcon);

        btnTopFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_home_page.this, TopFavouriteRecipesActivity.class);
                startActivity(intent);
            }
        });

    }
}