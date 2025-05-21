package com.example.recipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class User_page extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user);

        dbHelper = new KET_NOI_CSDL(this, null, null, 1);
        ImageButton btnprofile = findViewById(R.id.btnprofile_user);

        // Lấy UserID từ SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);

        if (userId != -1) {
            Cursor cursor = dbHelper.Doc_bang("SELECT Avatar FROM Users WHERE UserID = " + userId);
            String avatarName = null;
            if (cursor.moveToFirst()) {
                avatarName = cursor.getString(cursor.getColumnIndex("Avatar"));
            }
            cursor.close();

            if (avatarName == null || avatarName.equals("profile.png")) {
                btnprofile.setImageResource(R.drawable.profile); // ảnh mặc định
            } else {
                Glide.with(this)
                        .load(avatarName)  // avatarName có thể là URL hoặc đường dẫn file
                        .placeholder(R.drawable.profile)  // ảnh tạm khi đang load
                        .error(R.drawable.profile)
                        .circleCrop()// ảnh lỗi
                        .into(btnprofile);


            }

        }
        loadRecipes();
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(User_page.this, User_home_page.class);
                startActivity(it);
            }
        });

    }
    // TÁCH HÀM loadRecipes() ra để dùng chung
    private void loadRecipes() {
        View viewRecipes = getLayoutInflater().inflate(R.layout.layout_fragment_recipes, null);
        RecyclerView rv = viewRecipes.findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Recipe> recipes = dbHelper.getAllRecipes();
        RecipeAdapter adapter = new RecipeAdapter(this, recipes);
        rv.setAdapter(adapter);

        FrameLayout content = findViewById(R.id.content_user);
        content.removeAllViews();
        content.addView(viewRecipes);

        // Gắn sự kiện nút Add Recipes

    }

    // Gọi lại loadRecipes khi quay về từ AddActivity
    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(); // Load lại dữ liệu khi Activity resume
    }

}