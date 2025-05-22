package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;

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
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        ImageButton btnSearch = findViewById(R.id.btnsearch_user);

        btnSearch.setOnClickListener(v -> {
            String keyword = editTextSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadRecipes(); // load tất cả nếu không nhập gì
            } else {
                loadRecipesByKeyword(keyword); // load theo từ khóa tìm kiếm
            }
        });
        ImageButton btnMenu = findViewById(R.id.btnmenu); // hoặc btnmenu_user nếu ở layout user

        btnMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.search_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_search_by_category) {
                    startActivity(new Intent(MainActivity.this, SearchByCategoryActivity.class));
                    return true;
                } else if (id == R.id.menu_search_by_ingredient) {
                    startActivity(new Intent(MainActivity.this, SearchByIngredientActivity.class));
                    return true;
                }
                return false;
            });

            popupMenu.show();
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
    private void loadRecipesByKeyword(String keyword) {
        View viewRecipes = getLayoutInflater().inflate(R.layout.layout_fragment_recipes, null);
        RecyclerView rv = viewRecipes.findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Recipe> recipes = dbHelper.searchRecipesByName(keyword);
        RecipeAdapter adapter = new RecipeAdapter(this, recipes);
        rv.setAdapter(adapter);

        FrameLayout content = findViewById(R.id.content);
        content.removeAllViews();
        content.addView(viewRecipes);
    }

    // Gọi lại loadRecipes khi quay về từ AddActivity
    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(); // Load lại dữ liệu khi Activity resume
    }
}
