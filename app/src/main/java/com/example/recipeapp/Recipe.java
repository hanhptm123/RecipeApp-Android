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
        public String user;
        private Integer isApproved; // Có thể null
        private String rejectReason;
        private Integer categoryId;


        public String imagePath;    // Đường dẫn ảnh
        public int userImage;

        private String instructions; // Hướng dẫn nấu ăn
        private int countView; // Thêm biến này để lưu số lượt xem


        private String description;
        // Constructor đầy đủ (bổ sung instructions và updatedAt)
        public Recipe(int recipeId, String title, String time, String type, String origin,
                      String date, String updatedAt, String user, String imagePath,
                      Integer isApproved, String rejectReason, String instructions,
                      String description, int countView) {
            this.recipeId = recipeId;
            this.title = title;
            this.time = time;
            this.type = type;
            this.origin = origin;
            this.date = date;
            this.updatedAt = updatedAt;
            this.user = user;
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

        public String getUser() {
            return user;
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

        public String setType(String type) {
            return type;
        }

        public int getUserId() {
            if (user != null) {
                try {
                    return Integer.parseInt(user);
                } catch (NumberFormatException e) {
                    // Nếu user không phải là số hợp lệ
                    return -1; // hoặc giá trị báo lỗi
                }
            }
            return -1; // nếu user null
        }

    }

