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
    private static final int DATABASE_VERSION = 11;
    public KET_NOI_CSDL(@Nullable Context context, String s, Object o, int i) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public KET_NOI_CSDL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Categories (" +
                "CategoryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CategoryName TEXT)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Ingredients (" +
                "IngredientID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IngredientName TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Origins (" +
                "OriginID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "OriginName TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Recipes (" +
                "RecipeID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RecipeName TEXT, " +
                "CategoryID INTEGER, " +
                "OriginID INTEGER, " +
                "isApproved INTEGER, " +
                "rejectReason TEXT," +
                "vdescription TEXT ,"+
                "FOREIGN KEY(CategoryID) REFERENCES Categories(CategoryID), " +
                "FOREIGN KEY(OriginID) REFERENCES Origins(OriginID))");

        db.execSQL("CREATE TABLE IF NOT EXISTS RecipeTable (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "time TEXT, " +
                "type TEXT, " +
                "origin TEXT, " +
                "date TEXT, " +
                "userId TEXT, " +
                "imagePath TEXT," +
                "isApproved INTEGER," +
                "updatedAt TEXT," +
                "instructions TEXT," +
                "description TEXT ," +
                "rejectReason TEXT,"+
                "userImage INTEGER, " +
                "CategoryId INTEGER, " +
                "FOREIGN KEY(CategoryId) REFERENCES Categories(CategoryID))");

        db.execSQL("CREATE TABLE IF NOT EXISTS DetailRecipeIngredient (" +
                "IngredientID INTEGER, " +
                "RecipeID INTEGER, " +
                "Amount TEXT, " +
                "PRIMARY KEY (IngredientID, RecipeID), " +
                "FOREIGN KEY(IngredientID) REFERENCES Ingredients(IngredientID), " +
                "FOREIGN KEY (RecipeID) REFERENCES RecipeTable(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS Ratings (" +
                "RatingID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "RatingScore INTEGER, " +
                "Comment TEXT, " +
                "CreatedAt TEXT, " +
                "RecipeID INTEGER, " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

        db.execSQL("CREATE TABLE IF NOT EXISTS Favourites (" +
                "FavouriteID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserID INTEGER, " +
                "RecipeID INTEGER, " +
                "FOREIGN KEY(RecipeID) REFERENCES Recipes(RecipeID))");

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
        db.execSQL("DROP TABLE IF EXISTS RecipeTable");
        db.execSQL("DROP TABLE IF EXISTS Recipes");
        db.execSQL("DROP TABLE IF EXISTS Origins");
        db.execSQL("DROP TABLE IF EXISTS Ingredients");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Users");
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
                int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String origin = cursor.getString(cursor.getColumnIndexOrThrow("origin"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")); // ✅ fix tại đây
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
                String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updatedAt"));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                int userImage = cursor.getInt(cursor.getColumnIndexOrThrow("userImage"));
                int isApproved = cursor.getInt(cursor.getColumnIndexOrThrow("isApproved"));
                String rejectReason = cursor.getString(cursor.getColumnIndexOrThrow("rejectReason"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));


                Recipe recipe = new Recipe(recipeId, title, time, type, origin, date, updatedAt, userId, imagePath, isApproved, rejectReason, instructions,description);

                recipe.userImage = userImage;
                list.add(recipe);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }

    @SuppressLint("Range")
    public ArrayList<Recipe> getAllRecipesFull() {
        ArrayList<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM RecipeTable", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String origin = cursor.getString(cursor.getColumnIndexOrThrow("origin"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
                String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updatedAt"));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                int index = cursor.getColumnIndex("isApproved");
                Integer isApproved = cursor.isNull(index) ? null : cursor.getInt(index);
                String rejectReason = cursor.getString(cursor.getColumnIndexOrThrow("rejectReason"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                Recipe recipe = new Recipe(1,title, time, type, origin, date, updatedAt, userId, imagePath, isApproved, rejectReason, instructions, description);
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

        if (recipe.getIsApproved() == null) {
            values.putNull("isApproved");
        } else {
            values.put("isApproved", recipe.getIsApproved());
        }

        if (recipe.getIsApproved() != null && recipe.getIsApproved() == 0) {
            values.put("rejectReason", recipe.getRejectReason());
        } else {
            values.putNull("rejectReason");
        }

        Log.d("DB_UPDATE", "Updating ID=" + recipe.getRecipeId() + ", approved=" + recipe.getIsApproved() + ", reason=" + recipe.getRejectReason());

        int rows = db.update("RecipeTable", values, "id = ?", new String[]{String.valueOf(recipe.getRecipeId())});
        Log.d("DB_UPDATE", "Rows affected: " + rows);

        db.close();
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
            values.put("userId", recipe.getUserId());
            values.put("imagePath", recipe.getImagePath());
            values.put("userImage", recipe.getUserImage());
            values.put("isApproved", recipe.getIsApproved());
            values.put("rejectReason", recipe.getRejectReason());
            values.put("updatedAt", recipe.getUpdatedAt());
            values.put("instructions", recipe.getInstructions());
            values.put("description", recipe.getDescription());

            if (recipe.getIsApproved() != null) {
                values.put("isApproved", recipe.getIsApproved());
            } else {
                values.putNull("isApproved");
            }

            if (recipe.getRejectReason() != null) {
                values.put("rejectReason", recipe.getRejectReason());
            } else {
                values.putNull("rejectReason");
            }

            result = db.insert("RecipeTable", null, values);
            db.close();
        } catch (Exception e) {
            Log.e("InsertError", "Lỗi khi insert: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public int getOrCreateIngredientId(String name) {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT IngredientID FROM Ingredients WHERE IngredientName = ?";
            cursor = db.rawQuery(query, new String[]{name});

            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndexOrThrow("IngredientID"));
            } else {
                cursor.close();
                cursor = null;

                // Lấy writable DB để insert
                SQLiteDatabase writableDb = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("IngredientName", name);
                long insertedId = writableDb.insert("Ingredients", null, values);

                if (insertedId != -1) {
                    id = (int) insertedId;
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return id;
    }
    public List<DetailRecipeIngredient> layDanhSachNguyenLieu(int recipeId) {
        List<DetailRecipeIngredient> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT d.RecipeID, d.IngredientID, i.IngredientName, d.Amount " +
                "FROM DetailRecipeIngredient d " +
                "JOIN Ingredients i ON d.IngredientID = i.IngredientID " +
                "WHERE d.RecipeID = ?", new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                int ingrId = cursor.getInt(1);
                String ingrName = cursor.getString(2);
                String amount = cursor.getString(3);
                list.add(new DetailRecipeIngredient(ingrId, recipeId, ingrName, amount));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<DetailRecipeIngredient> getIngredientsByRecipeId(int recipeId) {
        Log.d("DB_LOG", "Getting ingredients for recipeId = " + recipeId);
        ArrayList<DetailRecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT d.IngredientID, d.RecipeID, d.Amount, i.IngredientName " +
                "FROM DetailRecipeIngredient d " +
                "JOIN Ingredients i ON d.IngredientID = i.IngredientID " +
                "WHERE d.RecipeID = ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int ingredientId = cursor.getInt(cursor.getColumnIndexOrThrow("IngredientID"));
                    String ingredientName = cursor.getString(cursor.getColumnIndexOrThrow("IngredientName"));
                    String amount = cursor.getString(cursor.getColumnIndexOrThrow("Amount"));

                    DetailRecipeIngredient dri = new DetailRecipeIngredient(ingredientId, recipeId, ingredientName, amount);
                    list.add(dri);
                } while (cursor.moveToNext());

                Log.d("DB_LOG", "Found " + list.size() + " ingredients for recipeId = " + recipeId);
            } else {
                Log.d("DB_LOG", "No ingredients found for recipeId = " + recipeId);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return list;
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
    public boolean updateRecipeAndIngredients(Recipe recipe, ArrayList<DetailRecipeIngredient> newIngredients) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("title", recipe.getTitle());
            values.put("Description", recipe.getDescription());
            values.put("Time", recipe.getTime());
            values.put("Instructions", recipe.getInstructions());
            values.put("UpdatedAt", System.currentTimeMillis());

            // Cập nhật công thức
            db.update("RecipeTable", values, "id = ?", new String[]{String.valueOf(recipe.getRecipeId())});

            // Xóa toàn bộ nguyên liệu cũ
            db.delete("DetailRecipeIngredient", "RecipeID = ?", new String[]{String.valueOf(recipe.getRecipeId())});

// Thêm lại danh sách nguyên liệu mới
            for (DetailRecipeIngredient dri : newIngredients) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put("RecipeID", recipe.getRecipeId());
                ingredientValues.put("IngredientID", dri.getIngredientId());
                ingredientValues.put("Amount", dri.getAmount());
                db.insert("DetailRecipeIngredient", null, ingredientValues);
            }

            db.setTransactionSuccessful(); // commit
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

        // Lấy danh sách tất cả tên loại món ăn (Category)
    public List<String> getAllCategoryNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CategoryName  FROM Categories", null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return names;
    }

    // Lấy danh sách tất cả tên nguồn gốc (Origin)
    public List<String> getAllOriginNames() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT OriginName FROM Origins", null);
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return names;
    }

    // Lấy ID của loại món ăn từ tên
    public int getCategoryIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CategoryID FROM Categories WHERE CategoryName = ?", new String[]{name});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }


    // Lấy tên loại món ăn từ ID
    public String getCategoryNameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CategoryName  FROM Categories WHERE CategoryID = ?", new String[]{String.valueOf(id)});
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return name;
    }
    public int getIngredientIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT IngredientID FROM Ingredients WHERE IngredientName = ?", new String[]{name});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        return id;
    }


    public int insertIngredientIfNotExists(String ingredientName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int existingId = getIngredientIdByName(ingredientName);
        if (existingId != -1) {
            return existingId;
        }

        ContentValues values = new ContentValues();
        values.put("IngredientName", ingredientName);
        long newId = db.insert("Ingredients", null, values);

        return (int) newId;
    }
    public ArrayList<Recipe> getRecipesByUserId(int userId) {
        ArrayList<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM RecipeTable WHERE userId = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Đọc các cột Recipe từ cursor
                int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String origin = cursor.getString(cursor.getColumnIndexOrThrow("origin"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("updatedAt"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"));
                int isApproved = cursor.getInt(cursor.getColumnIndexOrThrow("isApproved"));
                String rejectReason = cursor.getString(cursor.getColumnIndexOrThrow("rejectReason"));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                Recipe recipe = new Recipe(recipeId, title, time, type, origin, date, updatedAt, userId, imagePath, isApproved, rejectReason, instructions, description);

                list.add(recipe);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }




}
