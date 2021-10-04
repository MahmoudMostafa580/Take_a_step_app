package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.takeastep.R;
import com.example.takeastep.adapters.RequestsAdapter;
import com.example.takeastep.databinding.ActivityAdminCovidDiscountBinding;
import com.example.takeastep.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

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
            builder.show();
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
}