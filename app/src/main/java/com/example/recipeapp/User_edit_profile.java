package com.example.recipeapp;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class User_edit_profile extends AppCompatActivity {

    KET_NOI_CSDL dbHelper;
    EditText inputUsername, inputPhone, inputAddress, inputAvatarUrl;
    RadioGroup groupGender;
    RadioButton radioMale, radioFemale, radioOther;
    ImageView imageAvatar;  // Thêm ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_profile);

        dbHelper = new KET_NOI_CSDL(this, null, null, 1);

        inputUsername = findViewById(R.id.input_username_edit);
        inputPhone = findViewById(R.id.input_phone_edit);
        inputAddress = findViewById(R.id.input_address_edit);
        groupGender = findViewById(R.id.group_gender_edit);
        radioMale = findViewById(R.id.radio_male_edit);
        radioFemale = findViewById(R.id.radio_female_edit);
        radioOther = findViewById(R.id.radio_other_edit);
        inputAvatarUrl = findViewById(R.id.input_avatar_url);
        imageAvatar = findViewById(R.id.img_profile_edit);

        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPref.getInt("UserID", -1);

        loadUserData(userId);

        Button btnSave = findViewById(R.id.btn_save_profile);
        btnSave.setOnClickListener(view -> {
            String username = inputUsername.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String avatarUrl = inputAvatarUrl.getText().toString().trim();

            String gender = "Other";
            int checkedId = groupGender.getCheckedRadioButtonId();
            if (checkedId == R.id.radio_male_edit) gender = "Male";
            else if (checkedId == R.id.radio_female_edit) gender = "Female";

            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(User_edit_profile.this, "Invalid phone number. Must be 10 digits starting with 0.", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("UserName", username);
            values.put("PhoneNumber", phone);
            values.put("Address", address);
            values.put("Gender", gender);
            values.put("Avatar", avatarUrl); // Lưu luôn URL avatar, dù có hay không (cho dễ)

            int result = dbHelper.getWritableDatabase().update("Users", values, "UserID=?", new String[]{String.valueOf(userId)});
            if (result > 0) {
                Toast.makeText(User_edit_profile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                // Load lại ảnh ngay khi cập nhật xong
                if (!avatarUrl.isEmpty()) {
                    Glide.with(this).load(avatarUrl).into(imageAvatar);
                }
                finish(); // hoặc bạn có thể bỏ finish nếu muốn ở lại trang edit
            } else {
                Toast.makeText(User_edit_profile.this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData(int userId) {
        Cursor cursor = dbHelper.Doc_bang("SELECT * FROM Users WHERE UserID = " + userId);
        if (cursor.moveToFirst()) {
            inputUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("UserName")));
            inputPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("PhoneNumber")));
            inputAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("Address")));

            String avatarUrl = cursor.getString(cursor.getColumnIndexOrThrow("Avatar"));
            inputAvatarUrl.setText(avatarUrl);

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this).load(avatarUrl).into(imageAvatar);
            }

            String gender = cursor.getString(cursor.getColumnIndexOrThrow("Gender"));
            if ("Male".equals(gender)) radioMale.setChecked(true);
            else if ("Female".equals(gender)) radioFemale.setChecked(true);
            else radioOther.setChecked(true);
        }
        cursor.close();
    }
}