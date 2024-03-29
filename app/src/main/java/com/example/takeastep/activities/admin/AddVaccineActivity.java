package com.example.takeastep.activities.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityAddVaccineBinding;
import com.example.takeastep.models.Vaccine;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
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

public class AddVaccineActivity extends AppCompatActivity {
    ActivityAddVaccineBinding addVaccineBinding;

    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;
    private StorageTask mUploadTask;

    ArrayList<String> mVaccines;
    String vaccine;
    ArrayAdapter<String> spinnerAdapter;
    Uri vaccineImage;

    boolean isVaccineExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addVaccineBinding=ActivityAddVaccineBinding.inflate(getLayoutInflater());
        setContentView(addVaccineBinding.getRoot());

        setSupportActionBar(addVaccineBinding.toolBar);
        addVaccineBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mVaccines=new ArrayList<>();
        loadTypes();
        prepareSpinner();

        addVaccineBinding.imageFrameLayout.setOnClickListener(v -> openFileChooser());

        addVaccineBinding.uploadBtn.setOnClickListener(v -> {
                uploadVaccine();
        });

    }

    private void loadTypes() {
        mFirestore.collection("Vaccines").get()
                .addOnCompleteListener(task -> {
                    mVaccines.clear();
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        if (queryDocumentSnapshot.exists()){
                            String name=queryDocumentSnapshot.getString("name");
                            Log.w("name, ",queryDocumentSnapshot.getString("name"));

                            mVaccines.add(name);

                        }else{
                            Toast.makeText(this, "No Vaccines", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.v("list Size, ",mVaccines.size()+"");
                    spinnerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadVaccine() {
        loading(true);
        if (checkValidate()){
            String info = Objects.requireNonNull(addVaccineBinding.infoLayout.getEditText()).getText().toString();
            String vaccineName = addVaccineBinding.vaccinesSpinner.getText().toString();
            long time = System.currentTimeMillis();
            StorageReference vaccineReference=mStorageReference.child("Vaccines/"+time);
            mUploadTask=vaccineReference.putFile(vaccineImage)
                    .addOnSuccessListener(taskSnapshot -> vaccineReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Vaccine post=new Vaccine(vaccineName,info,uri.toString(),time);
                                DocumentReference documentReference=mFirestore.collection("TogetherWeWin").document(String.valueOf(time));
                                documentReference.set(post)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, "Vaccine uploaded successfully", Toast.LENGTH_SHORT).show();
                                            loading(false);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(command -> {
                        loading(true);
                    });

            mFirestore.collection("Vaccines").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            isVaccineExists = vaccineName.equals(documentSnapshot.getId());
                            if (isVaccineExists)
                                break;
                        }

                        if (isVaccineExists) {
                            mFirestore.collection("Vaccines").document(vaccineName).update("numOfPosts", FieldValue.increment(1));
                            Toast.makeText(this, "This vaccine already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            DocumentReference documentReference = mFirestore.collection("Vaccines").document(vaccineName);
                            Map<String, Object> v = new HashMap<>();
                            v.put("name", vaccineName);
                            v.put("numOfPosts",1);
                            documentReference.set(v)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Vaccine added successfully", Toast.LENGTH_SHORT).show();
                                        mVaccines.add(vaccineName);
                                        spinnerAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }else {
            loading(false);
            Toast.makeText(this, "Please fill fields!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            vaccineImage = data.getData();
            addVaccineBinding.vaccineImg.setImageURI(vaccineImage);
            Picasso.with(this).load(vaccineImage).into(addVaccineBinding.vaccineImg);
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
                Toast.makeText(AddVaccineActivity.this, "No vaccine selected !", Toast.LENGTH_SHORT).show();
            }
        });
        addVaccineBinding.vaccinesSpinner.setOnDismissListener(() -> addVaccineBinding.vaccinesSpinner.clearFocus());
    }

    private boolean checkValidate() {
        String info = addVaccineBinding.infoLayout.getEditText().getText().toString();
        String vaccineName = addVaccineBinding.vaccinesSpinner.getText().toString();
        if (vaccineImage != null) {
            if (!info.isEmpty() && !vaccineName.isEmpty()) {
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
            addVaccineBinding.progress.setVisibility(View.VISIBLE);
            addVaccineBinding.uploadBtn.setVisibility(View.INVISIBLE);
        } else {
            addVaccineBinding.progress.setVisibility(View.INVISIBLE);
            addVaccineBinding.uploadBtn.setVisibility(View.VISIBLE);
        }
    }
}