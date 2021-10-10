package com.example.takeastep.activities.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.takeastep.R;
import com.example.takeastep.activities.user.adapters.ChatAdapter;
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

    FragmentHelpCenterBinding helpCenterBinding;
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
        helpCenterBinding = FragmentHelpCenterBinding.inflate(getLayoutInflater());

        setContentView(helpCenterBinding.getRoot());

        androidx.appcompat.widget.Toolbar toolbar=new Toolbar(getApplicationContext());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitle("Chat");
        toolbar.setBackgroundColor(getColor(R.color.purple_200));


        firestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        Intent chatIntent=getIntent();
        userId=chatIntent.getStringExtra("userId");

        Log.w("hg",userId+"");

        chatRef = firestore.collection("chat users").document(userId);
        mDocumentReference=firestore.collection("users").document(userId).collection("chat").document();

        chatMessages = new ArrayList<>();
        listenMessages();

        helpCenterBinding.chatRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        helpCenterBinding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(chatMessages, this, Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid());
        helpCenterBinding.chatRecyclerView.setAdapter(chatAdapter);

        helpCenterBinding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });

    }

    public void sendMessage() {
        String message = helpCenterBinding.messageEditText.getText().toString();
        if (!message.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(mFirebaseAuth.getCurrentUser().getUid(), userId, message, System.currentTimeMillis());
            mDocumentReference.set(chatMessage)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            helpCenterBinding.messageEditText.setText(null);
                            helpCenterBinding.progressBar.setVisibility(View.GONE);
                            helpCenterBinding.chatRecyclerView.setVisibility(View.VISIBLE);
                            chatMessages.add(chatMessage);
                            chatAdapter.notifyDataSetChanged();
                            helpCenterBinding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "جاااااااااى", Toast.LENGTH_SHORT).show());
            //chatAdapter.notifyItemInserted(chatMessages.size()-1);

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
                        helpCenterBinding.progressBar.setVisibility(View.GONE);
                        helpCenterBinding.chatRecyclerView.setVisibility(View.VISIBLE);
                        ChatMessage message = documentSnapshot.toObject(ChatMessage.class);
                        chatMessages.add(message);

                    }
                    Log.v("list size", String.valueOf(chatMessages.size()));

                    chatAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error while loading messages", Toast.LENGTH_SHORT).show());

    }

}