package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.R;
import com.example.takeastep.activities.admin.adapters.AdminVaccineTypesAdapter;
import com.example.takeastep.databinding.ActivityAdminTogetherWeWinBinding;
import com.example.takeastep.models.Vaccine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminTogetherWeWinActivity extends AppCompatActivity {

    ArrayList<Vaccine> mVaccine;
    AdminVaccineTypesAdapter vaccineAdapter;
    ActivityAdminTogetherWeWinBinding adminTogetherWeWinBinding;
    Vaccine selectedVaccine = new Vaccine();

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mFirebaseStorage;
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
        mFirebaseStorage=FirebaseStorage.getInstance();
        mCollectionReference = mFirestore.collection("Vaccines");

        adminTogetherWeWinBinding.vaccineTypeRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setLayoutManager(linearLayoutManager);

        mVaccine = new ArrayList<>();
        loadVaccines();

        vaccineAdapter = new AdminVaccineTypesAdapter(AdminTogetherWeWinActivity.this, mVaccine);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setAdapter(vaccineAdapter);
        vaccineAdapter.setOnItemClickListener(this::menuClick);

        adminTogetherWeWinBinding.addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),AddVaccineActivity.class));
        });

    }

    private void loadVaccines() {
        mCollectionReference=mFirestore.collection("Vaccines");
        mCollectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            if (queryDocumentSnapshot.exists()){
                                queryDocumentSnapshot.getReference().collection("posts").get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()){
                                                mVaccine.clear();
                                                for (QueryDocumentSnapshot queryDocumentSnapshot1:task1.getResult()){
                                                    if (queryDocumentSnapshot1.exists()){
                                                        Vaccine vaccine = queryDocumentSnapshot1.toObject(Vaccine.class);
                                                        mVaccine.add(vaccine);
                                                    }
                                                }
                                                if (mVaccine.size()==0){
                                                    adminTogetherWeWinBinding.errorText.setVisibility(View.VISIBLE);
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

                    /* *******         محتاجة تعديل            ***************/

                    AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
                    LayoutInflater layoutInflater = this.getLayoutInflater();
                    View dialogView = layoutInflater.inflate(R.layout.add_vaccine_layout, null);
                    dialogBuilder.setView(dialogView);

                    nameLayout = dialogView.findViewById(R.id.vaccine_name_layout);
                    infoLayout = dialogView.findViewById(R.id.vaccine_info_layout);
                    uploadBtn = dialogView.findViewById(R.id.vaccineUploadBtn);

                    nameLayout.getEditText().setText(selectedVaccine.getName());
                    nameLayout.setEnabled(false);
                    infoLayout.getEditText().setText(selectedVaccine.getInfo());
                    uploadBtn.setText("Update");
                    uploadBtn.setOnClickListener(v -> {
                        String info = infoLayout.getEditText().getText().toString();
                        if (!info.isEmpty()) {
                            Map<String, Object> update = new HashMap<>();
                            update.put("info", info);

                            mCollectionReference.document(selectedVaccine.getName()).update(update)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Vaccine updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                            dialogBuilder.dismiss();
                        } else {
                            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
                        }

                    });
                    dialogBuilder.show();
                    dialogBuilder.setOnDismissListener(dialog -> loadVaccines());
                    loadVaccines();

                    return true;
                case R.id.delete:
                    mCollectionReference.document(selectedVaccine.getName()).collection("posts").get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                                    if (documentSnapshot.getString("info").equals(selectedVaccine.getInfo())
                                            && documentSnapshot.getString("image").equals(selectedVaccine.getImage())){
                                        documentSnapshot.getReference().delete()
                                                .addOnSuccessListener(unused -> {
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

    @Override
    protected void onResume() {
        super.onResume();
        loadVaccines();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadVaccines();
    }
}