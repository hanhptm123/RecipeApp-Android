package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
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

    }
}