package com.example.takeastep.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.activities.user.MainActivity;
import com.example.takeastep.databinding.ActivitySignUpBinding;
import com.example.takeastep.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivitySignUpBinding signUpBinding;
    private Uri profileImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;
    private StorageTask mUploadTask;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        signUpBinding.signInTxt.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignInActivity.class)));

        signUpBinding.imageFrameLayout.setOnClickListener(v -> {
            openFileChooser();
        });

        signUpBinding.signUpBtn.setOnClickListener(v -> {
            if (isValidData()) {
                signUp();
            } else {
                loading(false);
            }
        });
    }

    private void signUp() {
        loading(true);
        String name = signUpBinding.nameLayout.getEditText().getText().toString();
        String email = signUpBinding.emailLayout.getEditText().getText().toString();
        String password = signUpBinding.passLayout.getEditText().getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StorageReference imageReference = mStorageReference.child("usersPictures/" + firebaseAuth.getCurrentUser().getEmail() + "/profile");
                        mUploadTask = imageReference.putFile(profileImage)
                                .addOnSuccessListener(taskSnapshot -> {
                                    imageReference.getDownloadUrl()
                                            .addOnSuccessListener(uri -> {
                                                User user = new User(name, uri.toString(), email, firebaseAuth.getCurrentUser().getUid());
                                                DocumentReference documentReference = mFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
                                                documentReference.set(user)
                                                        .addOnSuccessListener(unused -> {
                                                            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(name)
                                                                    .setPhotoUri(uri)
                                                                    .build();
                                                            mFirebaseUser.updateProfile(profileChangeRequest);
                                                            showToast("Account created successfully");
                                                            editor.putBoolean("isLogged", true);
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading(false);
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileImage = data.getData();
            signUpBinding.profileImage.setImageURI(profileImage);
            //Glide.with(this).load(profileImage).fitCenter().into(signUpBinding.profileImage);
            Picasso.with(this).load(profileImage).fit().centerCrop().into(signUpBinding.profileImage);
            signUpBinding.addImageTxt.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidData() {
        if (profileImage == null) {
            showToast("Select profile image!");
            return false;
        } else if (signUpBinding.nameLayout.getEditText().getText().toString().trim().isEmpty()) {
            signUpBinding.nameLayout.setError("Please enter your name!");
            return false;
        } else if (signUpBinding.emailLayout.getEditText().getText().toString().trim().isEmpty()) {
            signUpBinding.emailLayout.setError("Please enter your mail!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(signUpBinding.emailLayout.getEditText().getText().toString()).matches()) {
            signUpBinding.emailLayout.setError("Enter valid mail!");
            return false;
        } else if (signUpBinding.passLayout.getEditText().getText().toString().trim().isEmpty()) {
            signUpBinding.passLayout.getEditText().setError("Please enter password!");
            return false;
        }else if (signUpBinding.confirmPassLayout.getEditText().getText().toString().trim().isEmpty()) {
            signUpBinding.confirmPassLayout.getEditText().setError("Please confirm password!");
            return false;
        } else if (!signUpBinding.passLayout.getEditText().getText().toString()
                .equals(signUpBinding.confirmPassLayout.getEditText().getText().toString())) {
            showToast("Password and confirm password must be the same");
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            signUpBinding.signUpProgressBar.setVisibility(View.VISIBLE);
            signUpBinding.signUpBtn.setVisibility(View.INVISIBLE);
        } else {
            signUpBinding.signUpProgressBar.setVisibility(View.INVISIBLE);
            signUpBinding.signUpBtn.setVisibility(View.VISIBLE);
        }
    }
}