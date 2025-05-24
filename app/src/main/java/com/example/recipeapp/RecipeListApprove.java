package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
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
    private List<Recipe> fullRecipeList;    // danh sách đầy đủ tất cả recipe
    private List<Recipe> filteredList;      // danh sách đã lọc theo trạng thái
    private Button btnBack;

    // ✅ SỬA 1: Khai báo đối tượng DBHelper
    private KET_NOI_CSDL dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list_approve);
        btnBack = findViewById(R.id.btnBack);
        btnPending = findViewById(R.id.btnPending);
        btnAccepted = findViewById(R.id.btnAccepted);
        btnRejected = findViewById(R.id.btnRejected);
        rvRecipeList = findViewById(R.id.rvRecipeList);

        rvRecipeList.setLayoutManager(new LinearLayoutManager(this));

        // ✅ SỬA 2: Khởi tạo DBHelper
        dbHelper = new KET_NOI_CSDL(this, null, null, 1);


        // ✅ SỬA 3: Lấy dữ liệu từ database
        fullRecipeList = getRecipesFromDatabase();

        // Hiển thị toàn bộ ban đầu
        filteredList = new ArrayList<>(fullRecipeList);

        adapter = new RecipeApproveAdapter(filteredList, new RecipeApproveAdapter.OnRecipeActionListener() {
            @Override
            public void onApprove(Recipe recipe) {
                recipe.setIsApproved(1);
                updateRecipeStatusInDatabase(recipe); // ✅ SỬA 4: cập nhật trạng thái
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onReject(Recipe recipe) {
                recipe.setIsApproved(0); // ✅ Đảm bảo trạng thái bị từ chối
                updateRecipeStatusInDatabase(recipe); // ✅ SỬA 4: cập nhật trạng thái
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onViewDetail(Recipe recipe) {
                Intent intent = new Intent(RecipeListApprove.this, RecipeDetailActivity.class);
                intent.putExtra("recipe_id", recipe.getId());
                startActivity(intent);
            }
        });

        rvRecipeList.setAdapter(adapter);
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnPending.setOnClickListener(v -> filterRecipesByStatus(null));
        btnAccepted.setOnClickListener(v -> filterRecipesByStatus(1));
        btnRejected.setOnClickListener(v -> filterRecipesByStatus(0));
    }

    // ✅ SỬA 5: Lọc recipe theo trạng thái duyệt (null = chờ)
    private void filterRecipesByStatus(Integer status) {
        filteredList.clear();

        for (Recipe recipe : fullRecipeList) {
            if (status == null) {
                if (recipe.getIsApproved() == null) {
                    filteredList.add(recipe);
                }
            } else {
                if (status.equals(recipe.getIsApproved())) {
                    filteredList.add(recipe);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    // ✅ SỬA 6: Thêm phương thức lấy danh sách từ database
    private List<Recipe> getRecipesFromDatabase() {
        return dbHelper.getAllRecipesFull(); // <-- đảm bảo đã có hàm này trong KET_NOI_CSDL
    }

    // ✅ SỬA 7: Thêm phương thức cập nhật trạng thái vào database
    private void updateRecipeStatusInDatabase(Recipe recipe) {
        dbHelper.updateRecipeStatus(recipe); // <-- đảm bảo đã có hàm này trong KET_NOI_CSDL
    }
}
