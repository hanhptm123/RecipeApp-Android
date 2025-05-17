package com.example.recipeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText editemail, editpassword;
    Button buttonLogin;
    CheckBox checkboxShow;
    KET_NOI_CSDL dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        editemail = findViewById(R.id.editemail);
        editpassword = findViewById(R.id.editpassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        checkboxShow = findViewById(R.id.checkboxShow);

        dbHelper = new KET_NOI_CSDL(this, "RecipeDB.db", null, 1);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editemail.getText().toString().trim();
                String password = editpassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter all required fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                        "SELECT * FROM Users WHERE Email = ? AND Password = ?",
                        new String[]{email, password}
                );

                if (cursor.moveToFirst()) {
                    int isBanned = cursor.getInt(cursor.getColumnIndexOrThrow("IsBanned"));
                    String banReason = cursor.getString(cursor.getColumnIndexOrThrow("BanReason"));

                    if (isBanned == 1) {
                        Toast.makeText(Login.this, "Your account has been banned. Reason: " + banReason, Toast.LENGTH_LONG).show();
                    } else {
                        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("UserID"));
                        String role = cursor.getString(cursor.getColumnIndexOrThrow("Role"));

                        // ✅ Lưu vào SharedPreferences
                        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("UserID", userId);
                        editor.putString("Role", role);
                        editor.apply();

                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("UserID", userId);
                        intent.putExtra("Role", role);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(Login.this, "Incorrect email or password.", Toast.LENGTH_SHORT).show();
                }

                cursor.close();
            }
        });

        // Show/hide password
        checkboxShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                editpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editpassword.setSelection(editpassword.length());
        });
        //code button go back
        Button btback = (Button) findViewById(R.id.btngoback);
        btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this,MainActivity.class);
                startActivity(it);
            }
        });
    }
}
