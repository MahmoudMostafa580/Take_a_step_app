package com.example.takeastep.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.takeastep.R;
import com.example.takeastep.activities.SignInActivity;
import com.example.takeastep.databinding.ActivityMainBinding;
import com.example.takeastep.fragments.HelpCenterFragment;
import com.example.takeastep.fragments.HomeFragment;
import com.example.takeastep.fragments.StatisticsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding homeBinding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;

    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;

    ActionBarDrawerToggle toggle;

    RoundedImageView drawerProfileImage;
    TextView drawerUserName;

    int messagesReceivedCounter;

    RelativeLayout customLayout;
    TextView badge;

    public static NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mFirestore=FirebaseFirestore.getInstance();
        mySharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor = mySharedPreferences.edit();

        navigationView=findViewById(R.id.drawer_nav_view);

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

        customLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.notification_badge, null);
        badge = (customLayout.findViewById(R.id.counter));
        countMessages();


        homeBinding.drawerNavView.getMenu().findItem(R.id.help_center).setActionView(customLayout);

        homeBinding.drawerNavView.setNavigationItemSelectedListener(this);

        if (homeBinding.drawerNavView.getHeaderCount() > 0) {
            // avoid NPE by first checking if there is at least one Header View available
            View headerLayout = homeBinding.drawerNavView.getHeaderView(0);
            drawerProfileImage=headerLayout.findViewById(R.id.drawer_profile_image);
            drawerUserName=headerLayout.findViewById(R.id.drawer_user_name);
            loadUserDetails();
        }

    }

    public void countMessages(){
        mFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("chat").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        messagesReceivedCounter=0;
                        for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if (queryDocumentSnapshot.exists()){
                                if (queryDocumentSnapshot.getString("receiverId").equals(firebaseAuth.getCurrentUser().getUid())
                                        && queryDocumentSnapshot.getBoolean("seen").equals(false)){
                                    messagesReceivedCounter++;
                                }
                            }
                        }
                        Log.v("messagesReceivedCounter, ",messagesReceivedCounter+"");
                        badge.setText(String.valueOf(messagesReceivedCounter));
                        Log.v("Badge, ",badge.getText().toString());
                        if (badge.getText().equals("0")){
                            badge.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserDetails() {
        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        StorageReference img= mStorageReference.child("usersPictures/"+firebaseAuth.getCurrentUser().getEmail()+"/profile");
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
                badge.setText("0");
                badge.setVisibility(View.GONE);
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
            case R.id.statics:
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment,new StatisticsFragment()).commit();
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