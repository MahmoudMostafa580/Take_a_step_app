package com.example.takeastep.activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.activities.admin.AdminAreYouReadyActivity;
import com.example.takeastep.activities.user.adapters.AreYouReadyAdapter;
import com.example.takeastep.databinding.ActivityAreYouReadyBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AreYouReadyActivity extends AppCompatActivity {
    ActivityAreYouReadyBinding areYouReadyBinding;

    AreYouReadyAdapter mAdapter;
    FirebaseFirestore mFirestore;
    CollectionReference mCollectionReference;

    ArrayList<ReadyContent> mContent;

    int chipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        areYouReadyBinding=ActivityAreYouReadyBinding.inflate(getLayoutInflater());
        setContentView(areYouReadyBinding.getRoot());
        setSupportActionBar(areYouReadyBinding.toolBar);
        areYouReadyBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Ready Content");
        mContent = new ArrayList<>();


        areYouReadyBinding.contentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        areYouReadyBinding.contentRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter=new AreYouReadyAdapter(mContent,AreYouReadyActivity.this);
        areYouReadyBinding.contentRecyclerView.setAdapter(mAdapter);


        chipId=areYouReadyBinding.chipGroup.getCheckedChipId();
        areYouReadyBinding.allCategoriesChip.setOnClickListener(v -> loadAllCategoriesList());
        areYouReadyBinding.vaccinationBenefits.setOnClickListener(v -> loadBenefitsList());
        areYouReadyBinding.vaccinationRisks.setOnClickListener(v -> loadRisksList());

        checkChip(chipId);
    }

    private void checkChip(int chipId) {
        if (chipId==areYouReadyBinding.allCategoriesChip.getId()){
            loadAllCategoriesList();
        }else if (chipId==areYouReadyBinding.vaccinationBenefits.getId()){
            loadBenefitsList();
        }else if (chipId==areYouReadyBinding.vaccinationRisks.getId()){
            loadRisksList();
        }
    }

    private void loadRisksList() {
        mCollectionReference.orderBy("time",Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        mContent.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if (queryDocumentSnapshot.exists()){
                                if (queryDocumentSnapshot.getString("category").equals("Vaccination Risks")){
                                    areYouReadyBinding.errorText.setVisibility(View.GONE);
                                    ReadyContent content=queryDocumentSnapshot.toObject(ReadyContent.class);
                                    mContent.add(content);
                                }
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        //Toast.makeText(this, mContent.size(), Toast.LENGTH_SHORT).show();
                    }else{
                        showTextMessage();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    private void loadBenefitsList() {
        mCollectionReference.orderBy("time",Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        mContent.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if (queryDocumentSnapshot.exists()){
                                if (queryDocumentSnapshot.getString("category").equals("Vaccination Benefits")){
                                    areYouReadyBinding.errorText.setVisibility(View.GONE);
                                    ReadyContent content=queryDocumentSnapshot.toObject(ReadyContent.class);
                                    mContent.add(content);
                                }
                            }
                        }
                        //Toast.makeText(this, mContent.size(), Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                    }else{
                        showTextMessage();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    private void loadAllCategoriesList() {
        mCollectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mContent.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        areYouReadyBinding.errorText.setVisibility(View.GONE);
                        ReadyContent content = documentSnapshot.toObject(ReadyContent.class);
                        mContent.add(content);
                    }
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AreYouReadyActivity.this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    private void showTextMessage() {
        areYouReadyBinding.errorText.setVisibility(View.VISIBLE);
        areYouReadyBinding.contentRecyclerView.setVisibility(View.GONE);
    }

}