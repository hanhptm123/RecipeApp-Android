package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    private ArrayList<Recipe> userRecipes;
    private ArrayList<Recipe> filteredRecipes;
    private int userId;

    private Button btnPending, btnAccepted, btnRejected;
    private static final int REQUEST_CODE_DETAIL = 1000;

    public MyRecipesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_recipes_user, container, false);

        recyclerView = view.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new KET_NOI_CSDL(getContext());

        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt("userId", -1);
        }

        userRecipes = db.getRecipesByUserId(userId);
        filteredRecipes = new ArrayList<>();

        Log.d("MyRecipesFragment", "userId = " + userId);
        Log.d("MyRecipesFragment", "Số công thức: " + userRecipes.size());

        recipeUserAdapter = new RecipeUserAdapter(getContext(), filteredRecipes, db);
        recipeUserAdapter.setOnRecipeClickListener(this);
        recyclerView.setAdapter(recipeUserAdapter);

        btnPending = view.findViewById(R.id.btnPending);
        btnAccepted = view.findViewById(R.id.btnAccepted);
        btnRejected = view.findViewById(R.id.btnRejected);

        btnPending.setOnClickListener(v -> filterRecipes(null));
        btnAccepted.setOnClickListener(v -> filterRecipes(1));
        btnRejected.setOnClickListener(v -> filterRecipes(0));

        // Mặc định hiển thị các công thức đang chờ duyệt
        filterRecipes(null);

        return view;
    }

    private void filterRecipes(Integer status) {
        filteredRecipes.clear();
        for (Recipe r : userRecipes) {
            if (status == null && r.getIsApproved() == null) {
                filteredRecipes.add(r);
            } else if (status != null && status.equals(r.getIsApproved())) {
                filteredRecipes.add(r);
            }
        }
        recipeUserAdapter.notifyDataSetChanged();
    }

    public void loadRecipeList() {
        userRecipes.clear();
        userRecipes.addAll(db.getRecipesByUserId(userId));
        filterRecipes(null); // làm mới theo bộ lọc mặc định
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        intent.putExtra("ingredients", db.getIngredientsByRecipeId(recipe.getRecipeId()));
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecipeList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == getActivity().RESULT_OK) {
            loadRecipeList();
        }
    }
}
