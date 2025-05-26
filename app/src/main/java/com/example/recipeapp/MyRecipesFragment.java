package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecipesFragment extends Fragment implements RecipeUserAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private RecipeUserAdapter recipeUserAdapter;
    private KET_NOI_CSDL db;
    private ArrayList<Recipe> displayedRecipes = new ArrayList<>();

    private static final int REQUEST_CODE_DETAIL = 1000;

    public MyRecipesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_recipes_user, container, false);

        recyclerView = view.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new KET_NOI_CSDL(getContext());

        recipeUserAdapter = new RecipeUserAdapter(getContext(), displayedRecipes, db);
        recipeUserAdapter.setOnRecipeClickListener(this);
        recyclerView.setAdapter(recipeUserAdapter);

        return view;
    }

    // Phương thức để Activity truyền danh sách đã lọc vào Fragment
    public void updateRecipeList(ArrayList<Recipe> recipes) {
        displayedRecipes.clear();
        displayedRecipes.addAll(recipes);
        if (recipeUserAdapter != null) {
            recipeUserAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        intent.putExtra("ingredients", db.getIngredientsByRecipeId(recipe.getRecipeId()));
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Việc load lại danh sách giờ do Activity xử lý nên không cần gọi gì ở đây
    }
}
