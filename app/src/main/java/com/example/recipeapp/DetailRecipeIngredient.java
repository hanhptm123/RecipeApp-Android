package com.example.recipeapp;

import java.io.Serializable;

/**
 * Class đại diện cho chi tiết nguyên liệu của một công thức nấu ăn.
 */
public class DetailRecipeIngredient implements Serializable {
    private int recipeId;
    private int ingredientId;
    private String ingredientName;
    private String amount;
    private Ingredient ingredient;

    // Constructor đầy đủ (không bao gồm đối tượng Ingredient)
    public DetailRecipeIngredient(int ingredientId, int recipeId, String ingredientName, String amount) {
        this.ingredientId = ingredientId;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.amount = amount;
    }

    // Constructor có đối tượng Ingredient (nếu cần)
    public DetailRecipeIngredient(int ingredientId, int recipeId, String ingredientName, String amount, Ingredient ingredient) {
        this(ingredientId, recipeId, ingredientName, amount);
        this.ingredient = ingredient;
    }

    // Getters và setters
    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
}
