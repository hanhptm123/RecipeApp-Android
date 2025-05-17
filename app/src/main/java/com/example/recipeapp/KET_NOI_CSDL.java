package com.example.recipeapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class KET_NOI_CSDL extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RecipeDB.db";
    private static final int DATABASE_VERSION = 1;

    public KET_NOI_CSDL(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng Categories
        db.execSQL("CREATE TABLE Categories (" +
                "CategoryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CategoryName TEXT)");

        // Bảng Ingredients
        db.execSQL("CREATE TABLE Ingredients (" +
                "IngredientID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IngredientName TEXT)");

        // Bảng Origins
        db.execSQL("CREATE TABLE Origins (" +
                "OriginID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "OriginName TEXT)");

        // Bảng Recipes
        db.execSQL("CREATE TABLE Recipes (" +
                "RecipeID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RecipeName TEXT, " +
                "CategoryID INTEGER, " +
                "OriginID INTEGER, " +
                "FOREIGN KEY(CategoryID) REFERENCES Categories(CategoryID), " +
                "FOREIGN KEY(OriginID) REFERENCES Origins(OriginID))");

        // Bảng DetailRecipeIngredient
        db.execSQL("CREATE TABLE DetailRecipeIngredient (" +
                "IngredientID INTEGER, " +
                "RecipeID INTEGER, " +
                "Amount TEXT, " +
                "PRIMARY KEY (IngredientID, RecipeID), " +
                "FOREIGN KEY(IngredientID) REFERENCES Ingredients(IngredientID), " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

        // Bảng Ratings
        db.execSQL("CREATE TABLE Ratings (" +
                "RatingID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RatingScore INTEGER, " +
                "Comment TEXT, " +
                "CreatedAt TEXT, " +
                "RecipeID INTEGER, " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

        // Bảng Favourites
        db.execSQL("CREATE TABLE Favourites (" +
                "FavouriteID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserID INTEGER, " +
                "RecipeID INTEGER, " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

        // Dữ liệu mẫu (tuỳ chọn)
        db.execSQL("INSERT INTO Categories (CategoryName) VALUES ('Món chính'), ('Tráng miệng')");
        db.execSQL("INSERT INTO Origins (OriginName) VALUES ('Việt Nam'), ('Nhật Bản')");
        db.execSQL("INSERT INTO Ingredients (IngredientName) VALUES ('Thịt gà'), ('Muối'), ('Đường')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Favourites");
        db.execSQL("DROP TABLE IF EXISTS Ratings");
        db.execSQL("DROP TABLE IF EXISTS DetailRecipeIngredient");
        db.execSQL("DROP TABLE IF EXISTS Recipes");
        db.execSQL("DROP TABLE IF EXISTS Origins");
        db.execSQL("DROP TABLE IF EXISTS Ingredients");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        onCreate(db);
    }

    public Cursor Doc_bang(String lenh) {
        SQLiteDatabase csdl = getReadableDatabase();
        return csdl.rawQuery(lenh, null);
    }

    public void Ghi_bang(String lenh) {
        SQLiteDatabase csdl = getWritableDatabase();
        csdl.execSQL(lenh);
    }
}

