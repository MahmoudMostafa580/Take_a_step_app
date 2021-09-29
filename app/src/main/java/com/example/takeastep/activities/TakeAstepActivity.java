package com.example.takeastep.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityTakeAstepBinding;

public class TakeAstepActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ActivityTakeAstepBinding takeAstepBinding;
    String[] countries ={"Egypt","Kuwait","Kingdom of Saudi Arabia", "The Emirates"};
    String mCountry;
    String countryUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takeAstepBinding=ActivityTakeAstepBinding.inflate(getLayoutInflater());
        setContentView(takeAstepBinding.getRoot());

        setSupportActionBar(takeAstepBinding.toolBar);
        takeAstepBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this,android.R.layout.select_dialog_item,countries);
        takeAstepBinding.countriesSpinner.setAdapter(spinnerAdapter);
        takeAstepBinding.countriesSpinner.setOnItemClickListener(this);

        takeAstepBinding.registrationBtn.setOnClickListener(v -> {
            if (checkCountry(mCountry)){
                Intent intent=new Intent(getApplicationContext(),VaccineRegistrationActivity.class);
                intent.putExtra("countryUrl",countryUrl);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Please select country...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkCountry(String country){
        if (country.equals("Egypt")){
            countryUrl="https://egcovac.mohp.gov.eg/#/home";
            return true;
        }else if (country.equals("Kuwait")){
            countryUrl="https://cov19vaccine.moh.gov.kw/SPCMS/CVD_19_Vaccine_RegistrationAr.aspx";
            return true;
        }else if (country.equals("Kingdom of Saudi Arabia")){
            countryUrl="https://eservices.moh.gov.sa/CoronaVaccineRegistration";
            return true;
        }else if (country.equals("The Emirates")){
            countryUrl="https://www.dha.gov.ae/ar/covid19/pages/vaccineappoint.aspx";
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCountry=parent.getItemAtPosition(position).toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this,android.R.layout.select_dialog_item,countries);
        takeAstepBinding.countriesSpinner.setAdapter(spinnerAdapter);
        takeAstepBinding.countriesSpinner.setOnItemClickListener(this);
    }
}