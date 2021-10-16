package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.databinding.ActivityAddDiscountBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AddDiscountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    ActivityAddDiscountBinding addDiscountBinding;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;
    private StorageTask mUploadTask;
    ;
    private Uri discountImage;
    String userEmail, userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addDiscountBinding = ActivityAddDiscountBinding.inflate(getLayoutInflater());
        setContentView(addDiscountBinding.getRoot());

        setSupportActionBar(addDiscountBinding.toolBar);
        addDiscountBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        userId = intent.getStringExtra("userId");

        addDiscountBinding.discountFrameLayout.setOnClickListener(v -> {
            openFileChooser();

        });

        addDiscountBinding.uploadDiscountBtn.setOnClickListener(v -> {
            uploadCertificate();
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
            discountImage = data.getData();
            addDiscountBinding.discountImage.setImageURI(discountImage);
            //Glide.with(this).load(profileImage).fitCenter().into(signUpBinding.profileImage);
            Picasso.with(this).load(discountImage).fit().centerCrop().into(addDiscountBinding.discountImage);
            addDiscountBinding.addDiscountTxt.setVisibility(View.GONE);
        }
    }

    private void uploadCertificate() {
        loading(true);
        if (discountImage != null) {
            StorageReference discountReference = mStorageReference.child("usersPictures/" + userEmail + "/discount");
            mUploadTask = discountReference.putFile(discountImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        discountReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("discount", uri.toString());

                                    DocumentReference documentReference = mFirestore.collection("users").document(userId);
                                    documentReference.update(user)
                                            .addOnSuccessListener(unused -> {
                                                loading(false);
                                                addDiscountBinding.uploadDiscountBtn.setEnabled(false);
                                                Toast.makeText(this,
                                                        "Discount uploaded successfully..", Toast.LENGTH_LONG).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(command -> loading(true));
        } else {
            Toast.makeText(this, "No discount selected!", Toast.LENGTH_SHORT).show();
            loading(false);
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            addDiscountBinding.progressBar.setVisibility(View.VISIBLE);
            addDiscountBinding.uploadDiscountBtn.setVisibility(View.INVISIBLE);
        } else {
            addDiscountBinding.progressBar.setVisibility(View.INVISIBLE);
            addDiscountBinding.uploadDiscountBtn.setVisibility(View.VISIBLE);
        }
    }
}