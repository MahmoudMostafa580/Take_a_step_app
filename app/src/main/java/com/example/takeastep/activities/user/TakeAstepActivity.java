package com.example.takeastep.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.takeastep.databinding.ActivityTakeAstepBinding;

public class TakeAstepActivity extends AppCompatActivity {
    ActivityTakeAstepBinding takeAstepBinding;
    String[] countries = {"Egypt", "Kuwait", "Kingdom of Saudi Arabia", "The Emirates"};
    String mCountry;
    String countryUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takeAstepBinding = ActivityTakeAstepBinding.inflate(getLayoutInflater());
        setContentView(takeAstepBinding.getRoot());

        setSupportActionBar(takeAstepBinding.toolBar);
        takeAstepBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, countries);
        takeAstepBinding.countriesSpinner.setAdapter(spinnerAdapter);
        takeAstepBinding.countriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountry = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(TakeAstepActivity.this, "No country selected !", Toast.LENGTH_SHORT).show();
            }
        });
        takeAstepBinding.countriesSpinner.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                takeAstepBinding.countriesSpinner.clearFocus();
            }
        });

        takeAstepBinding.registrationBtn.setOnClickListener(v -> checkCountry());
    }

    public void checkCountry() {
        String country = takeAstepBinding.countriesSpinner.getText().toString();
        Intent intent = new Intent(getApplicationContext(), VaccineRegistrationActivity.class);

        if (country.isEmpty()) {
            Toast.makeText(TakeAstepActivity.this, "No country selected !", Toast.LENGTH_SHORT).show();
        } else if (country.equals("Egypt")) {
            countryUrl = "https://egcovac.mohp.gov.eg/#/home";
            intent.putExtra("countryUrl", countryUrl);
            startActivity(intent);
        } else if (country.equals("Kuwait")) {
            countryUrl = "https://cov19vaccine.moh.gov.kw/SPCMS/CVD_19_Vaccine_RegistrationAr.aspx";
            intent.putExtra("countryUrl", countryUrl);
            startActivity(intent);
        } else if (country.equals("Kingdom of Saudi Arabia")) {
            countryUrl = "https://eservices.moh.gov.sa/CoronaVaccineRegistration";
            intent.putExtra("countryUrl", countryUrl);
            startActivity(intent);
        } else if (country.equals("The Emirates")) {
            countryUrl = "https://www.dha.gov.ae/ar/covid19/pages/vaccineappoint.aspx";
            intent.putExtra("countryUrl", countryUrl);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, countries);
        takeAstepBinding.countriesSpinner.setAdapter(spinnerAdapter);
    }
}