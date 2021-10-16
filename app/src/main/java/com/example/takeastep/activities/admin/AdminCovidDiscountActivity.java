package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.takeastep.R;
import com.example.takeastep.activities.admin.adapters.RequestsAdapter;
import com.example.takeastep.databinding.ActivityAdminCovidDiscountBinding;
import com.example.takeastep.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminCovidDiscountActivity extends AppCompatActivity {
    ActivityAdminCovidDiscountBinding adminCovidDiscountBinding;

    private FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;
    private StorageReference mStorageReference;
    ArrayList<User> mRequests;
    RequestsAdapter requestsAdapter;
    User selectedUser=new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminCovidDiscountBinding=ActivityAdminCovidDiscountBinding.inflate(getLayoutInflater());
        setContentView(adminCovidDiscountBinding.getRoot());


        setSupportActionBar(adminCovidDiscountBinding.toolBar);
        adminCovidDiscountBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore=FirebaseFirestore.getInstance();
        mCollectionReference=mFirestore.collection("users");

        adminCovidDiscountBinding.listView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        adminCovidDiscountBinding.listView.setLayoutManager(linearLayoutManager);

        mRequests=new ArrayList<>();

        requestsAdapter=new RequestsAdapter(mRequests,AdminCovidDiscountActivity.this);
        adminCovidDiscountBinding.listView.setAdapter(requestsAdapter);
        requestsAdapter.setOnItemClickListener(position -> {
            selectedUser=mRequests.get(position);
            mFirestore.collection("users").document(selectedUser.getId()).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.getString("discount")==null){
                    AlertDialog builder = new AlertDialog.Builder(AdminCovidDiscountActivity.this).create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.check_certificate_layout, null);
                    builder.setView(dialogLayout);
                    builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    ImageView certImage=dialogLayout.findViewById(R.id.certificate_img);
                    MaterialButton acceptBtn=dialogLayout.findViewById(R.id.accept_btn);
                    MaterialButton rejectBtn=dialogLayout.findViewById(R.id.reject_btn);

                    mStorageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference certificate=mStorageReference.child("usersPictures/"+selectedUser.getEmail()+"/certificate");
                    certificate.getDownloadUrl()
                            .addOnSuccessListener(uri ->
                                    Glide.with(AdminCovidDiscountActivity.this)
                                            .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .centerInside()
                                            .into(certImage))
                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                    acceptBtn.setOnClickListener(v -> {
                        Map<String,Object> valid=new HashMap<>();
                        valid.put("validCertificate",true);
                        builder.dismiss();

                        DocumentReference documentReference=mFirestore.collection("users").document(selectedUser.getId());
                        documentReference.update(valid)
                                .addOnSuccessListener(unused -> {
                                    //Toast.makeText(this, "Reply sent to user", Toast.LENGTH_SHORT).show();
                                    Intent discountIntent=new Intent(getApplicationContext(), AddDiscountActivity.class);
                                    discountIntent.putExtra("userEmail",selectedUser.getEmail());
                                    discountIntent.putExtra("userId",selectedUser.getId());
                                    startActivity(discountIntent);

                                    //mRequests.remove(position);
                                    //requestsAdapter.notifyItemRemoved(position);
                                });
                    });

                    rejectBtn.setOnClickListener(v -> {
                        Map<String,Object> valid=new HashMap<>();
                        valid.put("validCertificate",false);

                        DocumentReference documentReference=mFirestore.collection("users").document(selectedUser.getId());
                        documentReference.update(valid)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Reply sent to user", Toast.LENGTH_SHORT).show();
                                    builder.dismiss();
                                });
                    });
                    builder.show();
                }else{
                    Toast.makeText(this, "User already have discount", Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadRequests();
    }

    private void loadRequests() {
        mCollectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult()!=null){
                        mRequests.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if (queryDocumentSnapshot.exists()){
                                if (queryDocumentSnapshot.getString("certificate")!=null){
                                    adminCovidDiscountBinding.text.setVisibility(View.GONE);
                                    User user=queryDocumentSnapshot.toObject(User.class);
                                    mRequests.add(user);
                                }
                            }
                        }
                        requestsAdapter.notifyDataSetChanged();
                    }else {
                        showTextMessage();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showTextMessage() {
        adminCovidDiscountBinding.text.setVisibility(View.VISIBLE);
        adminCovidDiscountBinding.listView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}