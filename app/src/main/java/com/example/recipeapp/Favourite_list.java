package com.example.recipeapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Favourite_list extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    RecyclerView recyclerViewFavourite;
    FavouriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favourite_list);
        ImageButton btnBack = findViewById(R.id.btnBack_favourite);
        btnBack.setOnClickListener(v -> finish());

        recyclerViewFavourite = findViewById(R.id.recyclerViewFavourite);
        recyclerViewFavourite.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new KET_NOI_CSDL(this, "RecipeDB.db", null, 2);

        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);

        Log.d("APP_DEBUG", "Current logged in UserID: " + userId);

        if(userId != -1){
            ArrayList<Recipe> favouriteRecipes = dbHelper.getFavouriteRecipesByUserId(userId);
            Log.d("APP_DEBUG", "Favourite recipes count: " + (favouriteRecipes != null ? favouriteRecipes.size() : 0));

            if(favouriteRecipes != null && !favouriteRecipes.isEmpty()){
                adapter = new FavouriteAdapter(this, favouriteRecipes);
                recyclerViewFavourite.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No favourite recipes found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
