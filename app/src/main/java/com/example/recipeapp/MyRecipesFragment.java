package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecipesFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeUserAdapter recipeUserAdapter;
    private KET_NOI_CSDL db;
    private ArrayList<Recipe> userRecipes;
    private int userId;

    public MyRecipesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_recipes_user, container, false);


        recyclerView = view.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new KET_NOI_CSDL(getContext());

        // Nhận userId từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt("userId", -1);
        }

        String userIdStr = String.valueOf(userId);
        userRecipes = db.getRecipesByUserId(userId);



        Log.d("MyRecipesFragment", "userId = " + userId);
        Log.d("MyRecipesFragment", "Số công thức: " + userRecipes.size());

        recipeUserAdapter = new RecipeUserAdapter(getContext(), userRecipes, db);
        recyclerView.setAdapter(recipeUserAdapter);

        return view;
    }
}
