package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        setupBadge();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_dashboard_menu, menu);
        final MenuItem menuItem=menu.findItem(R.id.notification);
        View actionView=menuItem.getActionView();
        chatsItemCount=(TextView) actionView.findViewById(R.id.text_badge);

        setupBadge();

        actionView.setOnClickListener(v -> onOptionsItemSelected(menuItem));
        return super.onCreateOptionsMenu(menu);
    }

    private void setupBadge() {
        chatsCount=0;
        if (chatsItemCount != null) {
            mRef=mFirestore.collection("users");
            mRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                if (queryDocumentSnapshot.exists()){
                                    queryDocumentSnapshot.getReference().collection("chat").get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()){
                                                    for (QueryDocumentSnapshot queryDocumentSnapshot1:task1.getResult()){
                                                        if (queryDocumentSnapshot1.exists()){
                                                            if (queryDocumentSnapshot1.getString("receiverId").equals("ALQyPwPRatn1H3oGIaOo")
                                                                    && queryDocumentSnapshot1.getBoolean("seen").equals(false)){
                                                                chatsCount++;
                                                            }
                                                        }
                                                    }
                                                    if (chatsCount == 0) {
                                                        chatsItemCount.setVisibility(View.GONE);
                                                    } else {
                                                        chatsItemCount.setVisibility(View.VISIBLE);
                                                        chatsItemCount.setText(String.valueOf(chatsCount));
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());



//            mRef = mFirestore.collection("users").document().collection("chat");
//            mRef.get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
//                                if (queryDocumentSnapshot.exists()){
//                                    if (queryDocumentSnapshot.getString("receiverId").equals("ALQyPwPRatn1H3oGIaOo")
//                                            && queryDocumentSnapshot.getBoolean("seen").equals(false)){
//                                        chatsCount++;
//                                    }
//                                }
//                            }
//                            if (chatsCount == 0) {
//                                chatsItemCount.setVisibility(View.GONE);
//                            } else {
//                                chatsItemCount.setVisibility(View.VISIBLE);
//                                chatsItemCount.setText(String.valueOf(chatsCount));
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
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

    @Override
    protected void onStart() {
        super.onStart();
        setupBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
    }
}