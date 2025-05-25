package com.example.recipeapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editTime, editInstructions;
    private Spinner spinnerCategory, spinnerOrigin;
    private LinearLayout layoutIngredients;
    private Button btnAddIngredient, btnSave, btnBack;

    private Recipe recipe;
    private KET_NOI_CSDL db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("EditRecipeActivity", "onCreate called");
        setContentView(R.layout.activity_edit_recipe);

        initViews();
        db = new KET_NOI_CSDL(this);

        setupSpinners();

        // Better error logging when retrieving Recipe object from Intent
        Object obj = getIntent().getSerializableExtra("RECIPE_TO_EDIT");
        if (obj == null) {
            Log.e("EditRecipeActivity", "Intent does not contain RECIPE_TO_EDIT");
        } else if (!(obj instanceof Recipe)) {
            Log.e("EditRecipeActivity", "RECIPE_TO_EDIT data is not a Recipe: " + obj.getClass().getName());
        }

        if (obj instanceof Recipe) {
            recipe = (Recipe) obj;
            Log.d("EditRecipeActivity", "Received Recipe: " + recipe.getTitle());
//            SharedPreferences prefs = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
//            int currentUserId = prefs.getInt("USER_ID", -1);
//
//            if (recipe.getUserId() != currentUserId) {
//                Toast.makeText(this, "Bạn không có quyền chỉnh sửa công thức này", Toast.LENGTH_LONG).show();
//                finish(); // Đóng activity nếu không đúng người đăng
//                return;
//            }

            populateFields(recipe);
        } else {
            Toast.makeText(this, "Could not find recipe to edit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnAddIngredient.setOnClickListener(v -> addIngredientRow(null));
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void initViews() {
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editTime = findViewById(R.id.editTime);
        editInstructions = findViewById(R.id.editInstructions);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerOrigin = findViewById(R.id.spinnerOrigin);
        layoutIngredients = findViewById(R.id.layoutIngredients);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupSpinners() {
        List<String> categoryList = db.getAllCategoryNames();
        if (categoryList != null) {
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(categoryAdapter);
        }

        List<String> originList = db.getAllOriginNames();
        if (originList != null) {
            ArrayAdapter<String> originAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, originList);
            originAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerOrigin.setAdapter(originAdapter);
        }
    }

    private void populateFields(Recipe recipe) {
        editTitle.setText(recipe.getTitle());
        editDescription.setText(recipe.getDescription());
        editTime.setText(recipe.getTime());
        editInstructions.setText(recipe.getInstructions());

        // Category
        if (recipe.getCategoryId() != null) {
            String categoryName = db.getCategoryNameById(recipe.getCategoryId());
            if (categoryName != null) {
                int position = ((ArrayAdapter<String>) spinnerCategory.getAdapter()).getPosition(categoryName);
                if (position >= 0) spinnerCategory.setSelection(position);
            } else {
                Log.w("EditRecipeActivity", "Category name not found for ID: " + recipe.getCategoryId());
            }
        } else {
            Log.w("EditRecipeActivity", "Recipe categoryId is null");
        }

        // Origin
        if (recipe.getOrigin() != null) {
            int position = ((ArrayAdapter<String>) spinnerOrigin.getAdapter()).getPosition(recipe.getOrigin());
            if (position >= 0) spinnerOrigin.setSelection(position);
        }

        // Ingredients
        if (recipe.getDetailIngredients() != null) {
            for (DetailRecipeIngredient ingredient : recipe.getDetailIngredients()) {
                if (ingredient != null) {
                    addIngredientRow(ingredient);
                }
            }
        } else {
            Log.w("EditRecipeActivity", "Ingredient list is null");
        }
    }

    private void addIngredientRow(DetailRecipeIngredient detail) {
        View row = LayoutInflater.from(this).inflate(R.layout.row_ingredient, layoutIngredients, false);
        EditText name = row.findViewById(R.id.editIngredientName);
        EditText amount = row.findViewById(R.id.editIngredientAmount);
        Button delete = row.findViewById(R.id.btnDeleteIngredient);

        if (detail != null) {
            name.setText(detail.getIngredientName());
            amount.setText(detail.getAmount());
        }

        delete.setOnClickListener(v -> layoutIngredients.removeView(row));
        layoutIngredients.addView(row);
    }

    private void saveChanges() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String time = editTime.getText().toString().trim();
        String instructions = editInstructions.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy tên category từ spinner
        String selectedCategoryName = spinnerCategory.getSelectedItem().toString();
        Integer categoryId = db.getCategoryIdByName(selectedCategoryName);
        if (categoryId == null) {
            Toast.makeText(this, "Selected category not found: " + selectedCategoryName, Toast.LENGTH_SHORT).show();
            return;
        }

        // Gán categoryId cho recipe
        recipe.setCategoryId(categoryId);

        // Gán type cho recipe (lấy tên type, có thể chính là selectedCategoryName hoặc spinner khác nếu có)
        // Nếu bạn có spinner riêng cho type thì lấy từ đó, còn không thì dùng categoryName luôn
        recipe.setType(selectedCategoryName);

        // Lấy origin từ spinner
        String origin = spinnerOrigin.getSelectedItem() != null ? spinnerOrigin.getSelectedItem().toString() : "";
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please select an origin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật các trường khác
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setTime(time);
        recipe.setInstructions(instructions);
        recipe.setOrigin(origin);

        // Thu thập danh sách nguyên liệu
        ArrayList<DetailRecipeIngredient> updatedIngredients = new ArrayList<>();
        for (int i = 0; i < layoutIngredients.getChildCount(); i++) {
            View row = layoutIngredients.getChildAt(i);
            EditText name = row.findViewById(R.id.editIngredientName);
            EditText amount = row.findViewById(R.id.editIngredientAmount);

            String ingName = name.getText().toString().trim();
            String ingAmount = amount.getText().toString().trim();

            if (!ingName.isEmpty() && !ingAmount.isEmpty()) {
                int ingId = db.getIngredientIdByName(ingName);
                if (ingId == -1) {
                    ingId = db.insertIngredientIfNotExists(ingName);
                }
                updatedIngredients.add(new DetailRecipeIngredient(ingId, recipe.getId(), ingName, ingAmount));
            }
        }

        if (updatedIngredients.isEmpty()) {
            Toast.makeText(this, "Please add at least one ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật database
        boolean success = db.updateRecipeAndIngredients(recipe, updatedIngredients);
        if (success) {
            Toast.makeText(this, "Recipe updated successfully", Toast.LENGTH_SHORT).show();

            recipe.setDetailIngredients(updatedIngredients);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("UPDATED_RECIPE", recipe);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}
