package com.example.takeastep.activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityAreYouReadyBinding;

public class AreYouReadyActivity extends AppCompatActivity {
    ActivityAreYouReadyBinding areYouReadyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        areYouReadyBinding=ActivityAreYouReadyBinding.inflate(getLayoutInflater());
        setContentView(areYouReadyBinding.getRoot());
        setSupportActionBar(areYouReadyBinding.toolBar);
        areYouReadyBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }

}