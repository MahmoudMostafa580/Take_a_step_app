package com.example.takeastep.activities.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.activities.user.adapters.TogetherWeWinAdapter;
import com.example.takeastep.databinding.ActivityTogetherWeWinBinding;
import com.example.takeastep.models.Vaccine;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class TogetherWeWinActivity extends AppCompatActivity {
    ActivityTogetherWeWinBinding togetherWeWinBinding;
    ArrayList<Vaccine> mVaccine;
    TogetherWeWinAdapter togetherWeWinAdapter;

    private FirebaseFirestore mFirestore;
    private ArrayList<String> mTypes;
    private CollectionReference mCollectionReference;

    int chipId;
    Chip chip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        togetherWeWinBinding = ActivityTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(togetherWeWinBinding.getRoot());

        setSupportActionBar(togetherWeWinBinding.toolBar);
        togetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Vaccines");

        togetherWeWinBinding.togetherWeWinRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        togetherWeWinBinding.togetherWeWinRecycler.setLayoutManager(linearLayoutManager);

        mVaccine = new ArrayList<>();
        mTypes = new ArrayList<>();

        togetherWeWinAdapter = new TogetherWeWinAdapter(mVaccine, TogetherWeWinActivity.this);
        togetherWeWinBinding.togetherWeWinRecycler.setAdapter(togetherWeWinAdapter);

        loadTypes();
        loadAllVaccines();

    }

    void loadTypes() {
        mFirestore.collection("Vaccines").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mTypes.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            mTypes.add(name);
                        }
                    }

                    for (int i = 0; i < mTypes.size(); i++) {
                        chip = new Chip(this);
                        chip.setText(mTypes.get(i));
                        chip.setCheckable(true);
                        chip.setId(i);
                        togetherWeWinBinding.chipGroup.addView(chip, i);



                        chip.setOnClickListener(v -> {
                            loadChipsVaccines();
                        });
                    }

                    chip.setOnClickListener(v -> loadChipsVaccines());

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading types!", Toast.LENGTH_SHORT).show());
    }

    private void loadAllVaccines() {
        mCollectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (queryDocumentSnapshot.exists()) {
                                queryDocumentSnapshot.getReference().collection("posts").get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                mVaccine.clear();
                                                for (QueryDocumentSnapshot queryDocumentSnapshot1 : task1.getResult()) {
                                                    if (queryDocumentSnapshot1.exists()) {
                                                        Vaccine vaccine = queryDocumentSnapshot1.toObject(Vaccine.class);
                                                        mVaccine.add(vaccine);
                                                    }
                                                }
                                                Log.v("vaccine size, ", mVaccine.size() + "");
                                                if (mVaccine.size() == 0) {
                                                    showTextMessage();
                                                }
                                                togetherWeWinAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void loadChipsVaccines() {
        mCollectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (queryDocumentSnapshot.exists()) {
                                chipId = togetherWeWinBinding.chipGroup.getCheckedChipId();
                                if (chipId==-1){
                                    loadAllVaccines();
                                }else{
                                    if (queryDocumentSnapshot.getString("name").equals(mTypes.get(chipId))) {
                                        queryDocumentSnapshot.getReference().collection("posts").get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        mVaccine.clear();
                                                        for (QueryDocumentSnapshot queryDocumentSnapshot1 : task1.getResult()) {
                                                            if (queryDocumentSnapshot1.exists()) {
                                                                Vaccine vaccine = queryDocumentSnapshot1.toObject(Vaccine.class);
                                                                mVaccine.add(vaccine);
                                                            }
                                                        }
                                                        Log.v("vaccine size, ", mVaccine.size() + "");
                                                        if (mVaccine.size() == 0) {
                                                            showTextMessage();
                                                        }
                                                        togetherWeWinAdapter.notifyDataSetChanged();
                                                    }
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(this, "No posts available!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showTextMessage() {
        togetherWeWinBinding.text.setVisibility(View.VISIBLE);
        togetherWeWinBinding.togetherWeWinRecycler.setVisibility(View.GONE);
    }
}