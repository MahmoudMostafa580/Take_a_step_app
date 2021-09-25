package com.example.takeastep.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.takeastep.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding signInBinding;
    FirebaseAuth firebaseAuth;

    SharedPreferences mySharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mySharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor = mySharedPreferences.edit();

        signInBinding.createNewAccountTxt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        signInBinding.signInBtn.setOnClickListener(v -> {
            if (isValidData()){
                signIn();
            }
        });

    }

    private void signIn() {
        loading(true);
        String email = signInBinding.emailLayout.getEditText().getText().toString();
        String pasword = signInBinding.passLayout.getEditText().getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email, pasword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loading(false);
                        if (Objects.equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail(), "admin@admin.com")) {
                            startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class));
                            editor.putBoolean("isLogged", true);
                            editor.putBoolean("isAdmin", true);
                            editor.putBoolean("isUser", false);
                        } else {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            editor.putBoolean("isLogged", true);
                            editor.putBoolean("isAdmin", false);
                            editor.putBoolean("isUser", true);
                        }
                        editor.apply();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    showToast(e.getMessage());
                });
    }

    private boolean isValidData() {
        if (signInBinding.emailLayout.getEditText().getText().toString().trim().isEmpty()) {
            signInBinding.emailLayout.setError("Please enter your mail!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(signInBinding.emailLayout.getEditText().getText().toString()).matches()) {
            signInBinding.emailLayout.setError("Enter valid mail!");
            return false;
        } else if (signInBinding.passLayout.getEditText().getText().toString().trim().isEmpty()) {
            signInBinding.passLayout.setError("Please enter password!");
            return false;
        } else {
            return true;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            signInBinding.signInProgressBar.setVisibility(View.VISIBLE);
            signInBinding.signInBtn.setVisibility(View.INVISIBLE);
        } else {
            signInBinding.signInProgressBar.setVisibility(View.INVISIBLE);
            signInBinding.signInBtn.setVisibility(View.VISIBLE);
        }
    }
}