package com.example.recipeapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private int recipeId;
    public String title;
    public String time;
    public String type;
    public String origin;
    public String date;         // Ngày tạo
    public String updatedAt;    // Thời gian cập nhật
    private int userId;

    private Integer isApproved; // Có thể null
    private String rejectReason;
    private Integer categoryId;

    public String imagePath;    // Đường dẫn ảnh
    public int userImage;

    private String instructions; // Hướng dẫn nấu ăn
    private int countView;       // Số lượt xem
    private String description;
    private int totalFavourites;

    private String userName;

    private ArrayList<DetailRecipeIngredient> detailIngredients;

    // Constructor đầy đủ
    public Recipe(int id, String title, String time, String type, String origin,
                  String date, String updatedAt, int userId, String imagePath,
                  Integer isApproved, String rejectReason, String instructions,
                  String description, int countView) {
        this.recipeId = id;
        this.title = title;
        this.time = time;
        this.type = type;
        this.origin = origin;
        this.date = date;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.imagePath = imagePath;
        this.isApproved = isApproved;
        this.rejectReason = rejectReason;
        this.instructions = instructions;
        this.description = description;
        this.countView = countView;
    }

    // Constructor đơn giản cho Top Favourite Recipe
    public Recipe(int id, String title, String imagePath, String description, int totalFavourites) {
        this.recipeId = id;
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
        this.totalFavourites = totalFavourites;
    }

    // Constructor mặc định
    public Recipe() {}

    // Getters và Setters
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getUserImage() {
        return userImage;
    }

    public void setUserImage(int userImage) {
        this.userImage = userImage;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getCountView() {
        return countView;
    }

    public void setCountView(int countView) {
        this.countView = countView;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalFavourites() {
        return totalFavourites;
    }

    public void setTotalFavourites(int totalFavourites) {
        this.totalFavourites = totalFavourites;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<DetailRecipeIngredient> getDetailIngredients() {
        return detailIngredients;
    }

    public void setDetailIngredients(ArrayList<DetailRecipeIngredient> detailIngredients) {
        this.detailIngredients = detailIngredients;
    }

    // Alias cho getId()
    public int getId() {
        return recipeId;
    }
}
