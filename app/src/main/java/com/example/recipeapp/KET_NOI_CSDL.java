package com.example.recipeapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.ULocale;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KET_NOI_CSDL extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RecipeDB.db";
    private static final int DATABASE_VERSION = 6;

    public KET_NOI_CSDL(@Nullable Context context, String s, Object o, int i) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng Categories
        db.execSQL("CREATE TABLE IF NOT EXISTS Categories (" +
                "CategoryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CategoryName TEXT)");

        // Bảng Ingredients
        db.execSQL("CREATE TABLE IF NOT EXISTS Ingredients (" +
                "IngredientID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IngredientName TEXT)");

        // Bảng Origins
        db.execSQL("CREATE TABLE IF NOT EXISTS Origins (" +
                "OriginID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "OriginName TEXT)");

        // Bảng Recipes
        db.execSQL("CREATE TABLE IF NOT EXISTS Recipes (" +
                "RecipeID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RecipeName TEXT, " +
                "CategoryID INTEGER, " +
                "OriginID INTEGER, " +
                "isApproved INTEGER, " +
                "rejectReason TEXT,"+
                "FOREIGN KEY(CategoryID) REFERENCES Categories(CategoryID), " +
                "FOREIGN KEY(OriginID) REFERENCES Origins(OriginID))");

        db.execSQL("CREATE TABLE IF NOT EXISTS RecipeTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "time TEXT, " +
                "type TEXT, " +
                "origin TEXT, " +
                "date TEXT, " +
                "user TEXT, " +
                "imagePath TEXT," +
                "isApproved INTEGER," +
                "rejectReason TEXT,"+
                "userImage INTEGER, " +
                "FOREIGN KEY(CategoryId) REFERENCES Categories(CategoryID))");

        // Bảng DetailRecipeIngredient
        db.execSQL("CREATE TABLE IF NOT EXISTS DetailRecipeIngredient (" +
                "IngredientID INTEGER, " +
                "RecipeID INTEGER, " +
                "Amount TEXT, " +
                "PRIMARY KEY (IngredientID, RecipeID), " +
                "FOREIGN KEY(IngredientID) REFERENCES Ingredients(IngredientID), " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

        // Bảng Ratings
        db.execSQL("CREATE TABLE IF NOT EXISTS Ratings (" +
                "RatingID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RatingScore INTEGER, " +
                "Comment TEXT, " +
                "CreatedAt TEXT, " +
                "RecipeID INTEGER, " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

        // Bảng Favourites
        db.execSQL("CREATE TABLE IF NOT EXISTS Favourites (" +
                "FavouriteID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserID INTEGER, " +
                "RecipeID INTEGER, " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");
// Bảng Users
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserName TEXT, " +
                "Email TEXT NOT NULL UNIQUE, " +
                "Password TEXT NOT NULL, " +
                "Role TEXT DEFAULT 'User', " +
                "IsBanned INTEGER DEFAULT 0, " +
                "Avatar TEXT, " +
                "Address TEXT, " +
                "PhoneNumber TEXT, " +
                "Gender TEXT, " +
                "BanReason TEXT)");

        // Dữ liệu mẫu (tuỳ chọn)
        db.execSQL("INSERT INTO Categories (CategoryName) VALUES ('Món chính'), ('Tráng miệng')");
        db.execSQL("INSERT INTO Origins (OriginName) VALUES ('Việt Nam'), ('Nhật Bản')");
        db.execSQL("INSERT INTO Ingredients (IngredientName) VALUES ('Thịt gà'), ('Muối'), ('Đường')");
        db.execSQL("INSERT INTO Users (UserName, Email, Password, Role, IsBanned, Avatar, Address, PhoneNumber, Gender, BanReason) VALUES " +
                "('Admin', 'admin@gmail.com', '123456', 'Admin', 0, NULL, '123 Admin Street', '0123456789', 'Male', NULL), " +
                "('User1', 'user1@example.com', 'password1', 'User', 0, NULL, '456 User Ave', '0987654321', 'Female', NULL), " +
                "('BannedUser', 'banned@example.com', '123456', 'User', 1, NULL, '789 Banned Rd', '0111222333', 'Other', 'Vi phạm nội quy')");


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
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS RecipeTable");
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
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RecipeTable WHERE IsApproved = 1", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String origin = cursor.getString(cursor.getColumnIndexOrThrow("origin"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String user = cursor.getString(cursor.getColumnIndexOrThrow("user"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
                int userImage = cursor.getInt(cursor.getColumnIndexOrThrow("userImage"));
                int isApproved = cursor.getInt(cursor.getColumnIndexOrThrow("isApproved"));
                Boolean approved = isApproved == 1;
                String rejectReason = cursor.getString(cursor.getColumnIndexOrThrow("rejectReason"));
                list.add(new Recipe(title, time, type, origin, date, user, imagePath, isApproved, rejectReason));

            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }
    @SuppressLint("Range")
    public ArrayList<Recipe> getAllRecipesFull() {
        ArrayList<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query toàn bộ, không lọc theo trạng thái
        Cursor cursor = db.rawQuery("SELECT * FROM RecipeTable", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Lấy các cột giống getAllRecipes
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String origin = cursor.getString(cursor.getColumnIndexOrThrow("origin"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String user = cursor.getString(cursor.getColumnIndexOrThrow("user"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));

                // isApproved có thể null
                int index = cursor.getColumnIndex("isApproved");
                Integer isApproved = cursor.isNull(index) ? null : cursor.getInt(index);

                String rejectReason = cursor.getString(cursor.getColumnIndexOrThrow("rejectReason"));

                // Tạo đối tượng Recipe (chỉnh lại constructor nếu cần)
                Recipe recipe = new Recipe(title, time, type, origin, date, user, imagePath, isApproved, rejectReason);
                recipe.setRecipeId(cursor.getInt(cursor.getColumnIndex("id")));
                list.add(recipe);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }


    public void updateRecipeStatus(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Cập nhật trạng thái duyệt
        if (recipe.getIsApproved() == null) {
            values.putNull("isApproved");
        } else {
            values.put("isApproved", recipe.getIsApproved());
        }

        // Cập nhật lý do từ chối nếu bị từ chối
        if (recipe.getIsApproved() != null && recipe.getIsApproved() == 0) {
            values.put("rejectReason", recipe.getRejectReason());
        } else {
            values.putNull("rejectReason");
        }

        // Ghi log để debug nếu cần
        Log.d("DB_UPDATE", "Updating ID=" + recipe.getRecipeId() + ", approved=" + recipe.getIsApproved() + ", reason=" + recipe.getRejectReason());

        // Cập nhật vào bảng
        int rows = db.update("RecipeTable", values, "id = ?", new String[]{String.valueOf(recipe.getRecipeId())});
        Log.d("DB_UPDATE", "Rows affected: " + rows);

        db.close(); // ⚠️ Nhớ đóng database
    }



    public long insertRecipe(Recipe recipe) {
        long result = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("title", recipe.getTitle());
            values.put("time", recipe.getTime());
            values.put("type", recipe.getType());
            values.put("origin", recipe.getOrigin());
            values.put("date", recipe.getDate());
            values.put("user", recipe.getUser());

            // Thay đổi ở đây: lưu URI ảnh (String)
            values.put("imagePath", recipe.getImagePath());

            // Nếu bạn vẫn cần lưu userImage (ví dụ số), giữ lại dòng này
            values.put("userImage", recipe.getUserImage());
            // isApproved và rejectReason có thể null
            if (recipe.getIsApproved() != null) {
                values.put("isApproved", recipe.getIsApproved());
            }

            if (recipe.getRejectReason() != null) {
                values.put("rejectReason", recipe.getRejectReason());
            }
            result = db.insert("RecipeTable", null, values);
        } catch (Exception e) {
            Log.e("InsertError", "Lỗi khi insert: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public Ingredient getIngredientByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Ingredient ingredient = null;

        String query = "SELECT IngredientID, IngredientName FROM Ingredients WHERE IngredientName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("IngredientID"));
            String ingredientName = cursor.getString(cursor.getColumnIndexOrThrow("IngredientName"));
            ingredient = new Ingredient(id, ingredientName);
        }
        cursor.close();
        return ingredient;
    }
    public long insertIngredient(String ingredientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("IngredientName", ingredientName);

        return db.insert("Ingredients", null, values);
    }
    public long insertDetailRecipeIngredient(int recipeId, int ingredientId, String amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("RecipeID", recipeId);
        values.put("IngredientID", ingredientId);
        values.put("Amount", amount);

        // Nếu cặp (IngredientID, RecipeID) đã tồn tại, bạn có thể dùng replace hoặc update
        // Ở đây dùng insert bình thường, nếu trùng sẽ lỗi, bạn có thể xử lý sau

        return db.insert("DetailRecipeIngredient", null, values);
    }

    public ArrayList<Recipe> searchRecipesByName(String keyword) {
        ArrayList<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM RecipeTable WHERE title LIKE ?", new String[]{"%" + keyword + "%"});
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setRecipeId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                recipe.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                // Các trường khác tương tự
                list.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public List<Recipe> searchRecipesByType(String type) {
        SQLiteDatabase db = getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM RecipeTable WHERE type = ?", new String[]{type});

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setRecipeId(cursor.getInt(0));
                recipe.setTitle(cursor.getString(1));
                recipe.setType(cursor.getString(2));
                // ... add các trường khác nếu cần
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }






}