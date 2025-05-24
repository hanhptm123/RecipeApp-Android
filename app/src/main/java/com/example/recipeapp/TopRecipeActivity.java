package com.example.recipeapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopRecipeActivity extends AppCompatActivity {

    private RecyclerView rvTopRecipes;
    private RecipeAdapter recipeAdapter;
    private KET_NOI_CSDL dbHelper;

    private Spinner spinnerMonth;
    private EditText etYear;
    private Button btnFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_recipes);

        // Ánh xạ view
        rvTopRecipes = findViewById(R.id.rvTopRecipes);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etYear = findViewById(R.id.etYear);
        btnFilter = findViewById(R.id.btnFilter);
        rvTopRecipes.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new KET_NOI_CSDL(this);

        // Khởi tạo mặc định: lấy top tháng 5 năm 2025
        loadTopRecipes(5, 2025);

        // Thiết lập sự kiện cho nút Filter
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lấy tháng từ spinner (chỉ số bắt đầu từ 0 nên cộng 1)
                int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;

                // Lấy năm từ EditText
                String yearStr = etYear.getText().toString().trim();

                if (TextUtils.isEmpty(yearStr)) {
                    Toast.makeText(TopRecipeActivity.this, "Please enter a year", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedYear;
                try {
                    selectedYear = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(TopRecipeActivity.this, "Invalid year format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Load dữ liệu theo tháng, năm đã chọn
                loadTopRecipes(selectedMonth, selectedYear);
            }
        });
    }

    private void loadTopRecipes(int month, int year) {
        List<Recipe> topRecipes = dbHelper.getTopRecipes(month, year);

        if (recipeAdapter == null) {
            recipeAdapter = new RecipeAdapter(this, topRecipes, dbHelper);
            rvTopRecipes.setAdapter(recipeAdapter);
        } else {
            recipeAdapter.updateData(topRecipes); // Giả sử bạn có method updateData trong adapter để cập nhật lại danh sách
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
