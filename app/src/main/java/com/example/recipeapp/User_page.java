package com.example.recipeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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

        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(User_page.this, User_home_page.class);
                startActivity(it);
            }
        });
    }

}