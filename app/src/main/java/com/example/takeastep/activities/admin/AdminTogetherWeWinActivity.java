package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.R;
import com.example.takeastep.activities.LauncherActivity;
import com.example.takeastep.activities.admin.adapters.AdminVaccineTypesAdapter;
import com.example.takeastep.databinding.ActivityAdminTogetherWeWinBinding;
import com.example.takeastep.models.Vaccine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminTogetherWeWinActivity extends AppCompatActivity {

    ArrayList<Vaccine> mVaccine;
    AdminVaccineTypesAdapter vaccineAdapter;
    ActivityAdminTogetherWeWinBinding adminTogetherWeWinBinding;
    Vaccine selectedVaccine = new Vaccine();

    private FirebaseFirestore mFirestore;
    private FirebaseStorage mFirebaseStorage;
    private CollectionReference mCollectionReference;
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminTogetherWeWinBinding = ActivityAdminTogetherWeWinBinding.inflate(getLayoutInflater());
        setContentView(adminTogetherWeWinBinding.getRoot());

        setSupportActionBar(adminTogetherWeWinBinding.toolBar);
        adminTogetherWeWinBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mCollectionReference = mFirestore.collection("TogetherWeWin");
        mStorageReference = FirebaseStorage.getInstance().getReference();

        adminTogetherWeWinBinding.vaccineTypeRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setLayoutManager(linearLayoutManager);

        mVaccine = new ArrayList<>();
        loadAllVaccines();

        vaccineAdapter = new AdminVaccineTypesAdapter(AdminTogetherWeWinActivity.this, mVaccine);
        adminTogetherWeWinBinding.vaccineTypeRecycler.setAdapter(vaccineAdapter);
        vaccineAdapter.setOnItemClickListener(this::menuClick);

        adminTogetherWeWinBinding.addBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AddVaccineActivity.class));
        });
        loadAllVaccines();
    }

    private void loadAllVaccines() {
        mCollectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mVaccine.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        adminTogetherWeWinBinding.errorText.setVisibility(View.GONE);
                        Vaccine post = documentSnapshot.toObject(Vaccine.class);
                        mVaccine.add(post);
                    }
                    vaccineAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminTogetherWeWinActivity.this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    private void menuClick(View view, int position) {
        selectedVaccine = mVaccine.get(position);
        PopupMenu popup = new PopupMenu(AdminTogetherWeWinActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    Intent updateIntent = new Intent(getApplicationContext(), EditVaccineActivity.class);
                    String name = mVaccine.get(position).getName();
                    String info = mVaccine.get(position).getInfo();
                    String image = mVaccine.get(position).getImage();
                    long time = mVaccine.get(position).getTime();

                    updateIntent.putExtra("name", name);
                    updateIntent.putExtra("info", info);
                    updateIntent.putExtra("image", image);
                    updateIntent.putExtra("time", time);

                    startActivity(updateIntent);

                    return true;
                case R.id.delete:
                    mFirestore.collection("Vaccines").document(mVaccine.get(position).getName())
                            .update("numOfPosts", FieldValue.increment(-1))
                            .addOnSuccessListener(unused -> {
                                mCollectionReference.document(String.valueOf(mVaccine.get(position).getTime())).delete()
                                        .addOnSuccessListener(unused1 -> {
                                            StorageReference imageRef = mStorageReference.child("Vaccines/" + mVaccine.get(position).getTime().toString());
                                            imageRef.delete()
                                                    .addOnSuccessListener(unused2 -> {
                                                        mVaccine.remove(position);
                                                        vaccineAdapter.notifyItemRemoved(position);
                                                        LauncherActivity.mapExoPlayersvideo.remove(position);
                                                        Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                                        if (mVaccine.size() == 0) {
                                                            adminTogetherWeWinBinding.errorText.setVisibility(View.VISIBLE);
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Error while deleting item!", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                    return false;

            }
            return false;
        });
        popup.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllVaccines();
    }
}