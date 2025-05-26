package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
public class MyRecipesActivity extends AppCompatActivity {

    private MyRecipesFragment fragment;
    private KET_NOI_CSDL db;
    private int userId;
    private ArrayList<Recipe> userRecipes = new ArrayList<>();

    private Button btnPending, btnAccepted, btnRejected, btnGoBack;

    private Integer currentFilterStatus = null; // để lưu trạng thái lọc hiện tại

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        db = new KET_NOI_CSDL(this);

        userId = getIntent().getIntExtra("userId", -1);

        userRecipes = db.getRecipesByUserId(userId);

        fragment = new MyRecipesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        btnPending = findViewById(R.id.btnPending);
        btnAccepted = findViewById(R.id.btnAccepted);
        btnRejected = findViewById(R.id.btnRejected);
        btnGoBack = findViewById(R.id.btn_go_back);

        btnGoBack.setOnClickListener(v -> finish());

        btnPending.setOnClickListener(v -> {
            currentFilterStatus = null;
            filterRecipesByStatus(currentFilterStatus);
            setSelectedButton(btnPending);
        });

        btnAccepted.setOnClickListener(v -> {
            currentFilterStatus = 1;
            filterRecipesByStatus(currentFilterStatus);
            setSelectedButton(btnAccepted);
        });

        btnRejected.setOnClickListener(v -> {
            currentFilterStatus = 0;
            filterRecipesByStatus(currentFilterStatus);
            setSelectedButton(btnRejected);
        });

        // Mặc định hiển thị các công thức đang chờ duyệt (isApproved == null)
        currentFilterStatus = null;
        filterRecipesByStatus(currentFilterStatus);
        setSelectedButton(btnPending);
    }

    private void filterRecipesByStatus(Integer status) {
        ArrayList<Recipe> filteredList = new ArrayList<>();
        for (Recipe recipe : userRecipes) {
            if (status == null && recipe.getIsApproved() == null) {
                filteredList.add(recipe);
            } else if (status != null && status.equals(recipe.getIsApproved())) {
                filteredList.add(recipe);
            }
        }
        if (fragment != null) {
            fragment.updateRecipeList(filteredList);
        }
    }

    private void setSelectedButton(Button selectedButton) {
        int defaultColor = getResources().getColor(R.color.default_button_color);
        int defaultTextColor = getResources().getColor(R.color.black);
        int selectedColor = getResources().getColor(R.color.selected_button_color);
        int selectedTextColor = getResources().getColor(R.color.white);

        btnPending.setBackgroundColor(defaultColor);
        btnAccepted.setBackgroundColor(defaultColor);
        btnRejected.setBackgroundColor(defaultColor);

        btnPending.setTextColor(defaultTextColor);
        btnAccepted.setTextColor(defaultTextColor);
        btnRejected.setTextColor(defaultTextColor);

        selectedButton.setBackgroundColor(selectedColor);
        selectedButton.setTextColor(selectedTextColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            // Cập nhật danh sách từ DB
            userRecipes.clear();
            userRecipes.addAll(db.getRecipesByUserId(userId));

            // Gọi lọc theo trạng thái hiện tại để cập nhật giao diện
            filterRecipesByStatus(currentFilterStatus);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách công thức từ DB
        userRecipes.clear();
        userRecipes.addAll(db.getRecipesByUserId(userId));

        // Lọc lại theo trạng thái đang chọn
        filterRecipesByStatus(currentFilterStatus);
    }
}
