package com.example.recipeapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Sign_up extends AppCompatActivity {
    KET_NOI_CSDL dbHelper;

    EditText editUsername, editEmail, editPassword;
    CheckBox checkboxShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sign_up);
        dbHelper = new KET_NOI_CSDL(this, null, null, 1);

        editUsername = findViewById(R.id.edit_username_signup);
        editEmail = findViewById(R.id.edit_email_signup);
        editPassword = findViewById(R.id.edit_password_signup);
        checkboxShow = findViewById(R.id.checkboxShow);

        checkboxShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        Button btnSignup = findViewById(R.id.button_signup);
        btnSignup.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Sign_up.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(Sign_up.this, "Password must include uppercase, lowercase, number, special character and be at least 8 characters", Toast.LENGTH_LONG).show();
                return;
            }

            if (isEmailExists(email)) {
                Toast.makeText(Sign_up.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            addUser(username, email, password);
            Toast.makeText(Sign_up.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();

            // Chuyển sang màn hình Login
            Intent it = new Intent(Sign_up.this, Login.class);
            startActivity(it);
            finish();
        });

        Button btnLogin = findViewById(R.id.button_login_here);
        btnLogin.setOnClickListener(v -> {
            Intent it = new Intent(Sign_up.this, Login.class);
            startActivity(it);
        });
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isEmailExists(String email) {
        Cursor cursor = dbHelper.Doc_bang("SELECT * FROM Users WHERE Email = '" + email + "'");
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void addUser(String userName, String email, String password) {
        String sql = "INSERT INTO Users (UserName, Email, Password, Role, IsBanned, Avatar, Gender) VALUES (" +
                "'" + userName + "', " +
                "'" + email + "', " +
                "'" + password + "', " +
                "'User', 0, NULL, 'Other')";
        dbHelper.Ghi_bang(sql);
    }


}