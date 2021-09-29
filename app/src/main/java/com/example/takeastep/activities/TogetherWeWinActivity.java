package com.example.takeastep.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityTogetherWeWinBinding;


public class TogetherWeWinActivity extends AppCompatActivity {
    ActivityTogetherWeWinBinding togetherWeWinBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        togetherWeWinBinding = ActivityTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(togetherWeWinBinding.getRoot());

        setSupportActionBar(togetherWeWinBinding.toolBar);
        togetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

    }
}