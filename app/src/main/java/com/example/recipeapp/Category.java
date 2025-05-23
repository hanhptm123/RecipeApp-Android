package com.example.recipeapp;

public class Category {
    private int CategoryID;
    private String CategoryName;

    public Category(int CategoryID, String CategoryName){
        this.CategoryID = CategoryID;
        this.CategoryName = CategoryName;
    }

    public int getId() { return CategoryID; }
    public String getName() { return CategoryName; }
}
