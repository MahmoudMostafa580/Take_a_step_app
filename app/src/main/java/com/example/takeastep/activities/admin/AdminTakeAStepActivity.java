package com.example.takeastep.activities.admin;

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
import com.example.takeastep.activities.admin.adapters.CountryAdapter;
import com.example.takeastep.databinding.ActivityAdminTakeAStepBinding;
import com.example.takeastep.models.Country;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminTakeAStepActivity extends AppCompatActivity {
    ArrayList<Country> mCountries;
    CountryAdapter mCountryAdapter;
    Country selectedCountry = new Country();
    ActivityAdminTakeAStepBinding adminTakeAStepBinding;

    private FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;

    TextInputLayout countryName, countryLink;
    MaterialButton uploadCountryBtn;

    boolean isCountryExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminTakeAStepBinding = ActivityAdminTakeAStepBinding.inflate(getLayoutInflater());
        setContentView(adminTakeAStepBinding.getRoot());

        setSupportActionBar(adminTakeAStepBinding.toolBar);
        adminTakeAStepBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Countries");

        adminTakeAStepBinding.countriesRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adminTakeAStepBinding.countriesRecycler.setLayoutManager(linearLayoutManager);

        mCountries = new ArrayList<>();
        mCountryAdapter = new CountryAdapter(mCountries, AdminTakeAStepActivity.this);
        adminTakeAStepBinding.countriesRecycler.setAdapter(mCountryAdapter);

        mCountryAdapter.setOnItemClickListener(this::menuClick);


        adminTakeAStepBinding.addCountryBtn.setOnClickListener(v -> {
            AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.add_country_layout, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Add Country");

            countryName = dialogView.findViewById(R.id.country_name_layout);
            countryLink = dialogView.findViewById(R.id.country_link_layout);
            uploadCountryBtn = dialogView.findViewById(R.id.countryUploadBtn);

            uploadCountryBtn.setOnClickListener(v1 -> {
                String name = countryName.getEditText().getText().toString();
                String link = countryLink.getEditText().getText().toString();

                if (!name.isEmpty() && !link.isEmpty()) {
                    uploadCountry();
                    dialogBuilder.dismiss();
                } else {
                    Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
                }
            });
            dialogBuilder.show();
            dialogBuilder.setOnDismissListener(dialog -> {
                loadCountries();
            });
        });

        loadCountries();

    }

    private void loadCountries() {
        mCollectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mCountries.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Country country = documentSnapshot.toObject(Country.class);
                        mCountries.add(country);
                    }
                    mCountryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadCountry() {
        String name = countryName.getEditText().getText().toString();
        String link = countryLink.getEditText().getText().toString();

        Country country = new Country(name, link);

        mFirestore.collection("Countries").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        isCountryExists = name.equals(documentSnapshot.getId());
                        if (isCountryExists)
                            break;
                    }

                    if (isCountryExists) {
                        Toast.makeText(this, "This country already exist", Toast.LENGTH_SHORT).show();
                    } else {
                        DocumentReference documentReference = mFirestore.collection("Countries").document(country.getName());
                        documentReference.set(country)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Country added successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void menuClick(View view, int position) {
        selectedCountry = mCountries.get(position);
        PopupMenu popup = new PopupMenu(AdminTakeAStepActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
                    LayoutInflater layoutInflater = this.getLayoutInflater();
                    View dialogView = layoutInflater.inflate(R.layout.add_country_layout, null);
                    dialogBuilder.setView(dialogView);

                    TextInputLayout countryName = dialogView.findViewById(R.id.country_name_layout);
                    TextInputLayout countryLink = dialogView.findViewById(R.id.country_link_layout);
                    MaterialButton countryUploadBtn = dialogView.findViewById(R.id.countryUploadBtn);

                    countryName.getEditText().setText(selectedCountry.getName());
                    countryName.setEnabled(false);
                    countryLink.getEditText().setText(selectedCountry.getLink());
                    countryUploadBtn.setText("Update");
                    countryUploadBtn.setOnClickListener(v -> {
                        String link = countryLink.getEditText().getText().toString();
                        if (!link.isEmpty()) {
                            Map<String, Object> update = new HashMap<>();
                            update.put("link", link);

                            mCollectionReference.document(selectedCountry.getName()).update(update)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Country updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                            dialogBuilder.dismiss();
                        }else{
                            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
                        }

                    });
                    dialogBuilder.show();
                    dialogBuilder.setOnDismissListener(dialog -> loadCountries());
                    loadCountries();

                    return true;
                case R.id.delete:
                    mCollectionReference.document(selectedCountry.getName()).delete()
                            .addOnSuccessListener(unused -> {
                                mCountries.remove(position);
                                mCountryAdapter.notifyItemRemoved(position);
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                    return false;

            }
            return false;
        });
        popup.show();
    }

}