package com.example.takeastep.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.takeastep.R;
import com.example.takeastep.activities.SignInActivity;
import com.example.takeastep.databinding.ActivityMainBinding;
import com.example.takeastep.fragments.HelpCenterFragment;
import com.example.takeastep.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding homeBinding;
    FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;

    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;

    ActionBarDrawerToggle toggle;

    RoundedImageView drawerProfileImage;
    TextView drawerUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mySharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor = mySharedPreferences.edit();

        setSupportActionBar(homeBinding.toolBar);
        toggle=new ActionBarDrawerToggle(this,homeBinding.drawerLayout,
                homeBinding.toolBar, R.string.open,R.string.close);
        homeBinding.drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new HomeFragment()).commit();
            homeBinding.drawerNavView.setCheckedItem(R.id.home);

        }

        homeBinding.drawerNavView.setNavigationItemSelectedListener(this);

        if (homeBinding.drawerNavView.getHeaderCount() > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            View headerLayout = homeBinding.drawerNavView.getHeaderView(0);
            drawerProfileImage=headerLayout.findViewById(R.id.drawer_profile_image);
            drawerUserName=headerLayout.findViewById(R.id.drawer_user_name);
            loadUserDetails();
        }

    }

    private void loadUserDetails() {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        StorageReference img= mStorageReference.child("usersPictures/"+firebaseAuth.getCurrentUser().getEmail());
        img.getDownloadUrl()
                .addOnSuccessListener(uri ->{
                    Glide.with(MainActivity.this)
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.ic_person)
                            .into(drawerProfileImage);
                    drawerUserName.setText(mFirebaseUser.getDisplayName());
                }).addOnFailureListener(e ->
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
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
        switch (item.getItemId()){
            case R.id.help_center:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new HelpCenterFragment()).commit();
                break;
            case R.id.logout:
            {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                editor.putBoolean("isLogged",false);
                editor.apply();
                finish();
            }
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new HomeFragment()).commit();
        }

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