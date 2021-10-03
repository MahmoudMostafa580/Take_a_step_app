package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.takeastep.R;
import com.example.takeastep.databinding.ActivityAdminCovidDiscountBinding;

import java.util.ArrayList;

public class AdminCovidDiscountActivity extends AppCompatActivity {
    ActivityAdminCovidDiscountBinding adminCovidDiscountBinding;
    ArrayList<String> requests=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminCovidDiscountBinding=ActivityAdminCovidDiscountBinding.inflate(getLayoutInflater());
        setContentView(adminCovidDiscountBinding.getRoot());

        final ArrayAdapter<String> requestsAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_activated_1,requests);

        setSupportActionBar(adminCovidDiscountBinding.toolBar);
        adminCovidDiscountBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        adminCovidDiscountBinding.listView.setAdapter(requestsAdapter);
        adminCovidDiscountBinding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCovidDiscountActivity.this);
                builder.setPositiveButton("Accept", (dialog, which) -> {

                }).setNegativeButton("Reject", (dialog, which) -> {

                });
                final AlertDialog dialog = builder.create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.check_certificate_layout, null);
                dialog.setView(dialogLayout);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();

            }
        });

    }
}