package com.example.recipeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class User_profile extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;
    EditText inputUsername, inputPhone, inputAddress;
    RadioGroup groupGender;
    RadioButton radioMale, radioFemale, radioOther;
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_profile);

        // Ánh xạ view
        inputUsername = findViewById(R.id.input_username);
        inputPhone = findViewById(R.id.input_phone);
        inputAddress = findViewById(R.id.input_address);
        groupGender = findViewById(R.id.group_gender);
        radioMale = findViewById(R.id.radio_male);
        radioFemale = findViewById(R.id.radio_female);
        radioOther = findViewById(R.id.radio_other);
        imgProfile = findViewById(R.id.img_profile);

        dbHelper = new KET_NOI_CSDL(this, null, null, 1);

        // Xử lý các nút
        ImageButton btnlogout = findViewById(R.id.profilebtnLogout);
        btnlogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(User_profile.this)
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
                        Intent intent = new Intent(User_profile.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        ImageButton btnhome = findViewById(R.id.btn_home);
        btnhome.setOnClickListener(v -> {
            Intent it = new Intent(User_profile.this, User_page.class);
            startActivity(it);
            finish();
        });

        Button btnback = findViewById(R.id.btn_go_back);
        btnback.setOnClickListener(v -> {
            Intent it = new Intent(User_profile.this, User_home_page.class);
            startActivity(it);
        });

        Button btneditprofile = findViewById(R.id.btn_edit_profile);
        btneditprofile.setOnClickListener(v -> {
            Intent it = new Intent(User_profile.this, User_edit_profile.class);
            startActivity(it);
        });

        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Tải lại thông tin khi quay lại từ trang chỉnh sửa
    }

    private void loadUserData() {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);

        if (userId == -1) {
            startActivity(new Intent(User_profile.this, Login.class));
            finish();
            return;
        }

        Cursor cursor = dbHelper.Doc_bang("SELECT * FROM Users WHERE UserID = " + userId);

        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow("UserName"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("Address"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("Gender"));
            String avatarPath = cursor.getString(cursor.getColumnIndexOrThrow("Avatar"));

            inputUsername.setText(username);
            inputPhone.setText(phone);
            inputAddress.setText(address);

            if (gender != null) {
                switch (gender.toLowerCase()) {
                    case "male":
                        radioMale.setChecked(true);
                        break;
                    case "female":
                        radioFemale.setChecked(true);
                        break;
                    default:
                        radioOther.setChecked(true);
                        break;
                }
            } else {
                radioOther.setChecked(true);
            }

            // Dùng Glide tải ảnh avatar từ URL hoặc đường dẫn
            if (avatarPath != null && !avatarPath.trim().isEmpty()) {
                Glide.with(this)
                        .load(avatarPath.trim())
                        .placeholder(R.drawable.avatar) // ảnh mặc định khi đang tải
                        .error(R.drawable.avatar)       // ảnh fallback khi lỗi
                        .into(imgProfile);
            } else {
                // Nếu không có avatar thì đặt ảnh mặc định
                imgProfile.setImageResource(R.drawable.avatar);
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(User_profile.this, Login.class));
            finish();
        }
    }


}