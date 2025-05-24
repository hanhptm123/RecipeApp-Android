package com.example.recipeapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.Category;  // chắc bạn đã có
import com.example.recipeapp.KET_NOI_CSDL;
import com.example.recipeapp.R;
import com.example.recipeapp.Recipe;
import com.example.recipeapp.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchByCategoryActivity extends AppCompatActivity {
    Spinner spinnerType;
    Button btnSearch, btnBack;
    RecyclerView rvResults;
    KET_NOI_CSDL db;
    List<Recipe> results;

    private void setupSpinnerType() {
        String[] types = {"Món chính", "Tráng miệng", "Đồ uống"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_category);

        spinnerType = findViewById(R.id.spinnerType); // Đảm bảo ID là spinnerType trong XML
        btnSearch = findViewById(R.id.btnSearchCategory);
        rvResults = findViewById(R.id.rvResults);
        btnBack = findViewById(R.id.btnBack);  // <-- Thêm dòng này


        setupSpinnerType();

        db = new KET_NOI_CSDL(this, "RecipeDB.db", null, 1);

        btnSearch.setOnClickListener(v -> {
            String selectedType = spinnerType.getSelectedItem().toString();
            results = db.searchRecipesByType(selectedType); // Hàm này chỉ lọc theo type

            rvResults.setLayoutManager(new LinearLayoutManager(this));
            RecipeAdapter adapter = new RecipeAdapter(this, results,db);
            rvResults.setAdapter(adapter);
        });
        btnBack.setOnClickListener(v -> {
            // Quay về activity trước
            finish();
        });
    }
}

