package com.example.recipeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddRecipeActivity extends AppCompatActivity {

    private EditText editTitle, editTime, editIngredientName, editIngredientAmount;
    private Spinner spinnerType, spinnerOrigin;
    private Button btnSave;
    private KET_NOI_CSDL dbHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private Button btnChooseFile;
    private LinearLayout layoutIngredients;
    private Button btnAddIngredient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        layoutIngredients = findViewById(R.id.layoutIngredients);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        btnAddIngredient.setOnClickListener(v -> addIngredientRow());

        dbHelper = new KET_NOI_CSDL(this, null, null, 1);

        editTitle = findViewById(R.id.editTitle);
        editTime = findViewById(R.id.editTime);
        spinnerType = findViewById(R.id.spinnerCategory);  // dùng spinnerCategory làm type
        spinnerOrigin = findViewById(R.id.spinnerOrigin);
        editIngredientName = findViewById(R.id.editIngredientName);
        editIngredientAmount = findViewById(R.id.editIngredientAmount);
        btnSave = findViewById(R.id.btnSave);

        setupSpinners();
        btnChooseFile = findViewById(R.id.btnChooseFile);

        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        Button btback = (Button)findViewById(R.id.btnBack);
        btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(AddRecipeActivity.this, User_home_page.class);
                startActivity(it);
            }
        });
        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String time = editTime.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String origin = spinnerOrigin.getSelectedItem().toString();

            if (title.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tiêu đề và thời gian!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPositiveNumber(time)) {
                Toast.makeText(this, "Thời gian phải là số nguyên dương!", Toast.LENGTH_SHORT).show();
                return;
            }

            int cookTime = Integer.parseInt(time);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String user = getLoggedInUserName();
            String imagePath = selectedImageUri != null ? selectedImageUri.toString() : "";
            Recipe recipe = new Recipe(title, time, type, origin, currentDate, user, imagePath, null, null);

            long recipeId = dbHelper.insertRecipe(recipe);
            if (recipeId == -1) {
                Toast.makeText(this, "Thêm công thức thất bại!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔁 Duyệt tất cả nguyên liệu trong layoutIngredients
            int count = layoutIngredients.getChildCount();
            for (int i = 0; i < count; i++) {
                LinearLayout row = (LinearLayout) layoutIngredients.getChildAt(i);
                EditText editName = (EditText) row.getChildAt(0);
                EditText editAmount = (EditText) row.getChildAt(1);

                String ingredientName = editName.getText().toString().trim();
                String ingredientAmount = editAmount.getText().toString().trim();

                if (ingredientName.isEmpty() || ingredientAmount.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ nguyên liệu ở dòng " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }

                Ingredient ingredient = dbHelper.getIngredientByName(ingredientName);
                long ingredientId = (ingredient == null) ?
                        dbHelper.insertIngredient(ingredientName) : ingredient.getIngredientId();

                long detailId = dbHelper.insertDetailRecipeIngredient((int) recipeId, (int) ingredientId, ingredientAmount);
                if (detailId == -1) {
                    Toast.makeText(this, "Lỗi thêm nguyên liệu ở dòng " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(this, "Thêm công thức thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Toast.makeText(this, "Đã chọn ảnh!", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupSpinners() {
        String[] types = {"Món chính", "Tráng miệng", "Đồ uống"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        String[] origins = {"Việt Nam", "Ý", "Nhật", "Mexico"};
        ArrayAdapter<String> adapterOrigin = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, origins);
        adapterOrigin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigin.setAdapter(adapterOrigin);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.contains("user_name"); // thay bằng key bạn lưu user name
    }

    private String getLoggedInUserName() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_name", ""); // trả về chuỗi rỗng nếu chưa đăng nhập
    }

    private void addIngredientRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(3);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, 0, 16);
        row.setLayoutParams(rowParams);

        // Tên nguyên liệu
        EditText editName = new EditText(this);
        editName.setHint("Ingredient Name");
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f);
        editName.setLayoutParams(nameParams);

        // Số lượng
        EditText editAmount = new EditText(this);
        editAmount.setHint("Amount");
        LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        amountParams.setMarginStart(8);
        editAmount.setLayoutParams(amountParams);

        // Nút xóa
        Button btnRemove = new Button(this);
        btnRemove.setText("x");
        btnRemove.setTextColor(Color.WHITE);
        btnRemove.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f);
        btnParams.setMarginStart(8);
        btnRemove.setLayoutParams(btnParams);

        btnRemove.setOnClickListener(v -> layoutIngredients.removeView(row));

        row.addView(editName);
        row.addView(editAmount);
        row.addView(btnRemove);

        layoutIngredients.addView(row);
    }

    private boolean isPositiveNumber(String str) {
        try {
            return Integer.parseInt(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
