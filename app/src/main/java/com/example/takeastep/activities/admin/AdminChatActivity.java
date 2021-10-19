package com.example.takeastep.activities.admin;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.activities.user.adapters.ChatAdapter;
import com.example.takeastep.databinding.ActivityAdminChatBinding;
import com.example.takeastep.databinding.FragmentHelpCenterBinding;
import com.example.takeastep.models.ChatMessage;
import com.example.takeastep.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminChatActivity extends AppCompatActivity {

    ActivityAdminChatBinding adminChatBinding;
    ArrayList<ChatMessage> chatMessages;
    ChatAdapter chatAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth mFirebaseAuth;
    CollectionReference mCollectionReference;
    DocumentReference chatRef;
    DocumentReference mDocumentReference;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminChatBinding = ActivityAdminChatBinding.inflate(getLayoutInflater());
        setContentView(adminChatBinding.getRoot());


        setSupportActionBar(adminChatBinding.toolBar);
        adminChatBinding.toolBar.setNavigationOnClickListener(v -> onBackPressed());

        firestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        Intent chatIntent=getIntent();
        userId=chatIntent.getStringExtra("userId");
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName=documentSnapshot.getString("name");
                    adminChatBinding.toolBar.setTitle(userName);
                });

        Log.w("hg",userId+"");

        chatRef = firestore.collection("chat users").document(userId);
        mDocumentReference=firestore.collection("users").document(userId).collection("chat").document();

        chatMessages = new ArrayList<>();
        listenMessages();

        adminChatBinding.chatRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adminChatBinding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(chatMessages, this, Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid());
        adminChatBinding.chatRecyclerView.setAdapter(chatAdapter);

        adminChatBinding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });

    }

    public void sendMessage() {
        String message = adminChatBinding.messageEditText.getText().toString();
        if (!message.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(mFirebaseAuth.getCurrentUser().getUid(), userId, message, System.currentTimeMillis(),false);
            mDocumentReference.set(chatMessage)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            adminChatBinding.messageEditText.setText(null);
                            adminChatBinding.progressBar.setVisibility(View.GONE);
                            adminChatBinding.chatRecyclerView.setVisibility(View.VISIBLE);
                            chatMessages.add(chatMessage);
                            chatAdapter.notifyDataSetChanged();
                            adminChatBinding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "جاااااااااى", Toast.LENGTH_SHORT).show());

            Map<String,Object> user =new HashMap<>();
            user.put("lastMessage",message);
            user.put("lastMessageTime",System.currentTimeMillis());
            user.put("id",userId);
            chatRef.update(user);

        } else {
            Toast.makeText(this, "Leave message!", Toast.LENGTH_SHORT).show();
        }
    }

    private void listenMessages() {
        mCollectionReference = firestore.collection("users").document(userId).collection("chat");

        mCollectionReference.orderBy("dateTime", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    chatMessages.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        adminChatBinding.progressBar.setVisibility(View.GONE);
                        adminChatBinding.chatRecyclerView.setVisibility(View.VISIBLE);
                        ChatMessage message = documentSnapshot.toObject(ChatMessage.class);
                        message.setSeen(true);
                        Map<String, Object> seen=new HashMap<>();
                        seen.put("seen",true);
                        documentSnapshot.getReference().update(seen);
                        chatMessages.add(message);

                    }
                    Log.v("list size", String.valueOf(chatMessages.size()));

                    chatAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading messages", Toast.LENGTH_SHORT).show());

    }

}