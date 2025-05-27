package com.example.recipeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddRecipeActivity extends AppCompatActivity {
    private String savedImagePath = "";

    private EditText editTitle, editTime, editInstructions, editDescription, editIngredientName, editIngredientAmount;
    private Spinner spinnerType, spinnerOrigin;
    private Button btnSave;
    private ImageView imageView;

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

        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        final int userId = sharedPref.getInt("UserID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not identified!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        layoutIngredients = findViewById(R.id.layoutIngredients);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        imageView = findViewById(R.id.imageViewRecipe);

        btnAddIngredient.setOnClickListener(v -> addIngredientRow());

        dbHelper = new KET_NOI_CSDL(this);

        editInstructions = findViewById(R.id.editInstructions);
        editTitle = findViewById(R.id.editTitle);
        editTime = findViewById(R.id.editTime);
        spinnerType = findViewById(R.id.spinnerCategory);
        spinnerOrigin = findViewById(R.id.spinnerOrigin);
        editIngredientName = findViewById(R.id.editIngredientName);
        editIngredientAmount = findViewById(R.id.editIngredientAmount);
        editDescription = findViewById(R.id.editDescription);
        btnSave = findViewById(R.id.btnSave);

        setupSpinners();
        btnChooseFile = findViewById(R.id.btnChooseFile);

        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        Button btback = findViewById(R.id.btnBack);
        btback.setOnClickListener(v -> {
            Intent it = new Intent(AddRecipeActivity.this, User_home_page.class);
            startActivity(it);
        });

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String time = editTime.getText().toString().trim();
            String type = spinnerType.getSelectedItem().toString();
            String origin = spinnerOrigin.getSelectedItem().toString();
            String description = editDescription.getText().toString().trim();
            String instructions = editInstructions.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter the recipe title!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter the recipe description!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (time.isEmpty()) {
                Toast.makeText(this, "Please enter the cooking time!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPositiveNumber(time)) {
                Toast.makeText(this, "Time must be a positive number!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (instructions.isEmpty()) {
                Toast.makeText(this, "Please enter cooking instructions!", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = layoutIngredients.getChildCount();
            if (count == 0) {
                Toast.makeText(this, "Please add at least one ingredient!", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < count; i++) {
                LinearLayout row = (LinearLayout) layoutIngredients.getChildAt(i);
                EditText editName = (EditText) row.getChildAt(0);
                EditText editAmount = (EditText) row.getChildAt(1);

                String ingredientName = editName.getText().toString().trim();
                String ingredientAmount = editAmount.getText().toString().trim();

                if (ingredientName.isEmpty()) {
                    Toast.makeText(this, "Ingredient name cannot be empty (row " + (i + 1) + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ingredientAmount.isEmpty()) {
                    Toast.makeText(this, "Ingredient amount cannot be empty (row " + (i + 1) + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            int cookTime = Integer.parseInt(time);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String imagePath = savedImagePath;
            String updatedAt = currentDate;

            Glide.with(this).load(new File(savedImagePath)).into(imageView);

            Recipe recipe = new Recipe(
                    1, title, time, type, origin, currentDate, updatedAt, userId,
                    imagePath, null, null, instructions, description, 0
            );
            recipe.setIsApproved(null);
            recipe.setRejectReason(null);
            long recipeId = dbHelper.insertRecipe(recipe);

            if (recipeId == -1) {
                Toast.makeText(this, "Failed to add recipe!", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < count; i++) {
                LinearLayout row = (LinearLayout) layoutIngredients.getChildAt(i);
                EditText editName = (EditText) row.getChildAt(0);
                EditText editAmount = (EditText) row.getChildAt(1);

                String ingredientName = editName.getText().toString().trim();
                String ingredientAmount = editAmount.getText().toString().trim();

                int ingredientId = dbHelper.getOrCreateIngredientId(ingredientName);
                long detailId = dbHelper.insertDetailRecipeIngredient((int) recipeId, ingredientId, ingredientAmount);

                if (detailId == -1) {
                    Toast.makeText(this, "Error saving ingredient at row " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(this, "Recipe added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            String savedPath = saveImageToInternalStorage(selectedImageUri);
            if (savedPath != null) {
                savedImagePath = savedPath;
                selectedImageUri = Uri.fromFile(new File(savedPath));
                Toast.makeText(this, "Image saved successfully!", Toast.LENGTH_SHORT).show();
                Glide.with(this).load(selectedImageUri).into(imageView);
            } else {
                Toast.makeText(this, "Failed to save image!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSpinners() {
        String[] types = {"Main Dish", "Dessert", "Drink"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        String[] origins = {"Vietnamese", "Italian", "Japanese", "Mexican"};
        ArrayAdapter<String> adapterOrigin = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, origins);
        adapterOrigin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigin.setAdapter(adapterOrigin);
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.contains("user_name");
    }

    private String getLoggedInUserName() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getString("user_name", "");
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

        EditText editName = new EditText(this);
        editName.setHint("Ingredient Name");
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f);
        editName.setLayoutParams(nameParams);

        EditText editAmount = new EditText(this);
        editAmount.setHint("Amount");
        LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        amountParams.setMarginStart(8);
        editAmount.setLayoutParams(amountParams);

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

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            String fileName = "recipe_" + System.currentTimeMillis() + ".jpg";

            File directory = new File(getFilesDir(), "recipe_images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isPositiveNumber(String str) {
        try {
            return Integer.parseInt(str) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
