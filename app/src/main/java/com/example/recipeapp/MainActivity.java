package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        dbHelper = new KET_NOI_CSDL(this, "RecipeDB.db", null, 1);

        // Hiển thị layout chứa tiêu đề + danh sách món ăn
        loadRecipes();

        // Nút login
        ImageButton btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(v -> {
            Intent it = new Intent(MainActivity.this, Login.class);
            startActivity(it);
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

        FrameLayout content = findViewById(R.id.content);
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
