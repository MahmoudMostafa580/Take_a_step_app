package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.takeastep.databinding.ActivityAddVaccineBinding;
import com.example.takeastep.models.Vaccine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditVaccineActivity extends AppCompatActivity {
    ActivityAddVaccineBinding addVaccineBinding;
    FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;

    ArrayList<String> mVaccines;
    String vaccine;
    ArrayAdapter<String> spinnerAdapter;

    Vaccine myVaccine = new Vaccine();

    String name, info, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addVaccineBinding = ActivityAddVaccineBinding.inflate(getLayoutInflater());
        setContentView(addVaccineBinding.getRoot());

        setSupportActionBar(addVaccineBinding.toolBar);
        addVaccineBinding.toolBar.setTitle("Edit vaccine");
        addVaccineBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Vaccines");
        mVaccines = new ArrayList<>();
        loadTypes();
        prepareSpinner();

        Intent updateIntent = getIntent();
        info = updateIntent.getStringExtra("info");
        name = updateIntent.getStringExtra("name");
        image = updateIntent.getStringExtra("image");

        myVaccine.setName(name);
        myVaccine.setInfo(info);
        myVaccine.setImage(image);

        Glide.with(this).load(Uri.parse(image)).into(addVaccineBinding.vaccineImg);
        addVaccineBinding.addVaccineTxt.setVisibility(View.GONE);
        addVaccineBinding.vaccinesSpinner.setText(name);
        addVaccineBinding.infoLayout.getEditText().setText(info);
        addVaccineBinding.chooseVaccineLayout.setEnabled(false);
        addVaccineBinding.uploadBtn.setText("Update");
        addVaccineBinding.uploadBtn.setOnClickListener(v -> {
            if (checkValidate()) {
                info = Objects.requireNonNull(addVaccineBinding.infoLayout.getEditText()).getText().toString();
                name = addVaccineBinding.vaccinesSpinner.getText().toString();

                Map<String, Object> update = new HashMap<>();
                update.put("info", info);
                update.put("info", name);

                mCollectionReference.document(myVaccine.getName()).collection("posts").get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.getString("info").equals(myVaccine.getInfo()) &&
                                        documentSnapshot.getString("image").equals(myVaccine.getImage())) {
                                    documentSnapshot.getReference().update(update)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(EditVaccineActivity.this, AdminTogetherWeWinActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

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
                            Log.w("name, ", queryDocumentSnapshot.getString("name"));

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
}