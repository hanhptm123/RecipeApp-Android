package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView imageRecipe;
    private TextView textTitle, textTime, textType, textOrigin, textDate, textUser, textInstruction, textDescription;
    private LinearLayout ingredientsContainer;
    private Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        KET_NOI_CSDL db = new KET_NOI_CSDL(this);
        List<DetailRecipeIngredient> details = db.layDanhSachNguyenLieu(1);
        for (DetailRecipeIngredient detail : details) {
            Log.d("INGREDIENT_LOG", detail.getIngredientName() + " - " + detail.getAmount());
        }


        // Ánh xạ view
        imageRecipe = findViewById(R.id.image_recipe);
        textTitle = findViewById(R.id.text_title);
        textTime = findViewById(R.id.text_time);
        textType = findViewById(R.id.text_type);
        textOrigin = findViewById(R.id.text_origin);
        textDate = findViewById(R.id.text_date);
        textUser = findViewById(R.id.text_user);
        textInstruction = findViewById(R.id.text_instruction);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        textDescription = findViewById(R.id.text_description);

        btnGoBack = findViewById(R.id.btn_go_back);

        // Lấy dữ liệu từ Intent (giả sử bạn truyền đối tượng Recipe qua Intent)
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        ArrayList<DetailRecipeIngredient> detailIngredients =
                (ArrayList<DetailRecipeIngredient>) getIntent().getSerializableExtra("ingredients");

        if (recipe != null) {
            // Hiển thị dữ liệu công thức
            textTitle.setText(recipe.getTitle());
            textTime.setText("Thời gian: " + recipe.getTime());
            textType.setText("Loại: " + recipe.getType());
            textOrigin.setText("Xuất xứ: " + recipe.getOrigin());
            textDate.setText("Ngày đăng: " + recipe.getDate());
            textUser.setText("Người đăng: " + recipe.getUser());
            textDescription.setText(recipe.getDescription());
            textInstruction.setText(recipe.getInstructions());

            // Nếu có ảnh từ path thì load (tạm dùng ảnh mặc định)
            imageRecipe.setImageResource(R.drawable.chebamau);
        }

        // Hiển thị danh sách nguyên liệu
        if (detailIngredients != null && !detailIngredients.isEmpty()) {
            for (DetailRecipeIngredient dri : detailIngredients) {
                TextView tv = new TextView(this);
                String ingredientLine = "- " + dri.getIngredientName() + ": " + dri.getAmount();
                tv.setText(ingredientLine);
                tv.setTextSize(16);
                ingredientsContainer.addView(tv);
            }
        }

        // Xử lý nút quay lại
        btnGoBack.setOnClickListener(v -> finish());
    }
}
