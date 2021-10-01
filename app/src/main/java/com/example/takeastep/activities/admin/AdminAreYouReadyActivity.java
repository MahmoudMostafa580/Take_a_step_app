package com.example.takeastep.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityAdminAreYouReadyBinding;

public class AdminAreYouReadyActivity extends AppCompatActivity {
    ActivityAdminAreYouReadyBinding adminAreYouReadyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminAreYouReadyBinding=ActivityAdminAreYouReadyBinding.inflate(getLayoutInflater());
        setContentView(adminAreYouReadyBinding.getRoot());

        setSupportActionBar(adminAreYouReadyBinding.toolBar);
        adminAreYouReadyBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.are_you_ready_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.add){
            startActivity(new Intent(getApplicationContext(),AddContentActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}