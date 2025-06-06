package com.example.recipeapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView imageRecipe;
    private Uri imageUri;

    private Recipe recipe;
    private KET_NOI_CSDL db;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        initViews();
        db = new KET_NOI_CSDL(this);

        setupSpinners();

        // Get the recipe from the intent
        Object obj = getIntent().getSerializableExtra("RECIPE_TO_EDIT");
        if (obj instanceof Recipe) {
            recipe = (Recipe) obj;
            populateFields(recipe);
        } else {
            Toast.makeText(this, "No recipe found to edit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnChooseFile = findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(v -> openImageChooser());

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
        imageRecipe = findViewById(R.id.imageRecipe);
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

        if (recipe.getImagePath() != null && !recipe.getImagePath().isEmpty()) {
            imageUri = Uri.parse(recipe.getImagePath());
            imageRecipe.setImageURI(imageUri);
        }

        // Select category in spinner
        if (recipe.getCategoryId() != null) {
            String categoryName = db.getCategoryNameById(recipe.getCategoryId());
            if (categoryName != null) {
                int position = ((ArrayAdapter<String>) spinnerCategory.getAdapter()).getPosition(categoryName);
                if (position >= 0) spinnerCategory.setSelection(position);
            }
        }

        // Select origin in spinner
        if (recipe.getOrigin() != null) {
            int position = ((ArrayAdapter<String>) spinnerOrigin.getAdapter()).getPosition(recipe.getOrigin());
            if (position >= 0) spinnerOrigin.setSelection(position);
        }

        // Add ingredients to layout
        if (recipe.getDetailIngredients() != null) {
            for (DetailRecipeIngredient detail : recipe.getDetailIngredients()) {
                addIngredientRow(detail);
            }
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

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageRecipe.setImageURI(imageUri);
        }
    }

    private void saveChanges() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String time = editTime.getText().toString().trim();
        String instructions = editInstructions.getText().toString().trim();

//        if (title.isEmpty() || description.isEmpty() || time.isEmpty()) {
//            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (title.isEmpty()) {
            editTitle.setError("Please enter a title");
            return;
        }
        if (description.isEmpty()) {
            editDescription.setError("Please enter a description");
            return;
        }
        if (time.isEmpty()) {
            editTime.setError("Please enter cooking time");
            return;
        }
        if (instructions.isEmpty()) {
            editInstructions.setError("Please enter instructions");
            return;
        }
        // Get category
        String selectedCategoryName = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "";
        Integer categoryId = db.getCategoryIdByName(selectedCategoryName);
        if (categoryId == null) {
            Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get origin
        String origin = spinnerOrigin.getSelectedItem() != null ? spinnerOrigin.getSelectedItem().toString() : "";
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please select origin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update recipe object
        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setTime(time);
        recipe.setInstructions(instructions);
        recipe.setCategoryId(categoryId);
        recipe.setType(selectedCategoryName);
        recipe.setOrigin(origin);

        if (imageUri != null) {
            recipe.setImagePath(imageUri.toString());
        }

        // Collect ingredients
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

        // Update in database
        recipe.setIsApproved(null);
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
