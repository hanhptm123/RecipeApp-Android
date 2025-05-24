package com.example.recipeapp;

public class Rating {
    private int userId;
    private String userName;
    private int ratingScore;
    private String createdAt;
    private String comment;

    public Rating(int userId, String userName, int ratingScore, String createdAt, String comment) {
        this.userId = userId;
        this.userName = userName;
        this.ratingScore = ratingScore;
        this.createdAt = createdAt;
        this.comment = comment;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getComment() {
        return comment;
    }
}
