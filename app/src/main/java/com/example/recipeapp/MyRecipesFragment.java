package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private ArrayList<Recipe> userRecipes;
    private int userId;

    private static final int REQUEST_CODE_DETAIL = 1000;

    public MyRecipesFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_recipes_user, container, false);

        recyclerView = view.findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = new KET_NOI_CSDL(getContext());

        // Nhận userId từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt("userId", -1);
        }

        userRecipes = db.getRecipesByUserId(userId);

        Log.d("MyRecipesFragment", "userId = " + userId);
        Log.d("MyRecipesFragment", "Số công thức: " + userRecipes.size());

        // Truyền listener vào adapter để bắt sự kiện click
        recipeUserAdapter = new RecipeUserAdapter(getContext(), userRecipes, db);
        recipeUserAdapter.setOnRecipeClickListener(this); // Đăng ký listener
        recyclerView.setAdapter(recipeUserAdapter);

        return view;
    }

    public void loadRecipeList() {
        userRecipes.clear();
        userRecipes.addAll(db.getRecipesByUserId(userId));
        recipeUserAdapter.notifyDataSetChanged();
    }

    // --- Thêm phương thức xử lý sự kiện click mở Detail ---
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        // Gọi startActivityForResult từ Fragment
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
    }

    // Nhận kết quả trả về từ Detail
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == getActivity().RESULT_OK) {
            // Load lại danh sách công thức khi có thay đổi (ví dụ xóa công thức)
            loadRecipeList();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Load lại danh sách công thức từ CSDL khi fragment được hiện lại
        loadRecipeList();
    }

}
