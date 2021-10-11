package com.example.takeastep.activities.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityCovidDiscountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CovidDiscountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    ActivityCovidDiscountBinding covidDiscountBinding;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private StorageTask mUploadTask;
    private DocumentReference mDocumentReference;
    private CollectionReference mCollectionReference;
    private Uri certificateImage;


    boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        covidDiscountBinding = ActivityCovidDiscountBinding.inflate(getLayoutInflater());
        setContentView(covidDiscountBinding.getRoot());
        setSupportActionBar(covidDiscountBinding.toolBar);
        covidDiscountBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDocumentReference = mFirestore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid());

        covidDiscountBinding.certificateFrameLayout.setOnClickListener(v -> {
            openFileChooser();

        });

        covidDiscountBinding.uploadCertificateBtn.setOnClickListener(v -> {
            uploadCertificate();
        });
        checkValidation();

    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CovidDiscountActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.discount_layout, null);

        builder.setView(dialogLayout);
        builder.setTitle("Congratulations");

        ImageView discountImg = dialogLayout.findViewById(R.id.discount_img);

        Random random = new Random();
        int randInt = random.nextInt(10) + 1;
        String imgName = "discount" + randInt;
        Uri img = Uri.parse("android.resource://com.example.takeastep/drawable/" + imgName);
        Glide.with(getApplicationContext()).load(img).centerInside().into(discountImg);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void checkValidation() {
        mDocumentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    isValid = documentSnapshot.getBoolean("validCertificate") != null;
                    if (isValid) {
                        Toast.makeText(this, "Congratulations. You are vaccinated", Toast.LENGTH_SHORT).show();
                        covidDiscountBinding.certificateFrameLayout.setEnabled(false);
                        covidDiscountBinding.uploadCertificateBtn.setEnabled(false);
                        loading(false);

                        showAlert();
                    } else {
                        loading(false);
                        Toast.makeText(this, "Waiting for admin confirmation", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            certificateImage = data.getData();
            covidDiscountBinding.certificateImage.setImageURI(certificateImage);
            //Glide.with(this).load(profileImage).fitCenter().into(signUpBinding.profileImage);
            Picasso.with(this).load(certificateImage).fit().centerCrop().into(covidDiscountBinding.certificateImage);
            covidDiscountBinding.addCertificateTxt.setVisibility(View.GONE);
        }
    }

    private void uploadCertificate() {
        loading(true);
        //covidDiscountBinding.uploadCertificateBtn.setEnabled(false);
        if (certificateImage != null) {
            StorageReference certificateReference = mStorageReference.child("usersPictures/" + mFirebaseAuth.getCurrentUser().getEmail() + "/" + "certificate");
            mUploadTask = certificateReference.putFile(certificateImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        certificateReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("certificate", uri.toString());

                                    DocumentReference documentReference = mFirestore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid());
                                    documentReference.update(user)
                                            .addOnSuccessListener(unused -> {
                                                loading(false);
                                                covidDiscountBinding.uploadCertificateBtn.setEnabled(false);
                                                Toast.makeText(this,
                                                        "Certificate uploaded successfully..Wait for admin response", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            loading(false);
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            covidDiscountBinding.progressBar.setVisibility(View.VISIBLE);
            covidDiscountBinding.uploadCertificateBtn.setVisibility(View.INVISIBLE);
        } else {
            covidDiscountBinding.progressBar.setVisibility(View.INVISIBLE);
            covidDiscountBinding.uploadCertificateBtn.setVisibility(View.VISIBLE);
        }
    }
}