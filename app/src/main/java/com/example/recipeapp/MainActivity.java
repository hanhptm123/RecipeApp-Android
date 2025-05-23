package com.example.recipeapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

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


        dbHelper = new KET_NOI_CSDL(this, "RecipeDB.db", null, 2);
        themNguyenLieuMau();

        loadRecipes();

        ImageButton btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(v -> {
            Intent it = new Intent(MainActivity.this, Login.class);
            startActivity(it);
        });
    }

    // Thêm dữ liệu mẫu nếu chưa có
    private void themNguyenLieuMau() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("INSERT OR IGNORE INTO DetailRecipeIngredient (RecipeID, IngredientID, Amount) VALUES (?, ?, ?)",
                    new Object[]{1, "Đường", "2 muỗng"});
            Toast.makeText(this, "Thêm nguyên liệu mẫu nếu chưa có", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi thêm nguyên liệu mẫu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Load danh sách món ăn và nguyên liệu tương ứng
    private void loadRecipes() {
        View viewRecipes = getLayoutInflater().inflate(R.layout.layout_fragment_recipes, null);
        RecyclerView rv = viewRecipes.findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Recipe> recipes = dbHelper.getAllRecipes();
        for (Recipe r : recipes) {
            ArrayList<DetailRecipeIngredient> ingredients = dbHelper.getIngredientsByRecipeId(r.getId());
            r.setDetailIngredients(ingredients);
        }

        RecipeAdapter adapter = new RecipeAdapter(this, recipes, dbHelper);
        rv.setAdapter(adapter);

        FrameLayout content = findViewById(R.id.content);
        content.removeAllViews();
        content.addView(viewRecipes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(); // Reload data when activity resumes
    }
}
