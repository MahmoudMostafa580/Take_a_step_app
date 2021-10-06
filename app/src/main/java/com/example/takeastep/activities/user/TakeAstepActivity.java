package com.example.takeastep.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.takeastep.activities.user.adapters.TakeStepAdapter;
import com.example.takeastep.databinding.ActivityTakeAstepBinding;
import com.example.takeastep.models.Country;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TakeAstepActivity extends AppCompatActivity {
    ActivityTakeAstepBinding takeAstepBinding;
    ArrayList<Country> countries;
    Country selectedCountry = new Country();
    ArrayAdapter<Country> spinnerAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;

    String mCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takeAstepBinding = ActivityTakeAstepBinding.inflate(getLayoutInflater());
        setContentView(takeAstepBinding.getRoot());

        setSupportActionBar(takeAstepBinding.toolBar);
        takeAstepBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Countries");

        countries=new ArrayList<>();

        spinnerAdapter = new ArrayAdapter<>(TakeAstepActivity.this, android.R.layout.expandable_list_content,countries);
        takeAstepBinding.countriesSpinner.setAdapter(spinnerAdapter);
        takeAstepBinding.countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountry = parent.getItemAtPosition(position).toString();
                selectedCountry=countries.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TakeAstepActivity.this, "No country selected !", Toast.LENGTH_SHORT).show();
            }
        });
        takeAstepBinding.countriesSpinner.setOnDismissListener(() -> takeAstepBinding.countriesSpinner.clearFocus());

        takeAstepBinding.registrationBtn.setOnClickListener(v -> checkCountry());

        loadCountries();
    }

    private void loadCountries(){
        mCollectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    countries.clear();
                    for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        Country country=documentSnapshot.toObject(Country.class);
                        countries.add(country);
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading countries!", Toast.LENGTH_SHORT).show());
    }

    public void checkCountry() {
        String countryUrl=selectedCountry.getLink();
        Intent intent = new Intent(getApplicationContext(), VaccineRegistrationActivity.class);

        intent.putExtra("countryUrl",countryUrl);
        startActivity(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<Country> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, countries);
        takeAstepBinding.countriesSpinner.setAdapter(spinnerAdapter);
    }
}