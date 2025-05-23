package com.example.recipeapp;

public class Recipe {
    private int recipeId;
    public String title;
    public String time;
    public String type;
    public String origin;
    public String date;
    public String user;
    private Integer isApproved;     // Có thể null
    private String rejectReason;
    private Integer categoryId;


    public String imagePath;  // Lưu đường dẫn ảnh dạng String
    public int userImage;

    // Constructor đầy đủ, dùng imagePath thay cho recipeImage
    public Recipe(String title, String time, String type, String origin, String date, String user, String imagePath, Integer isApproved, String rejectReason) {
        this.title = title;
        this.time = time;
        this.type = type;
        this.origin = origin;
        this.date = date;
        this.user = user;
        this.imagePath = imagePath;
        this.userImage = userImage;
        this.isApproved = isApproved;
        this.rejectReason = rejectReason;
    }
    public Recipe() {
        // Constructor mặc định để tạo object rồi set dữ liệu sau
    }
    public void setTitle(String title) {
        this.title = title;
    }
    // getter cho imagePath
    public String getImagePath() {
        return imagePath;
    }
    public Integer getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    // getter và setter các trường còn lại
    public String getTitle() {
        return title;
    }
    public String getTime() {
        return time;
    }
    public String getType() {
        return type;
    }
    public String getOrigin() {
        return origin;
    }
    public String getDate() {
        return date;
    }
    public String getUser() {
        return user;
    }
    public int getUserImage() {
        return userImage;
    }
    public Integer getIsApproved() {
        return isApproved;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    // Setters (nếu cần cập nhật)
    public void setIsApproved(Integer isApproved) {
        this.isApproved = isApproved;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String setType(String type) {
        return type;
    }
}