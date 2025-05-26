package com.example.recipeapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class searchRecipesByIngredient  extends AppCompatActivity {

    private EditText etIngredients;
    private Button btnSearch;
    private RecyclerView rvRecipes;

    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private KET_NOI_CSDL dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_ingredient);  // đổi tên nếu bạn dùng khác

        // Ánh xạ view
        etIngredients = findViewById(R.id.etIngredients);
        btnSearch = findViewById(R.id.btnSearch);
        rvRecipes = findViewById(R.id.rvRecipes);

        // Khởi tạo database helper
        dbHelper = new KET_NOI_CSDL(this);

        // Setup RecyclerView
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this, recipeList, dbHelper);
        rvRecipes.setAdapter(adapter);

        // Xử lý nút search
        btnSearch.setOnClickListener(v -> {
            String keyword = etIngredients.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Enter ingredients separated by commas, e.g., egg, milk, sugar", Toast.LENGTH_LONG).show();
                return;
            }
            searchRecipes(keyword);
        });
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

    }

    private void searchRecipes(String keyword) {
        // Nếu bạn muốn tìm nhiều nguyên liệu, có thể tách bằng dấu phẩy rồi tìm theo từng phần
        // Hiện tại mình search theo 1 từ khóa

        // Xóa dữ liệu cũ trước khi thêm mới
        recipeList.clear();

        ArrayList<Recipe> results = dbHelper.searchRecipesByIngredient(keyword);
        if (results.isEmpty()) {
            Toast.makeText(this, "No recipes found for: " + keyword, Toast.LENGTH_SHORT).show();
        } else {
            recipeList.addAll(results);
        }
        adapter.notifyDataSetChanged();
    }
}