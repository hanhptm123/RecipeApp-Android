package com.example.recipeapp;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private int recipeId; // nếu có
    private int ingredientId;
    private String ingredientName;


    public Ingredient(int recipeId, int ingredientId, String ingredientName, String quantity) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
    }

    // Getter & Setter đầy đủ
    public int getRecipeId() { return recipeId; }
    public int getIngredientId() { return ingredientId; }
    public String getIngredientName() { return ingredientName; }
}
