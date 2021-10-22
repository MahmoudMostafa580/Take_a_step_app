package com.example.takeastep.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.R;
import com.example.takeastep.activities.LauncherActivity;
import com.example.takeastep.activities.admin.adapters.ReadyContentAdapter;
import com.example.takeastep.databinding.ActivityAdminAreYouReadyBinding;
import com.example.takeastep.models.ReadyContent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminAreYouReadyActivity extends AppCompatActivity {
    ActivityAdminAreYouReadyBinding adminAreYouReadyBinding;
    ReadyContentAdapter mContentAdapter;
    FirebaseFirestore mFirestore;
    CollectionReference mCollectionReference;
    StorageReference mStorageReference;
    ArrayList<ReadyContent> mContent;
    ReadyContent selectedContent = new ReadyContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminAreYouReadyBinding = ActivityAdminAreYouReadyBinding.inflate(getLayoutInflater());
        setContentView(adminAreYouReadyBinding.getRoot());

        mFirestore = FirebaseFirestore.getInstance();
        mCollectionReference = mFirestore.collection("Ready Content");
        mStorageReference = FirebaseStorage.getInstance().getReference();


        setSupportActionBar(adminAreYouReadyBinding.toolBar);
        adminAreYouReadyBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        adminAreYouReadyBinding.recycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
                        Intent updateIntent = new Intent(getApplicationContext(), EditContentActivity.class);
                        String caption = mContent.get(position).getCaption();
                        String category = mContent.get(position).getCategory();
                        String videoUrl = mContent.get(position).getVideoUrl();
                        long time = mContent.get(position).getTime();

                        updateIntent.putExtra("caption", caption);
                        updateIntent.putExtra("category", category);
                        updateIntent.putExtra("videoUrl", videoUrl);
                        updateIntent.putExtra("time", time);
                        startActivity(updateIntent);

                        return true;
                    case R.id.delete:

                        mFirestore.collection("Categories").document(mContent.get(position).getCategory())
                                .update("numOfVideos", FieldValue.increment(-1))
                                .addOnSuccessListener(unused2 -> {
                                    mCollectionReference.document(String.valueOf(mContent.get(position).getTime())).delete()
                                            .addOnSuccessListener(unused -> {
                                                StorageReference videoRef = mStorageReference.child("ReadyContent/" + mContent.get(position).getTime().toString());
                                                videoRef.delete()
                                                        .addOnSuccessListener(unused1 -> {
                                                            mContent.remove(position);
                                                            mContentAdapter.notifyItemRemoved(position);
                                                            LauncherActivity.mapExoPlayersvideo.remove(position);
                                                            Toast.makeText(this, "Content deleted successfully", Toast.LENGTH_SHORT).show();
                                                            if (mContent.size() == 0) {
                                                                adminAreYouReadyBinding.errorText.setVisibility(View.VISIBLE);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(this, "Error while deleting item!", Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        return false;

                }
                return false;
            });
            popup.show();
        });

        LauncherActivity.releaseVideos(-1);
        loadContents();
    }

    private void loadContents() {
        mCollectionReference.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mContent.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        adminAreYouReadyBinding.errorText.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadContents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LauncherActivity.stopVideos(-1);
        loadContents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LauncherActivity.stopVideos(-1);
    }
}