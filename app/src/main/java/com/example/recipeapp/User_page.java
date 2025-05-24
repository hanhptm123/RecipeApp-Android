package com.example.recipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class User_page extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user);

        dbHelper = new KET_NOI_CSDL(this, null, null, 1);
        ImageButton btnprofile = findViewById(R.id.btnprofile_user);

        // Lấy UserID từ SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);

        if (userId != -1) {
            Cursor cursor = dbHelper.Doc_bang("SELECT Avatar FROM Users WHERE UserID = " + userId);
            String avatarName = null;
            if (cursor.moveToFirst()) {
                avatarName = cursor.getString(cursor.getColumnIndex("Avatar"));
            }
            cursor.close();

            if (avatarName == null || avatarName.equals("profile.png")) {
                btnprofile.setImageResource(R.drawable.profile); // ảnh mặc định
            } else {
                Glide.with(this)
                        .load(avatarName)  // avatarName có thể là URL hoặc đường dẫn file
                        .placeholder(R.drawable.profile)  // ảnh tạm khi đang load
                        .error(R.drawable.profile)
                        .circleCrop()// ảnh lỗi
                        .into(btnprofile);


            }

        }
        String role = "user"; // mặc định là user
        Cursor cursor = dbHelper.Doc_bang("SELECT Avatar, Role FROM Users WHERE UserID = " + userId);
        String avatarName = null;
        if (cursor.moveToFirst()) {
            avatarName = cursor.getString(cursor.getColumnIndex("Avatar"));
            role = cursor.getString(cursor.getColumnIndex("Role")); // lấy role
        }
        cursor.close();

        loadRecipes();
        String finalRole = role; // vì dùng trong inner class
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(User_page.this, v);

                if (finalRole.equalsIgnoreCase("admin")) {
                    popupMenu.getMenu().add("Admin Page");
                    popupMenu.getMenu().add("Your World");
                    popupMenu.getMenu().add("Your favourite list");
                } else {
                    popupMenu.getMenu().add("Your World");
                    popupMenu.getMenu().add("Your favourite list");
                }
                popupMenu.getMenu().add("Logout"); // Thêm logout vào cả admin và user


                popupMenu.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    if (title.equals("Admin Page")) {
                        startActivity(new Intent(User_page.this, Admin_page.class));
                    } else if (title.equals("Your World")) {
                        startActivity(new Intent(User_page.this, User_home_page.class));
                    } else if (title.equals("Your favourite list")) {
                        startActivity(new Intent(User_page.this, Favourite_list.class));
                    } else if (title.equals("Logout")) {
                        new androidx.appcompat.app.AlertDialog.Builder(User_page.this)
                                .setTitle("Confirm Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
                                    Intent intent = new Intent(User_page.this, Login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    return true;
                });


                popupMenu.show();
            }
        });


        EditText editTextSearch = findViewById(R.id.editTextSearch);
        ImageButton btnSearch = findViewById(R.id.btnsearch_user);

        btnSearch.setOnClickListener(v -> {
            String keyword = editTextSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                loadRecipes(); // load tất cả nếu không nhập gì
            } else {
                loadRecipesByKeyword(keyword); // load theo từ khóa tìm kiếm
            }
        });
        ImageButton btnMenu = findViewById(R.id.btnmenu_user); // hoặc btnmenu_user nếu ở layout user

        btnMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(User_page.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.search_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_search_by_category) {
                    startActivity(new Intent(User_page.this, SearchByCategoryActivity.class));
                    return true;
                } else if (id == R.id.menu_search_by_ingredient) {
                    startActivity(new Intent(User_page.this, searchRecipesByIngredient.class));
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }
    private void loadRecipesByKeyword(String keyword) {
        View viewRecipes = getLayoutInflater().inflate(R.layout.layout_fragment_recipes, null);
        RecyclerView rv = viewRecipes.findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Recipe> recipes = dbHelper.searchRecipesByName(keyword);
        RecipeAdapter adapter = new RecipeAdapter(this, recipes,dbHelper);
        rv.setAdapter(adapter);

        FrameLayout content = findViewById(R.id.content_user);
        content.removeAllViews();
        content.addView(viewRecipes);
    }
    // TÁCH HÀM loadRecipes() ra để dùng chung
    private void loadRecipes() {
        View viewRecipes = getLayoutInflater().inflate(R.layout.layout_fragment_recipes, null);
        RecyclerView rv = viewRecipes.findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Recipe> recipes = dbHelper.getAllRecipes();
        RecipeAdapter adapter = new RecipeAdapter(this, recipes, dbHelper);
        rv.setAdapter(adapter);

        FrameLayout content = findViewById(R.id.content_user);
        content.removeAllViews();
        content.addView(viewRecipes);

        // Gắn sự kiện nút Add Recipes

    }

    // Gọi lại loadRecipes khi quay về từ AddActivity
    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(); // Load lại dữ liệu khi Activity resume
    }

}