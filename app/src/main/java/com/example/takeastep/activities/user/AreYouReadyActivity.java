package com.example.takeastep.activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

        mAdapter=new AreYouReadyAdapter(mContent,AreYouReadyActivity.this);
        areYouReadyBinding.contentRecyclerView.setAdapter(mAdapter);

        loadCategories();
        loadAllCategoriesList();

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
                        chip=new Chip(this);
                        chip.setText(mCategories.get(i));
                        chip.setCheckable(true);
                        chip.setId(i);
                        areYouReadyBinding.chipGroup.addView(chip,i);
                        chip.setOnClickListener(v -> {
                            loadContents();
                        });

                    }

                    chip.setOnClickListener(v -> loadContents());

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading categories!", Toast.LENGTH_SHORT).show());
    }


    private void loadContents() {

        mCollectionReference.orderBy("time",Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        mContent.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if (queryDocumentSnapshot.exists()){
                                chipId=areYouReadyBinding.chipGroup.getCheckedChipId();
                                if (chipId==-1){
                                    loadAllCategoriesList();
                                }else{
                                    if (queryDocumentSnapshot.getString("category").equals(mCategories.get(chipId))){
                                        areYouReadyBinding.errorText.setVisibility(View.GONE);
                                        ReadyContent content=queryDocumentSnapshot.toObject(ReadyContent.class);
                                        mContent.add(content);

                                    }
                                }
                            }
                        }
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
                    Log.v("categoriesSize, ",mContent.size()+"");
                    mAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AreYouReadyActivity.this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    private void showTextMessage() {
        areYouReadyBinding.errorText.setVisibility(View.VISIBLE);
        areYouReadyBinding.contentRecyclerView.setVisibility(View.GONE);
    }

}