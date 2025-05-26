package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecipeListApprove extends AppCompatActivity {

    private Button btnPending, btnAccepted, btnRejected;
    private RecyclerView rvRecipeList;
    private RecipeApproveAdapter adapter;
    private List<Recipe> fullRecipeList;
    private List<Recipe> filteredList;
    private Button btnBack;

    private KET_NOI_CSDL dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list_approve);

        // Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        btnPending = findViewById(R.id.btnPending);
        btnAccepted = findViewById(R.id.btnAccepted);
        btnRejected = findViewById(R.id.btnRejected);
        rvRecipeList = findViewById(R.id.rvRecipeList);

        rvRecipeList.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new KET_NOI_CSDL(this, null, null, 1);

        fullRecipeList = getRecipesFromDatabase();
        filteredList = new ArrayList<>();

        adapter = new RecipeApproveAdapter(filteredList, this, dbHelper);
        rvRecipeList.setAdapter(adapter);

        // Mặc định lọc danh sách chưa duyệt (Pending)
        filterRecipesByStatus(null);
        setSelectedButton(btnPending);

        btnBack.setOnClickListener(v -> finish());

        btnPending.setOnClickListener(v -> {
            filterRecipesByStatus(null);
            setSelectedButton(btnPending);
        });

        btnAccepted.setOnClickListener(v -> {
            filterRecipesByStatus(1);
            setSelectedButton(btnAccepted);
        });

        btnRejected.setOnClickListener(v -> {
            filterRecipesByStatus(0);
            setSelectedButton(btnRejected);
        });
    }

    private void filterRecipesByStatus(Integer status) {
        filteredList.clear();
        for (Recipe recipe : fullRecipeList) {
            // Log giá trị isApproved để kiểm tra
            Log.d("DEBUG_APPROVAL", "Recipe ID: " + recipe.getId() + " | isApproved: " + recipe.getIsApproved());

            if (status == null && recipe.getIsApproved() == null) {
                filteredList.add(recipe);
            } else if (status != null && status.equals(recipe.getIsApproved())) {
                filteredList.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private List<Recipe> getRecipesFromDatabase() {
        return dbHelper.getAllRecipesFull();
    }

    private void setSelectedButton(Button selectedButton) {
        // Reset trạng thái tất cả nút
        btnPending.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        btnAccepted.setBackgroundColor(getResources().getColor(R.color.default_button_color));
        btnRejected.setBackgroundColor(getResources().getColor(R.color.default_button_color));

        btnPending.setTextColor(getResources().getColor(R.color.black));
        btnAccepted.setTextColor(getResources().getColor(R.color.black));
        btnRejected.setTextColor(getResources().getColor(R.color.black));

        // Nút được chọn
        selectedButton.setBackgroundColor(getResources().getColor(R.color.selected_button_color));
        selectedButton.setTextColor(getResources().getColor(R.color.white));
    }
}
