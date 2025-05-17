    package com.example.recipeapp;

    import android.content.Intent;
    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;

    import android.view.View;

    import android.widget.ImageButton;


    public class MainActivity extends AppCompatActivity {
        KET_NOI_CSDL dbHelper;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_main);
            dbHelper = new KET_NOI_CSDL(this, "RecipeDB.db", null, 1);

            ImageButton btnlogin = (ImageButton) findViewById(R.id.btnlogin);
            btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(MainActivity.this,Login.class);
                    startActivity(it);


                }
            });

        }
    }