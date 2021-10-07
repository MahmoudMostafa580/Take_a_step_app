package com.example.takeastep.activities.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.takeastep.activities.admin.adapters.UsersAdapter;
import com.example.takeastep.databinding.ActivityAdminHelpcenterBinding;
import com.example.takeastep.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminHelpCenterActivity extends AppCompatActivity {
    ActivityAdminHelpcenterBinding helpCenterBinding;
    private FirebaseFirestore mFirestore;
    private CollectionReference mCollectionReference;
    private ArrayList<User> usersList;
    private UsersAdapter mUsersAdapter;
    User selectedUser = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpCenterBinding = ActivityAdminHelpcenterBinding.inflate(getLayoutInflater());
        setContentView(helpCenterBinding.getRoot());

        setSupportActionBar(helpCenterBinding.toolBar);
        helpCenterBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("users");
        usersList = new ArrayList<>();

        mUsersAdapter = new UsersAdapter(AdminHelpCenterActivity.this, usersList);
        helpCenterBinding.usersRecycler.setAdapter(mUsersAdapter);
        mUsersAdapter.setOnItemClickListener(position -> {
            selectedUser = usersList.get(position);

        });

        getUsers();

    }

    private void getUsers() {
        mCollectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        usersList.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (queryDocumentSnapshot.exists()) {
                                if (queryDocumentSnapshot.contains("chat")) {
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    usersList.add(user);
                                }
                            }
                        }
                        mUsersAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}