package com.example.recipeapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class User_management extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    RecyclerView recyclerView;
    Button btnActive, btnBanned;
    UserAdapter userAdapter;
    ArrayList<User> userList = new ArrayList<>();
    String currentStatus = "active"; // Mặc định là active

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_management);

        dbHelper = new KET_NOI_CSDL(this, null, null, 1);
        recyclerView = findViewById(R.id.recyclerViewUsers);
        btnActive = findViewById(R.id.btnActive);
        btnBanned = findViewById(R.id.btnBanned);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList, dbHelper, this);
        recyclerView.setAdapter(userAdapter);

        loadUsers("active");
        updateTabUI(); // cập nhật UI ban đầu

        btnActive.setOnClickListener(v -> {
            loadUsers("active");
            updateTabUI();
        });

        btnBanned.setOnClickListener(v -> {
            loadUsers("banned");
            updateTabUI();
        });

        Button btnback = findViewById(R.id.userManagementbtnGoBack);
        btnback.setOnClickListener(v -> {
            Intent it = new Intent(User_management.this, Admin_page.class);
            startActivity(it);
        });
    }

    private void loadUsers(String status) {
        userList.clear();
        String query = "SELECT * FROM Users WHERE Role != 'Admin'";

        if (status.equals("banned")) {
            query += " AND IsBanned = 1";
        } else {
            query += " AND IsBanned = 0";
        }

        Cursor cursor = dbHelper.Doc_bang(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("UserID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("UserName"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
                int isBanned = cursor.getInt(cursor.getColumnIndexOrThrow("IsBanned"));
                String avatar = cursor.getString(cursor.getColumnIndexOrThrow("Avatar"));
                String banReason = cursor.getString(cursor.getColumnIndexOrThrow("BanReason"));

                userList.add(new User(id, name, email, isBanned, avatar, banReason));
            }
            cursor.close();
        }

        currentStatus = status;
        userAdapter.setCurrentStatus(currentStatus);
        userAdapter.notifyDataSetChanged();
    }

    // Cập nhật màu nền các tab
    private void updateTabUI() {
        if (currentStatus.equals("active")) {
            btnActive.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green_active));
            btnBanned.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red_inactive));
        } else {
            btnActive.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green_inactive));
            btnBanned.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red_active));
        }
    }
}
