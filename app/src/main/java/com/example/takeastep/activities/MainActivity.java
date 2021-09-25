package com.example.takeastep.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityMainBinding;
import com.example.takeastep.fragments.HelpCenterFragment;
import com.example.takeastep.fragments.HomeFragment;
import com.example.takeastep.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding homeBinding;
    FirebaseAuth firebaseAuth;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mySharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor = mySharedPreferences.edit();

        setSupportActionBar(homeBinding.toolBar);
        toggle=new ActionBarDrawerToggle(this,homeBinding.drawerLayout,
                homeBinding.toolBar, R.string.open,R.string.close);
        homeBinding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        homeBinding.drawerNavView.setNavigationItemSelectedListener(this);

        if (homeBinding.drawerNavView.getHeaderCount() > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            View headerLayout = homeBinding.drawerNavView.getHeaderView(0);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                homeBinding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment=null;
        Class fragmentClass = null;
        switch (item.getItemId()){
            case R.id.home:
                fragmentClass= HomeFragment.class;
                break;
            case R.id.profile:
                fragmentClass= ProfileFragment.class;
                break;
            case R.id.help_center:
                fragmentClass= HelpCenterFragment.class;
                break;
            case R.id.logout:
            {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                editor.putBoolean("isLogged",false);
                editor.apply();
                finish();
            }
                break;
            default:
                fragmentClass= HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_fragment, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer
        homeBinding.drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        toggle.onConfigurationChanged(newConfig);
    }
}