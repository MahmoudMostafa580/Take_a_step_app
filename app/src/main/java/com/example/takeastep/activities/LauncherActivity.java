package com.example.takeastep.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.activities.admin.AdminDashboardActivity;
import com.example.takeastep.activities.user.MainActivity;
import com.example.takeastep.databinding.ActivityLauncherBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.HashMap;
import java.util.Map;

public class LauncherActivity extends AppCompatActivity {
    SharedPreferences mySharedPreferences;
    ActivityLauncherBinding launcherBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcherBinding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(launcherBinding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mySharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        checkState();

    }

    public static SimpleExoPlayer exoPlayersVideo;
    public static Map<Integer, SimpleExoPlayer> mapExoPlayersvideo = new HashMap<>();


    public static void stopVideos(int pos) {
        for (int position : mapExoPlayersvideo.keySet())
            if (pos != position && mapExoPlayersvideo.get(position) != null)
                mapExoPlayersvideo.get(position).pause();
    }

    public static void releaseVideos(int pos) {
        for (int position : mapExoPlayersvideo.keySet())
            if (pos != position && mapExoPlayersvideo.get(position) != null) {
                mapExoPlayersvideo.get(position).stop();
                mapExoPlayersvideo.get(position).release();
            }
    }

    private void checkState() {
        Boolean isLogged = mySharedPreferences.getBoolean("isLogged", false);
        Boolean isUser = mySharedPreferences.getBoolean("isUser", true);
        Boolean isAdmin = mySharedPreferences.getBoolean("isAdmin", false);
        if (isLogged) {
            if (isUser) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }, 2000);
            }
            if (isAdmin) {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class));
                    finish();
                }, 2000);
            }
        } else {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }, 2000);
        }
    }
}