package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import com.example.takeastep.R;
import com.example.takeastep.adapters.CountryAdapter;
import com.example.takeastep.databinding.ActivityAdminTakeAStepBinding;
import com.example.takeastep.models.Country;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AdminTakeAStepActivity extends AppCompatActivity implements CountryAdapter.OnItemClickListener {
    ArrayList<Country> mCountries=new ArrayList<>();
    ActivityAdminTakeAStepBinding adminTakeAStepBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminTakeAStepBinding=ActivityAdminTakeAStepBinding.inflate(getLayoutInflater());
        setContentView(adminTakeAStepBinding.getRoot());

        adminTakeAStepBinding.addCountryBtn.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.add_country_layout, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Add Country");
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        });
    }

    @Override
    public void onMenuClick(View view, int position) {
        Country currentCountry=mCountries.get(position);
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.edit:
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    LayoutInflater layoutInflater = this.getLayoutInflater();
                    View dialogView = layoutInflater.inflate(R.layout.add_country_layout, null);

                    TextInputLayout countryName=dialogView.findViewById(R.id.country_name_layout);
                    TextInputLayout countryLink=dialogView.findViewById(R.id.country_link_layout);
                    MaterialButton countryUploadBtn=dialogView.findViewById(R.id.countryUploadBtn);

                    countryName.getEditText().setText(currentCountry.getName());
                    countryLink.getEditText().setText(currentCountry.getLink());
                    countryUploadBtn.setOnClickListener(v -> {

                    });
                    dialogBuilder.setView(dialogView);

                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                    return true;

                case R.id.delete:

                    return true;
            }
            return false;
        });
    }
}