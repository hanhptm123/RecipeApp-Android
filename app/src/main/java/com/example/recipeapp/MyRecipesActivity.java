package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MyRecipesActivity extends AppCompatActivity {
    private MyRecipesFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        // Nhận userId từ Intent
        int userId = getIntent().getIntExtra("userId", -1);

        // Truyền userId vào Fragment thông qua Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);

        fragment = new MyRecipesFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Xử lý nút Go Back
        Button btnGoBack = findViewById(R.id.btn_go_back);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình trước đó
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            if (fragment != null) {
                fragment.loadRecipeList();  // ✅ Gọi từ fragment
            }
            }
    }
}
