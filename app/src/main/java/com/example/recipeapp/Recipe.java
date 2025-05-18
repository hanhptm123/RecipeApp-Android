package com.example.recipeapp;

public class Recipe {
    public String title;
    public String time;
    public String type;
    public String origin;
    public String date;
    public String user;
    public String imagePath;  // Lưu đường dẫn ảnh dạng String
    public int userImage;

    // Constructor đầy đủ, dùng imagePath thay cho recipeImage
    public Recipe(String title, String time, String type, String origin, String date, String user, String imagePath) {
        this.title = title;
        this.time = time;
        this.type = type;
        this.origin = origin;
        this.date = date;
        this.user = user;
        this.imagePath = imagePath;
        this.userImage = userImage;
    }

    // getter cho imagePath
    public String getImagePath() {
        return imagePath;
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
}
