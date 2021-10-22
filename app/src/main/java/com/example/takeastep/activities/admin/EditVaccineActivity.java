package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.takeastep.databinding.ActivityAddVaccineBinding;
import com.example.takeastep.models.Vaccine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditVaccineActivity extends AppCompatActivity {
    ActivityAddVaccineBinding addVaccineBinding;
    private static final int PICK_IMAGE_REQUEST = 1;

    FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;
    StorageReference mStorageReference;

    ArrayList<String> mVaccines;
    String vaccine;
    ArrayAdapter<String> spinnerAdapter;

    Vaccine myVaccine = new Vaccine();

    private StorageTask mUploadTask;
    private Uri imageUri;

    String name, info, image;
    long time;
    boolean isVaccineExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addVaccineBinding = ActivityAddVaccineBinding.inflate(getLayoutInflater());
        setContentView(addVaccineBinding.getRoot());

        setSupportActionBar(addVaccineBinding.toolBar);
        addVaccineBinding.toolBar.setTitle("Edit vaccine");
        addVaccineBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("TogetherWeWin");
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mVaccines = new ArrayList<>();
        loadTypes();
        prepareSpinner();

        addVaccineBinding.imageFrameLayout.setOnClickListener(v -> openFileChooser());

        Intent updateIntent = getIntent();
        info = updateIntent.getStringExtra("info");
        name = updateIntent.getStringExtra("name");
        image = updateIntent.getStringExtra("image");
        time = updateIntent.getLongExtra("time", time);

        myVaccine.setName(name);
        myVaccine.setInfo(info);
        myVaccine.setImage(image);
        myVaccine.setTime(time);

        Glide.with(this).load(Uri.parse(image)).into(addVaccineBinding.vaccineImg);
        addVaccineBinding.addVaccineTxt.setVisibility(View.GONE);

        addVaccineBinding.vaccinesSpinner.setText(name);
        addVaccineBinding.infoLayout.getEditText().setText(info);

        addVaccineBinding.uploadBtn.setText("Update");
        addVaccineBinding.uploadBtn.setOnClickListener(v -> {
            loading(true);
            if (checkValidate()) {
                info = Objects.requireNonNull(addVaccineBinding.infoLayout.getEditText()).getText().toString();
                name = addVaccineBinding.vaccinesSpinner.getText().toString();
                StorageReference postReference = mStorageReference.child("Vaccines/" + time);

                if (imageUri == null) {
                    Map<String, Object> update = new HashMap<>();
                    update.put("info", info);
                    update.put("name", name);
                    mCollectionReference.document(myVaccine.getTime().toString()).update(update)
                            .addOnSuccessListener(unused -> {
                                loading(false);
                                Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                loading(false);
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                } else {
                    mUploadTask = postReference.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                postReference.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Map<String, Object> update = new HashMap<>();
                                            update.put("info", info);
                                            update.put("name", name);
                                            update.put("image", uri.toString());
                                            mCollectionReference.document(myVaccine.getTime().toString()).update(update)
                                                    .addOnSuccessListener(unused -> {
                                                        loading(false);
                                                        Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        loading(false);
                                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> {
                                loading(false);
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnProgressListener(command -> loading(true));
                }

                mFirestore.collection("Vaccines").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                isVaccineExists = name.equals(documentSnapshot.getId());
                                if (isVaccineExists)
                                    break;
                            }

                            if (isVaccineExists) {
                                Toast.makeText(this, "This vaccine already exist", Toast.LENGTH_SHORT).show();
                            } else {
                                DocumentReference documentReference = mFirestore.collection("Vaccines").document(name);
                                Map<Object, String> vaccine = new HashMap<>();
                                vaccine.put("name", name);
                                documentReference.set(vaccine)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Vaccine added successfully", Toast.LENGTH_SHORT).show();
                                            mVaccines.add(name);
                                            spinnerAdapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
            else {
                loading(false);
                Toast.makeText(this, "Please fill fields!", Toast.LENGTH_SHORT).show();
            }
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
            imageUri = data.getData();
            addVaccineBinding.vaccineImg.setImageURI(imageUri);
            Picasso.with(this).load(imageUri).into(addVaccineBinding.vaccineImg);
            addVaccineBinding.addVaccineTxt.setVisibility(View.GONE);
        }else{
            addVaccineBinding.vaccineImg.setImageURI(Uri.parse(image));
            Glide.with(this).load(Uri.parse(image)).into(addVaccineBinding.vaccineImg);
            addVaccineBinding.addVaccineTxt.setVisibility(View.GONE);
        }
    }

    private void prepareSpinner() {
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, mVaccines);
        addVaccineBinding.vaccinesSpinner.setAdapter(spinnerAdapter);
        addVaccineBinding.vaccinesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vaccine = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditVaccineActivity.this, "No vaccine selected !", Toast.LENGTH_SHORT).show();
            }
        });
        addVaccineBinding.vaccinesSpinner.setOnDismissListener(() -> addVaccineBinding.vaccinesSpinner.clearFocus());
    }

    private void loadTypes() {
        mFirestore.collection("Vaccines").get()
                .addOnCompleteListener(task -> {
                    mVaccines.clear();
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        if (queryDocumentSnapshot.exists()) {
                            String name = queryDocumentSnapshot.getString("name");
                            mVaccines.add(name);
                        } else {
                            Toast.makeText(this, "No Vaccines", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.v("list Size, ", mVaccines.size() + "");
                    spinnerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean checkValidate() {
        String info = addVaccineBinding.infoLayout.getEditText().getText().toString();
        String name = addVaccineBinding.vaccinesSpinner.getText().toString();
        if (image != null) {
            if (!info.isEmpty() && !name.isEmpty()) {
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

    private void loading(boolean isLoading) {
        if (isLoading) {
            addVaccineBinding.progress.setVisibility(View.VISIBLE);
            addVaccineBinding.uploadBtn.setVisibility(View.INVISIBLE);
        } else {
            addVaccineBinding.progress.setVisibility(View.INVISIBLE);
            addVaccineBinding.uploadBtn.setVisibility(View.VISIBLE);
        }
    }
}