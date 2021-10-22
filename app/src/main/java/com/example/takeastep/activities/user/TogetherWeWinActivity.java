package com.example.takeastep.activities.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.R;
import com.example.takeastep.activities.user.adapters.TogetherWeWinAdapter;
import com.example.takeastep.databinding.ActivityTogetherWeWinBinding;
import com.example.takeastep.models.ReadyContent;
import com.example.takeastep.models.Vaccine;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class TogetherWeWinActivity extends AppCompatActivity {
    ActivityTogetherWeWinBinding togetherWeWinBinding;
    ArrayList<Vaccine> mVaccine;
    ArrayList<Vaccine> tempVaccine = new ArrayList<>();
    TogetherWeWinAdapter togetherWeWinAdapter;

    private FirebaseFirestore mFirestore;
    private ArrayList<String> mTypes;
    private CollectionReference mCollectionReference;

    Chip chip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        togetherWeWinBinding = ActivityTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(togetherWeWinBinding.getRoot());

        setSupportActionBar(togetherWeWinBinding.toolBar);
        togetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("TogetherWeWin");

        togetherWeWinBinding.togetherWeWinRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        togetherWeWinBinding.togetherWeWinRecycler.setLayoutManager(linearLayoutManager);

        mVaccine = new ArrayList<>();
        mTypes = new ArrayList<>();

        togetherWeWinAdapter = new TogetherWeWinAdapter(tempVaccine, TogetherWeWinActivity.this);
        togetherWeWinBinding.togetherWeWinRecycler.setAdapter(togetherWeWinAdapter);

        loadTypes();
        loadAllVaccines();

        togetherWeWinBinding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                togetherWeWinBinding.chipGroup.check(R.id.chip_all);
            } else if (checkedId == R.id.chip_all) {
                tempVaccine.clear();
                tempVaccine.addAll(mVaccine);
                togetherWeWinAdapter.notifyDataSetChanged();
            } else {
                tempVaccine.clear();
                Chip chip = group.findViewById(checkedId);
                for (int i = 0; i < mVaccine.size(); i++) {
                    if (mVaccine.get(i).getName().equals(chip.getText().toString().trim())) {
                        tempVaccine.add(mVaccine.get(i));
                    }
                }
                togetherWeWinAdapter.notifyDataSetChanged();
            }
        });
    }

    void loadTypes() {
        mFirestore.collection("Vaccines").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mTypes.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.getLong("numOfPosts")>=1){
                                String name = documentSnapshot.getString("name");
                                mTypes.add(name);
                            }
                        }
                    }

                    for (int i = 0; i < mTypes.size(); i++) {
                        chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, togetherWeWinBinding.chipGroup, false);
                        chip.setText(mTypes.get(i));
                        chip.setId(i);
                        togetherWeWinBinding.chipGroup.addView(chip);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading types!", Toast.LENGTH_SHORT).show());
    }

    private void loadAllVaccines() {
        mCollectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mVaccine.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        togetherWeWinBinding.text.setVisibility(View.GONE);
                        Vaccine post = documentSnapshot.toObject(Vaccine.class);
                        mVaccine.add(post);
                        tempVaccine.add(post);
                    }

                    togetherWeWinAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(TogetherWeWinActivity.this, "Error while loading posts!", Toast.LENGTH_SHORT).show());
    }


}