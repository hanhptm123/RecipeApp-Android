package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private ArrayList<Recipe> filteredRecipes;
    private int userId;

    private Button btnPending, btnAccepted, btnRejected;

    public MyRecipesFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_recipes_user, container, false);

        recyclerView = view.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new KET_NOI_CSDL(getContext());

        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt("userId", -1);
        }

        userRecipes = db.getRecipesByUserId(userId); // Toàn bộ recipe của user
        filteredRecipes = new ArrayList<>();

        recipeUserAdapter = new RecipeUserAdapter(getContext(), filteredRecipes, db);
        recyclerView.setAdapter(recipeUserAdapter);

        // Ánh xạ nút
        btnPending = view.findViewById(R.id.btnPending);
        btnAccepted = view.findViewById(R.id.btnAccepted);
        btnRejected = view.findViewById(R.id.btnRejected);

        // Gán sự kiện lọc
        btnPending.setOnClickListener(v -> filterRecipes(null));
        btnAccepted.setOnClickListener(v -> filterRecipes(1));
        btnRejected.setOnClickListener(v -> filterRecipes(0));

        // Mặc định hiển thị các recipe đang chờ duyệt
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
}
