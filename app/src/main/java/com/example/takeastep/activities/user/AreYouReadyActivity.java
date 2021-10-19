package com.example.takeastep.activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.activities.LauncherActivity;
import com.example.takeastep.activities.user.adapters.AreYouReadyAdapter;
import com.example.takeastep.databinding.ActivityAreYouReadyBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.android.material.chip.Chip;
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
    ArrayList<ReadyContent> tempContent = new ArrayList<>();
    private ArrayList<String> mCategories;

    int chipId;
    Chip chip;

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
        mCategories=new ArrayList<>();


        areYouReadyBinding.contentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        areYouReadyBinding.contentRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter=new AreYouReadyAdapter(tempContent,AreYouReadyActivity.this);
        areYouReadyBinding.contentRecyclerView.setAdapter(mAdapter);

        loadCategories();
        loadAllCategoriesList();

        areYouReadyBinding.chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                areYouReadyBinding.chipGroup.check(R.id.chip_all);
            } else if (checkedId == R.id.chip_all) {
                LauncherActivity.stopVideos(-1);
                tempContent.clear();
                tempContent.addAll(mContent);
                mAdapter.notifyDataSetChanged();
            } else {
                LauncherActivity.stopVideos(-1);
                tempContent.clear();
                Chip chip = group.findViewById(checkedId);
                for (int i = 0; i < mContent.size(); i++) {
                    if (mContent.get(i).getCategory().equals(chip.getText().toString().trim().toLowerCase())) {
                        tempContent.add(mContent.get(i));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }



    private void loadCategories() {
        mFirestore.collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mCategories.clear();
                    for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                        if (documentSnapshot.exists()){
                            String name = documentSnapshot.getString("name");
                            mCategories.add(name);
                        }
                    }

                    for (int i = 0; i <mCategories.size() ; i++) {
                        chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, areYouReadyBinding.chipGroup, false);
                        chip.setText(mCategories.get(i));
                        chip.setCheckable(true);
                        chip.setId(i);
                        areYouReadyBinding.chipGroup.addView(chip);
                    }

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading categories!", Toast.LENGTH_SHORT).show());
    }


    private void loadAllCategoriesList() {
        mCollectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mContent.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        areYouReadyBinding.errorText.setVisibility(View.GONE);
                        ReadyContent content = documentSnapshot.toObject(ReadyContent.class);
                        mContent.add(content);
                        tempContent.add(content);
                    }
                    Log.v("categoriesSize, ",mContent.size()+"");
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AreYouReadyActivity.this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LauncherActivity.stopVideos(-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LauncherActivity.stopVideos(-1);

    }
}