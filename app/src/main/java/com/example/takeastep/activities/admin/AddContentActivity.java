package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.databinding.ActivityAddContentBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddContentActivity extends AppCompatActivity {
    private static final int PICK_VIDEO_REQUEST = 2;
    ActivityAddContentBinding addContentBinding;
    ArrayList<String> categories;
    String mCategory;
    ArrayAdapter<String> spinnerAdapter;

    FirebaseFirestore mFirestore;
    StorageReference mStorageReference;
    private StorageTask mUploadTask;
    private Uri videoUri;

    boolean isCategoryExists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentBinding = ActivityAddContentBinding.inflate(getLayoutInflater());
        setContentView(addContentBinding.getRoot());

        setSupportActionBar(addContentBinding.toolBar);
        addContentBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        categories = new ArrayList<>();
        loadCategories();
        prepareSpinner();
        addContentBinding.videoFrameLayout.setOnClickListener(v -> openVideoChooser());

        addContentBinding.uploadBtn.setOnClickListener(v -> {
            uploadContent();
        });
    }

    private void loadCategories() {
        mFirestore.collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categories.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            categories.add(name);
                        }
                    }
                    spinnerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void prepareSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, categories);
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

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            MediaController mc = new MediaController(this);
            mc.setAnchorView(addContentBinding.contentVideo);
            addContentBinding.contentVideo.setMediaController(mc);
            addContentBinding.contentVideo.setVideoURI(videoUri);
            addContentBinding.contentVideo.seekTo(1);
            //addContentBinding.contentVideo.start();
            addContentBinding.addVideoTxt.setVisibility(View.GONE);
            addContentBinding.videoFrameLayout.setClickable(false);
        }
    }

    private void uploadContent() {
        loading(true);
        if (checkValidate()) {
            String caption = Objects.requireNonNull(addContentBinding.captionLayout.getEditText()).getText().toString();
            String category = addContentBinding.categorySpinner.getText().toString();
            long time = System.currentTimeMillis();
            StorageReference contentReference = mStorageReference.child("ReadyContent/" + time);
            mUploadTask = contentReference.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> contentReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                ReadyContent content = new ReadyContent(uri.toString(), caption, category, time);
                                DocumentReference documentReference = mFirestore.collection("Ready Content").document(String.valueOf(time));
                                documentReference.set(content)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            loading(false);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(snapshot -> {
                        loading(true);
                    });

            mFirestore.collection("Categories").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            isCategoryExists = category.equals(documentSnapshot.getId());
                            if (isCategoryExists)
                                break;
                        }

                        if (isCategoryExists) {
                            mFirestore.collection("Categories").document(category).update("numOfVideos",FieldValue.increment(1));
                            Toast.makeText(this, "This category already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            DocumentReference documentReference = mFirestore.collection("Categories").document(category);
                            Map<String, Object> c = new HashMap<>();
                            c.put("name", category);
                            c.put("numOfVideos",1);
                            documentReference.set(c)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                                        categories.add(category);
                                        spinnerAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            loading(false);
            Toast.makeText(this, "Please fill fields!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkValidate() {
        String caption = addContentBinding.captionLayout.getEditText().getText().toString();
        String category = addContentBinding.categorySpinner.getText().toString();
        if (videoUri != null) {
            if (!caption.isEmpty() && !category.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            addContentBinding.progress.setVisibility(View.VISIBLE);
            addContentBinding.uploadBtn.setVisibility(View.INVISIBLE);
        } else {
            addContentBinding.progress.setVisibility(View.INVISIBLE);
            addContentBinding.uploadBtn.setVisibility(View.VISIBLE);
        }
    }


}