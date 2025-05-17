    package com.example.recipeapp;

    import android.content.Intent;
    import android.os.Bundle;

    import com.google.android.material.snackbar.Snackbar;

    import androidx.appcompat.app.AppCompatActivity;

    import android.view.View;

    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.navigation.ui.AppBarConfiguration;
    import androidx.navigation.ui.NavigationUI;

    import com.example.recipeapp.databinding.ActivityMainBinding;

    import android.view.Menu;
    import android.view.MenuItem;
    import android.widget.Button;
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