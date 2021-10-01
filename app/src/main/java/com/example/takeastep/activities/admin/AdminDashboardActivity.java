package com.example.takeastep.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.takeastep.R;
import com.example.takeastep.activities.SignInActivity;
import com.example.takeastep.databinding.ActivityAdminDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {
    ActivityAdminDashboardBinding adminDashboardBinding;
    FirebaseAuth firebaseAuth;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminDashboardBinding=ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(adminDashboardBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mySharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor = mySharedPreferences.edit();

        setSupportActionBar(adminDashboardBinding.toolBar);
        adminDashboardBinding.areYouReadyCard.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                AdminAreYouReadyActivity.class)));
        adminDashboardBinding.togetherWeWinCard.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                AdminTogetherWeWinActivity.class)));
        adminDashboardBinding.covidDiscountCard.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                AdminCovidDiscountActivity.class)));
        adminDashboardBinding.takeAstepCard.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),
                AdminTakeAStepActivity.class)));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.admin_dashboard_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            signOut();
        }else if (item.getItemId()==R.id.notification){

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        editor.putBoolean("isLogged",false);
        editor.apply();
        finish();
    }
}