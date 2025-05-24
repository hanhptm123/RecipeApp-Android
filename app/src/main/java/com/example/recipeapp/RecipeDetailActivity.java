package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView imageRecipe;
    private TextView textTitle, textTime, textType, textOrigin, textDate, textUser, textInstruction, textDescription;
    private LinearLayout ingredientsContainer;
    private Button btnGoBack, btnEdit;

    private Recipe recipe;
    private ArrayList<DetailRecipeIngredient> detailIngredients;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Map views
        imageRecipe = findViewById(R.id.image_recipe);
        textTitle = findViewById(R.id.text_title);
        textTime = findViewById(R.id.text_time);
        textType = findViewById(R.id.text_type);
        textOrigin = findViewById(R.id.text_origin);
        textDate = findViewById(R.id.text_date);
        textUser = findViewById(R.id.text_user);
        textInstruction = findViewById(R.id.text_instruction);
        textDescription = findViewById(R.id.text_description);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        btnGoBack = findViewById(R.id.btn_go_back);
        btnEdit = findViewById(R.id.btnEdit);

        // Get data from Intent
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        detailIngredients = (ArrayList<DetailRecipeIngredient>) getIntent().getSerializableExtra("ingredients");
        currentUserId = getIntent().getIntExtra("currentUserId", -1);

        if (recipe != null) {
            displayRecipeInfo(recipe);

            // Set visibility of Edit button
            btnEdit.setVisibility(recipe.getUserId() == currentUserId ? View.VISIBLE : View.GONE);
        }

        if (detailIngredients != null && !detailIngredients.isEmpty()) {
            displayIngredients(detailIngredients);
            recipe.setDetailIngredients(detailIngredients);
        }

        // Handle Edit button click
        btnEdit.setOnClickListener(v -> {
            Log.d("DEBUG_EDIT", "Edit button clicked");
            if (recipe == null) {
                Toast.makeText(this, "Recipe is null", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(RecipeDetailActivity.this, EditRecipeActivity.class);
            intent.putExtra("RECIPE_TO_EDIT", recipe);
            startActivityForResult(intent, 1001);
        });

        // Handle Go Back button
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void displayRecipeInfo(Recipe recipe) {
        textTitle.setText(recipe.getTitle());
        textTime.setText("Time: " + recipe.getTime());
        textType.setText("Type: " + recipe.getType());
        textOrigin.setText("Origin: " + recipe.getOrigin());
        textDate.setText("Posted on: " + recipe.getDate());
        textUser.setText("Posted by: " + recipe.getUserId());
        textDescription.setText(recipe.getDescription());
        textInstruction.setText(recipe.getInstructions());

        // Temporary placeholder image
        imageRecipe.setImageResource(R.drawable.chebamau);
    }

    private void displayIngredients(ArrayList<DetailRecipeIngredient> ingredients) {
        ingredientsContainer.removeAllViews();
        for (DetailRecipeIngredient dri : ingredients) {
            TextView tv = new TextView(this);
            tv.setText("- " + dri.getIngredientName() + ": " + dri.getAmount());
            tv.setTextSize(16);
            ingredientsContainer.addView(tv);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null) {
                Recipe updatedRecipe = (Recipe) data.getSerializableExtra("UPDATED_RECIPE");
                if (updatedRecipe != null) {
                    this.recipe = updatedRecipe;
                    displayRecipeInfo(recipe);
                    if (recipe.getDetailIngredients() != null) {
                        displayIngredients(recipe.getDetailIngredients());
                    }
                }
            }
        }
    }
}
