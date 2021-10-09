package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.experimental.UseExperimental;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.R;
import com.example.takeastep.activities.SignInActivity;
import com.example.takeastep.databinding.ActivityAdminDashboardBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AdminDashboardActivity extends AppCompatActivity {
    ActivityAdminDashboardBinding adminDashboardBinding;
    FirebaseAuth firebaseAuth;
    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;


    TextView chatsItemCount;
    int chatsCount = 0;

    CollectionReference mRef;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminDashboardBinding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(adminDashboardBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
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

        //setupBadge();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_dashboard_menu, menu);

        //setupBadge();

        return super.onCreateOptionsMenu(menu);
    }

    private void setupBadge() {
        BadgeDrawable badge = BadgeDrawable.create(getApplicationContext());
        //BadgeUtils.attachBadgeDrawable(badge, new FrameLayout(getApplicationContext()));
        badge.setBadgeGravity(BadgeDrawable.TOP_END);
        badge.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        badge.setBadgeTextColor(getResources().getColor(R.color.white));
        badge.setMaxCharacterCount(99);

        if (chatsItemCount != null) {
            mRef = mFirestore.collection("chat users");
            mRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                chatsCount++;
                            }
                            if (chatsCount == 0) {
                                badge.setVisible(false);
                            } else {
                                badge.setVisible(true);
                                badge.setNumber(chatsCount);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            signOut();
        } else if (item.getItemId() == R.id.notification) {
            startActivity(new Intent(getApplicationContext(), AdminHelpCenterActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        editor.putBoolean("isLogged", false);
        editor.apply();
        finish();
    }
}