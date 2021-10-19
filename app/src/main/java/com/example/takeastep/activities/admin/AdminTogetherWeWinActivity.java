package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.R;
import com.example.takeastep.activities.admin.adapters.AdminVaccineTypesAdapter;
import com.example.takeastep.databinding.ActivityAdminTogetherWeWinBinding;
import com.example.takeastep.models.Vaccine;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AdminTogetherWeWinActivity extends AppCompatActivity {

    ArrayList<Vaccine> mVaccine;
    ArrayList<Vaccine> tempVaccine = new ArrayList<>();
    AdminVaccineTypesAdapter vaccineAdapter;
    ActivityAdminTogetherWeWinBinding adminTogetherWeWinBinding;
    Vaccine selectedVaccine = new Vaccine();
    private ArrayList<String> mTypes = new ArrayList<>();

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mFirebaseStorage;
    private CollectionReference mCollectionReference;


    boolean isVaccineExists;
    Chip chip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminTogetherWeWinBinding = ActivityAdminTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(adminTogetherWeWinBinding.getRoot());

        setSupportActionBar(adminTogetherWeWinBinding.toolBar);
        adminTogetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mCollectionReference = mFirestore.collection("Vaccines");

        adminTogetherWeWinBinding.vaccineTypeRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setLayoutManager(linearLayoutManager);

        mVaccine = new ArrayList<>();
        loadTypes();
        loadAllVaccines();

        vaccineAdapter = new AdminVaccineTypesAdapter(AdminTogetherWeWinActivity.this, tempVaccine);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setAdapter(vaccineAdapter);
        vaccineAdapter.setOnItemClickListener(this::menuClick);

        adminTogetherWeWinBinding.addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AddVaccineActivity.class));
        });

        adminTogetherWeWinBinding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                adminTogetherWeWinBinding.chipGroup.check(R.id.chip_all);
            } else if (checkedId == R.id.chip_all) {
                tempVaccine.clear();
                tempVaccine.addAll(mVaccine);
                vaccineAdapter.notifyDataSetChanged();
            } else {
                tempVaccine.clear();
                Chip chip = group.findViewById(checkedId);
                for (int i = 0; i < mVaccine.size(); i++) {
                    if (mVaccine.get(i).getName().equals(chip.getText().toString().trim())) {
                        tempVaccine.add(mVaccine.get(i));
                    }
                }
                vaccineAdapter.notifyDataSetChanged();
            }
        });
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
                        chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, adminTogetherWeWinBinding.chipGroup, false);
                        chip.setText(mTypes.get(i));
                        chip.setId(i);
                        adminTogetherWeWinBinding.chipGroup.addView(chip);
                    }


                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading types!", Toast.LENGTH_SHORT).show());
    }

    private void loadAllVaccines() {
        mCollectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mVaccine.clear();
                        tempVaccine.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (queryDocumentSnapshot.exists()) {
                                queryDocumentSnapshot.getReference().collection("posts").get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {

                                                adminTogetherWeWinBinding.errorText.setVisibility(View.GONE);
                                                for (QueryDocumentSnapshot queryDocumentSnapshot1 : task1.getResult()) {
                                                    if (queryDocumentSnapshot1.exists()) {
                                                        Vaccine vaccine = queryDocumentSnapshot1.toObject(Vaccine.class);
                                                        mVaccine.add(vaccine);
                                                        tempVaccine.add(vaccine);
                                                    }
                                                }
                                                Log.v("vaccine size, ", mVaccine.size() + "");
                                                if (mVaccine.size() == 0) {
                                                    showTextMessage();
                                                }

                                                vaccineAdapter.notifyDataSetChanged();

                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void menuClick(View view, int position) {
        selectedVaccine = mVaccine.get(position);
        PopupMenu popup = new PopupMenu(AdminTogetherWeWinActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    Intent updateIntent = new Intent(getApplicationContext(), EditVaccineActivity.class);
                    String name = selectedVaccine.getName();
                    String info = selectedVaccine.getInfo();
                    String image = selectedVaccine.getImage();

                    updateIntent.putExtra("name", name);
                    updateIntent.putExtra("info", info);
                    updateIntent.putExtra("image", image);
                    startActivity(updateIntent);

                    return true;
                case R.id.delete:
                    mCollectionReference.document(selectedVaccine.getName()).collection("posts").get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (documentSnapshot.getString("info").equals(selectedVaccine.getInfo())
                                            && documentSnapshot.getString("image").equals(selectedVaccine.getImage())) {
                                        documentSnapshot.getReference().delete()
                                                .addOnSuccessListener(unused -> {
                                                    mVaccine.remove(position);
                                                    vaccineAdapter.notifyItemRemoved(position);
                                                    Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                    return false;

            }
            return false;
        });
        popup.show();
    }

    private void showTextMessage() {
        adminTogetherWeWinBinding.errorText.setVisibility(View.VISIBLE);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setVisibility(View.GONE);
    }


}