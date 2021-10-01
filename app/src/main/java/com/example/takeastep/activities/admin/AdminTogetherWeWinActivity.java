package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.takeastep.R;
import com.example.takeastep.adapters.AdminVaccineTypesAdapter;
import com.example.takeastep.models.Vaccine;

import java.util.ArrayList;

public class AdminTogetherWeWinActivity extends AppCompatActivity implements AdminVaccineTypesAdapter.OnMenuItemClickListener {

    ArrayList<Vaccine> mVaccine=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_together_we_win);
    }

    @Override
    public void onMenuClick(View view, int position) {
        Vaccine currentVaccine=mVaccine.get(position);
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.edit:
                    return true;
                case R.id.delete:
                    return true;
            }
            return false;
        });
    }
}