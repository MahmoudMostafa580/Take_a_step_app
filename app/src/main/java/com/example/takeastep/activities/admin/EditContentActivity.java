package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.databinding.ActivityAddContentBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditContentActivity extends AppCompatActivity {
    ActivityAddContentBinding addContentBinding;

    String[] categories = {"Vaccination Benefits", "Vaccination Risks"};
    String mCategory;

    FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;

    ReadyContent content = new ReadyContent();
    String caption, category, videoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentBinding = ActivityAddContentBinding.inflate(getLayoutInflater());
        setContentView(addContentBinding.getRoot());

        setSupportActionBar(addContentBinding.toolBar);
        addContentBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Ready Content");

        prepareSpinner();

        Intent updateIntent = getIntent();
        caption = updateIntent.getStringExtra("caption");
        category = updateIntent.getStringExtra("category");
        videoUrl = updateIntent.getStringExtra("videoUrl");


        content.setCaption(caption);
        content.setCategory(category);

        content.setVideoUrl(videoUrl);
        addContentBinding.contentVideo.setVideoURI(Uri.parse(videoUrl));
        addContentBinding.contentVideo.start();
        addContentBinding.addVideoTxt.setVisibility(View.GONE);

        addContentBinding.captionLayout.getEditText().setText(caption);
        addContentBinding.categorySpinner.setText(category);
        addContentBinding.captionLayout.setEnabled(false);

        addContentBinding.uploadBtn.setText("Update");
        addContentBinding.uploadBtn.setOnClickListener(v -> {
            if (checkValidate()) {
                caption = Objects.requireNonNull(addContentBinding.captionLayout.getEditText()).getText().toString();
                category = addContentBinding.categorySpinner.getText().toString();

                Map<String, Object> update = new HashMap<>();
                update.put("caption", caption);
                update.put("category", category);

                mCollectionReference.document(content.getCaption()).update(update)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Content updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditContentActivity.this, AdminAreYouReadyActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
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
                addContentBinding.categorySpinner.setText(mCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditContentActivity.this, "No category selected !", Toast.LENGTH_SHORT).show();
            }
        });
        addContentBinding.categorySpinner.setOnDismissListener(() -> addContentBinding.categorySpinner.clearFocus());
    }

    private boolean checkValidate() {
        String caption = addContentBinding.captionLayout.getEditText().getText().toString();
        String category = addContentBinding.categorySpinner.getText().toString();
        if (videoUrl != null) {
            if (!caption.isEmpty() && !category.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareSpinner();
    }
}