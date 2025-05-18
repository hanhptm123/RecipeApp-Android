package com.example.recipeapp;

// DetailRecipeIngredient.java
public class DetailRecipeIngredient {
    private int recipeId;
    private int ingredientId;
    private String amount;

    public DetailRecipeIngredient(int recipeId, int ingredientId, String amount) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.amount = amount;
    }

    public int getRecipeId() { return recipeId; }
    public int getIngredientId() { return ingredientId; }
    public String getAmount() { return amount; }
}
