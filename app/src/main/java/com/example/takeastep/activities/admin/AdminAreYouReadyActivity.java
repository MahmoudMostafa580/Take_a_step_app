package com.example.takeastep.activities.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.activities.admin.adapters.ReadyContentAdapter;
import com.example.takeastep.databinding.ActivityAdminAreYouReadyBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminAreYouReadyActivity extends AppCompatActivity {
    ActivityAdminAreYouReadyBinding adminAreYouReadyBinding;
    ReadyContentAdapter mContentAdapter;
    FirebaseFirestore mFirestore;
    CollectionReference mCollectionReference;

    ArrayList<ReadyContent> mContent;
    ReadyContent selectedContent = new ReadyContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminAreYouReadyBinding = ActivityAdminAreYouReadyBinding.inflate(getLayoutInflater());
        setContentView(adminAreYouReadyBinding.getRoot());

        mFirestore = FirebaseFirestore.getInstance();
        //mStorageReference = FirebaseStorage.getInstance().getReference();
        mCollectionReference = mFirestore.collection("Ready Content");

        setSupportActionBar(adminAreYouReadyBinding.toolBar);
        adminAreYouReadyBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        adminAreYouReadyBinding.recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        adminAreYouReadyBinding.recycler.setLayoutManager(linearLayoutManager);

        mContent = new ArrayList<>();

        mContentAdapter = new ReadyContentAdapter(mContent, AdminAreYouReadyActivity.this);
        adminAreYouReadyBinding.recycler.setAdapter(mContentAdapter);
        mContentAdapter.setOnItemClickListener((position, view) -> {
            selectedContent = mContent.get(position);
            PopupMenu popup = new PopupMenu(AdminAreYouReadyActivity.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.vaccine_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit:
                        Intent updateIntent=new Intent(getApplicationContext(),EditContentActivity.class);
                        String caption=mContent.get(position).getCaption();
                        String category=mContent.get(position).getCategory();
                        String imageUrl=mContent.get(position).getImageUrl();
                        String videoUrl=mContent.get(position).getVideoUrl();
                        updateIntent.putExtra("caption",caption);
                        updateIntent.putExtra("category",category);
                        if (videoUrl==null){
                            updateIntent.putExtra("imageUrl",imageUrl);
                        }else{
                            updateIntent.putExtra("videoUrl",videoUrl);
                        }
                        startActivity(updateIntent);

                        return true;
                    case R.id.delete:
                        mCollectionReference.document(selectedContent.getCaption()).delete()
                                .addOnSuccessListener(unused -> {
                                    mContent.remove(position);
                                    mContentAdapter.notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

                        return false;

                }
                return false;
            });
            popup.show();
        });

        loadContents();
    }

    private void loadContents() {
        mCollectionReference.orderBy("time",Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mContent.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        ReadyContent content = documentSnapshot.toObject(ReadyContent.class);
                        mContent.add(content);
                    }
                    mContentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminAreYouReadyActivity.this, "Error while loading content!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.are_you_ready_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(getApplicationContext(), AddContentActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}