package com.example.takeastep.activities.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.activities.user.adapters.TogetherWeWinAdapter;
import com.example.takeastep.databinding.ActivityTogetherWeWinBinding;
import com.example.takeastep.models.Vaccine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class TogetherWeWinActivity extends AppCompatActivity {
    ActivityTogetherWeWinBinding togetherWeWinBinding;
    ArrayList<Vaccine> mVaccine;
    TogetherWeWinAdapter togetherWeWinAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        togetherWeWinBinding = ActivityTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(togetherWeWinBinding.getRoot());

        setSupportActionBar(togetherWeWinBinding.toolBar);
        togetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Vaccines Types");

        togetherWeWinBinding.togetherWeWinRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        togetherWeWinBinding.togetherWeWinRecycler.setLayoutManager(linearLayoutManager);

        mVaccine = new ArrayList<>();

        togetherWeWinAdapter = new TogetherWeWinAdapter(mVaccine, TogetherWeWinActivity.this);
        togetherWeWinBinding.togetherWeWinRecycler.setAdapter(togetherWeWinAdapter);

        loadVaccines();

    }

    private void loadVaccines() {
        mCollectionReference.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mVaccine.clear();
                    for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                            togetherWeWinBinding.text.setVisibility(View.GONE);
                            Vaccine vaccine = documentSnapshot.toObject(Vaccine.class);
                            mVaccine.add(vaccine);
                    }
                    togetherWeWinAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading vaccines", Toast.LENGTH_SHORT).show());
    }

    private void showTextMessage() {
        togetherWeWinBinding.text.setVisibility(View.VISIBLE);
        togetherWeWinBinding.togetherWeWinRecycler.setVisibility(View.GONE);
    }
}