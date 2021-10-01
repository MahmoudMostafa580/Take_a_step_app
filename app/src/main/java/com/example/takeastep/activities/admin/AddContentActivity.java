package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityAddContentBinding;

public class AddContentActivity extends AppCompatActivity {
    ActivityAddContentBinding addContentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentBinding=ActivityAddContentBinding.inflate(getLayoutInflater());
        setContentView(addContentBinding.getRoot());

        setSupportActionBar(addContentBinding.toolBar);
        addContentBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());
    }
}