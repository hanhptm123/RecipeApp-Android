package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Admin_page extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin);
        ImageButton btnhome = (ImageButton) findViewById(R.id.btnHome);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Admin_page.this, User_page.class);
                startActivity(it);
                finish();
            }
        });
        ImageButton btnlogout = (ImageButton) findViewById(R.id.btnLogout);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(Admin_page.this)
                        .setTitle("Confirm Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Xóa session SharedPreferences
                            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

                            // Chuyển về màn hình đăng nhập
                            Intent intent = new Intent(Admin_page.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", null) // Không làm gì nếu chọn "No"
                        .show();
            }
        });
        ImageButton btn_user_management =(ImageButton) findViewById(R.id.btnUserManagement);
        btn_user_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Admin_page.this, User_management.class);
                startActivity(it);
            }
        });

    }
}