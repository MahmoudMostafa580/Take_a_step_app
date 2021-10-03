package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.adapters.AdminVaccineTypesAdapter;
import com.example.takeastep.databinding.ActivityAdminTogetherWeWinBinding;
import com.example.takeastep.models.Vaccine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminTogetherWeWinActivity extends AppCompatActivity{

    ArrayList<Vaccine> mVaccine;
    AdminVaccineTypesAdapter vaccineAdapter;
    ActivityAdminTogetherWeWinBinding adminTogetherWeWinBinding;
    Vaccine selectedVaccine=new Vaccine();

    private FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;

    TextInputLayout nameLayout;
    TextInputLayout infoLayout;
    MaterialButton uploadBtn;

    boolean isVaccineExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminTogetherWeWinBinding = ActivityAdminTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(adminTogetherWeWinBinding.getRoot());

        setSupportActionBar(adminTogetherWeWinBinding.toolBar);
        adminTogetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Vaccines Types");

        adminTogetherWeWinBinding.vaccineTypeRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setLayoutManager(linearLayoutManager);

        mVaccine=new ArrayList<>();

        vaccineAdapter=new AdminVaccineTypesAdapter(AdminTogetherWeWinActivity.this,mVaccine);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setAdapter(vaccineAdapter);
        vaccineAdapter.setOnItemClickListener(this::menuClick);

        adminTogetherWeWinBinding.addBtn.setOnClickListener(v -> {
            AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.add_vaccine_layout, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Add Vaccine");

            nameLayout = dialogView.findViewById(R.id.vaccine_name_layout);
            infoLayout = dialogView.findViewById(R.id.vaccine_info_layout);
            uploadBtn = dialogView.findViewById(R.id.vaccineUploadBtn);


            uploadBtn.setOnClickListener(v1 -> {
                String name = nameLayout.getEditText().getText().toString();
                String info = infoLayout.getEditText().getText().toString();

                if (!name.isEmpty() && !info.isEmpty()) {
                    uploadVaccine();
                    dialogBuilder.dismiss();
                } else {
                    Toast.makeText(this, "Invalid data!", Toast.LENGTH_SHORT).show();
                }
            });
            dialogBuilder.show();
        });

        loadVaccines();
    }

    private void loadVaccines() {
        mCollectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mVaccine.clear();
                    for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        Vaccine vaccine=documentSnapshot.toObject(Vaccine.class);
                        mVaccine.add(vaccine);
                    }
                    vaccineAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void menuClick(View view,int position) {
        selectedVaccine = mVaccine.get(position);
        PopupMenu popup = new PopupMenu(AdminTogetherWeWinActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:

                    return true;
                case R.id.delete:

                    return false;

            }
            return false;
        });
        popup.show();
    }

    private void uploadVaccine() {
        String name = nameLayout.getEditText().getText().toString();
        String info = infoLayout.getEditText().getText().toString();

        Vaccine vaccine = new Vaccine(name, info);

        mFirestore.collection("Vaccine Types").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            isVaccineExists = name.equals(queryDocumentSnapshot.getId());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

        if (isVaccineExists) {
            Toast.makeText(this, "This vaccine already exist...", Toast.LENGTH_SHORT).show();
        } else {
            DocumentReference documentReference = mFirestore.collection("Vaccines Types").document(vaccine.getName());
            documentReference.set(vaccine)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Vaccine added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadVaccines();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVaccines();
    }
}