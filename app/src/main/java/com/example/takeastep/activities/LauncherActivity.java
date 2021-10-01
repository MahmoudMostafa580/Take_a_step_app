package com.example.takeastep.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.takeastep.activities.admin.AdminDashboardActivity;
import com.example.takeastep.activities.user.MainActivity;
import com.example.takeastep.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {
    SharedPreferences mySharedPreferences;
    ActivityLauncherBinding launcherBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcherBinding=ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(launcherBinding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mySharedPreferences=getSharedPreferences("userData",MODE_PRIVATE);

        checkState();
    }
    private void checkState() {
        Boolean isLogged=mySharedPreferences.getBoolean("isLogged",false);
        Boolean isUser=mySharedPreferences.getBoolean("isUser",true);
        Boolean isAdmin=mySharedPreferences.getBoolean("isAdmin",false);
        if (isLogged){
            if (isUser){
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                },2000);
            }
            if (isAdmin){
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class));
                    finish();
                },2000);
            }
        }else{
            new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            },2000);
        }
    }
}