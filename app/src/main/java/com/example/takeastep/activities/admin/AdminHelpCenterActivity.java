package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.activities.admin.adapters.UsersAdapter;
import com.example.takeastep.databinding.ActivityAdminHelpcenterBinding;
import com.example.takeastep.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;

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
        mCollectionReference = mFirestore.collection("chat users");
        usersList = new ArrayList<>();

        mUsersAdapter = new UsersAdapter(AdminHelpCenterActivity.this, usersList);
        helpCenterBinding.usersRecycler.setAdapter(mUsersAdapter);
        helpCenterBinding.usersRecycler.setLayoutManager(new LinearLayoutManager(this));
        mUsersAdapter.setOnItemClickListener(position -> {
            selectedUser = usersList.get(position);
            Intent intent=new Intent(getApplicationContext(),AdminChatActivity.class);
            if (selectedUser.getId()!=null){
                intent.putExtra("userId",selectedUser.getId());
                startActivity(intent);
            }

        });

        getUsers();

    }

    private void getUsers() {
        mCollectionReference.orderBy("lastMessageTime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        usersList.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Log.w("HERE,",queryDocumentSnapshot.exists()+"");
                            if (queryDocumentSnapshot.exists()) {
                                helpCenterBinding.errorText.setVisibility(View.GONE);
                                Log.w("HERE,",queryDocumentSnapshot.getId());
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    usersList.add(user);
                            }
                        }
                        Log.w("HERE,",usersList.size()+"");
                        mUsersAdapter.setUsersList(usersList);
                        mUsersAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsers();
    }
}