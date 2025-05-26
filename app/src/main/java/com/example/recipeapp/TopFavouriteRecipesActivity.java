package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TopFavouriteRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TopFavouriteAdapter adapter;
    private ArrayList<Recipe> topFavouriteList;
    private ArrayList<User> userList;
    private KET_NOI_CSDL database;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_favourite_recipes);

        // Khởi tạo dữ liệu
        currentUserId = getIntent().getIntExtra("currentUserId", -1);
        database = new KET_NOI_CSDL(this);
        topFavouriteList = database.getTopFavouriteRecipes();
        userList = database.getAllUsers(); // Bạn cần đảm bảo hàm này có trong KET_NOI_CSDL

        recyclerView = findViewById(R.id.recyclerTopFavourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TopFavouriteAdapter(this, topFavouriteList, database, currentUserId);
        recyclerView.setAdapter(adapter);

        ImageButton btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(v -> {
            onBackPressed(); // Quay lại màn hình trước
        });

        // Log để debug
        Log.d("TopFavourite", "Số lượng công thức: " + topFavouriteList.size());
        for (Recipe recipe : topFavouriteList) {
            Log.d("TopFavourite", "Tên: " + recipe.getTitle() + ", Mô tả: " + recipe.getDescription());
        }
    }
}
