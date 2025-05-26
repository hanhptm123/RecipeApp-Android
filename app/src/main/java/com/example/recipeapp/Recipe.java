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
        private int countView; // Thêm biến này để lưu số lượt xem
        private String description;
        private int totalFavourites;
        // Constructor đầy đủ (bổ sung instructions và updatedAt)
        public Recipe(int id, String title, String time, String type, String origin,
                      String date, String updatedAt, int userId, String imagePath,
                      Integer isApproved, String rejectReason, String instructions, String description, int countView) {
            this.recipeId = id;
            this.title = title;
            this.time = time;
            this.type = type;
            this.origin = origin;
            this.date = date;
            this.updatedAt = updatedAt;
            this.userId = userId;
            this.imagePath = imagePath;
            this.userImage = userImage;
            this.isApproved = isApproved;
            this.rejectReason = rejectReason;
            this.instructions = instructions;
            this.description = description;
            this.countView = countView;

        }

        // Getters
        public int getRecipeId() {
            return recipeId;
        }

        public Recipe() {
            // Constructor mặc định để tạo object rồi set dữ liệu sau
        }
        public void setTitle(String title) {
            this.title = title;
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
        public int getCountView() {
            return countView;
        }

        public void setCountView(int countView) {
            this.countView = countView;
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

        public String getUpdatedAt() {
            return updatedAt;
        }


        public int getUserImage() {
            return userImage;
        }

        public String getImagePath() {
            return imagePath;
        }

        public Integer getIsApproved() {
            return isApproved;
        }

        public String getRejectReason() {
            return rejectReason;
        }

        public String getInstructions() {
            return instructions;
        }

        // Setters
        public void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

        public void setIsApproved(Integer isApproved) {
            this.isApproved = isApproved;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }


        public int getId() {
            return recipeId;
        }

        private ArrayList<DetailRecipeIngredient> detailIngredients;

        public ArrayList<DetailRecipeIngredient> getDetailIngredients() {
            return detailIngredients;
        }

        public void setDetailIngredients(ArrayList<DetailRecipeIngredient> detailIngredients) {
            this.detailIngredients = detailIngredients;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }

        public void setType(String type) {
            this.type = type;
        }
        public void setTime(String time) {
            this.time = time;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
        public void setDate(String date) {
            this.date = date;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }


        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
////        public int getUserId() {
////            if (user != null) {
////                try {
////                    return Integer.parseInt(user);
////                } catch (NumberFormatException e) {
////                    // Nếu user không phải là số hợp lệ
////                    return -1; // hoặc giá trị báo lỗi
////                }
////            }
////            return -1; // nếu user null
////        }

 //Constructor đơn giản cho Top Favourite Recipe
// Constructor đơn giản cho top favourite recipes

        public Recipe(int id, String title, String imagePath, String description, int totalFavourites) {
            this.recipeId = id;
            this.title = title;
            this.imagePath = imagePath;
            this.description = description;
            this.totalFavourites = totalFavourites;
        }

        public int getTotalFavourites() {
            return totalFavourites;
        }

        public void setTotalFavourites(int totalFavourites) {
            this.totalFavourites = totalFavourites;
        }


        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

    }

