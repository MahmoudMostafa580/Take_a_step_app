package com.example.takeastep.activities.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.takeastep.databinding.ActivityAddContentBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

public class AddContentActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    ActivityAddContentBinding addContentBinding;
    String[] categories = {"Vaccination Benefits", "Vaccination Risks"};
    String mCategory;

    FirebaseFirestore mFirestore;
    StorageReference mStorageReference;
    private StorageTask mUploadTask;
    private Uri imageUri,videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentBinding = ActivityAddContentBinding.inflate(getLayoutInflater());
        setContentView(addContentBinding.getRoot());

        setSupportActionBar(addContentBinding.toolBar);
        addContentBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        prepareSpinner();

        addContentBinding.contentImage.setOnClickListener(v -> openImageChooser());
        addContentBinding.contentVideo.setOnClickListener(v -> openVideoChooser());

        addContentBinding.uploadBtn.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(AddContentActivity.this, "Upload in progress...", Toast.LENGTH_SHORT).show();
            } else {
                uploadContent();
            }
        });
    }

    private void prepareSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, categories);
        addContentBinding.categorySpinner.setAdapter(spinnerAdapter);
        addContentBinding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddContentActivity.this, "No category selected !", Toast.LENGTH_SHORT).show();
            }
        });
        addContentBinding.categorySpinner.setOnDismissListener(() -> addContentBinding.categorySpinner.clearFocus());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void openVideoChooser(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST &&resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            addContentBinding.contentImage.setImageURI(imageUri);
            Glide.with(this).load(imageUri).fitCenter().into(addContentBinding.contentImage);
            addContentBinding.addImageTxt.setVisibility(View.GONE);
        }else if (requestCode==PICK_VIDEO_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            videoUri=data.getData();
            addContentBinding.contentVideo.setVideoURI(videoUri);
            //addContentBinding.contentVideo.canPause();
            addContentBinding.contentVideo.start();
            addContentBinding.addVideoTxt.setVisibility(View.GONE);
        }
    }

    private void uploadContent() {
        if (checkValidate()) {
            String caption = Objects.requireNonNull(addContentBinding.captionLayout.getEditText()).getText().toString();
            String category = addContentBinding.categorySpinner.getText().toString();
            StorageReference contentReference = mStorageReference.child("ReadyContent/" + caption);
            if (imageUri == null) {
                mUploadTask = contentReference.putFile(videoUri)
                        .addOnSuccessListener(taskSnapshot -> contentReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    ReadyContent content = new ReadyContent(null,uri.toString(), caption, category,new Date().toString());
                                    DocumentReference documentReference = mFirestore.collection("Ready Content").document(content.getCaption());
                                    documentReference.set(content)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AddContentActivity.this,AdminAreYouReadyActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnProgressListener(snapshot -> Toast.makeText(this, "Upload in progress...", Toast.LENGTH_SHORT).show());

            } else if (videoUri == null) {
                mUploadTask = contentReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> contentReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    ReadyContent content = new ReadyContent(uri.toString(),null, caption, category,new Date().toString());
                                    DocumentReference documentReference = mFirestore.collection("Ready Content").document(content.getCaption());
                                    documentReference.set(content)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(AddContentActivity.this,AdminAreYouReadyActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }else{
                Toast.makeText(this, "No File Selected!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please fill fields!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkValidate() {
        String caption = addContentBinding.captionLayout.getEditText().getText().toString();
        String category = addContentBinding.categorySpinner.getText().toString();
        if (imageUri != null || videoUri != null) {
            if (!caption.isEmpty() && !category.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}