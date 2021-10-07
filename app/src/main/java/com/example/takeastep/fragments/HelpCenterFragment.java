package com.example.takeastep.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.takeastep.activities.user.adapters.ChatAdapter;
import com.example.takeastep.databinding.FragmentHelpCenterBinding;
import com.example.takeastep.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class HelpCenterFragment extends Fragment {
    FragmentHelpCenterBinding helpCenterBinding;
    ArrayList<ChatMessage> chatMessages;
    ChatAdapter chatAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth mFirebaseAuth;
    CollectionReference mCollectionReference;
    DocumentReference mDocumentReference;

    public HelpCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        helpCenterBinding = FragmentHelpCenterBinding.inflate(inflater, container, false);

        firestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCollectionReference = firestore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid()).collection("chat");

        chatMessages = new ArrayList<>();
        listenMessages();

        helpCenterBinding.chatRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        helpCenterBinding.chatRecyclerView.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(chatMessages, getActivity(), Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid());
        helpCenterBinding.chatRecyclerView.setAdapter(chatAdapter);

        helpCenterBinding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });

        Log.v("list size", String.valueOf(chatMessages.size()));
        return helpCenterBinding.getRoot();
    }

    public void sendMessage() {
        String message = helpCenterBinding.messageEditText.getText().toString();
        if (!message.isEmpty()) {
            ChatMessage chatMessage = new ChatMessage(mFirebaseAuth.getCurrentUser().getUid(), "ALQyPwPRatn1H3oGIaOo", message, new Date().toString());
            DocumentReference mDocumentReference=firestore.collection("users").document(mFirebaseAuth.getCurrentUser().getUid()).collection("chat").document();
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
            .addOnFailureListener(e -> Toast.makeText(getContext(), "جاااااااااى", Toast.LENGTH_SHORT).show());
            //chatAdapter.notifyItemInserted(chatMessages.size()-1);

        } else {
            Toast.makeText(getContext(), "Leave message!", Toast.LENGTH_SHORT).show();
        }
    }

    private void listenMessages() {

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
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error while loading messages", Toast.LENGTH_SHORT).show());

    }

    /*private final EventListener<QuerySnapshot> eventListener=(value, error) ->{
        if (error!=null){
            return;
        }
        if (value!=null){
            int count=chatMessages.size();
            for (DocumentChange documentChange:value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.senderId=documentChange.getDocument().getString("sender id");
                    chatMessage.receiverId=documentChange.getDocument().getString("receiver id");
                    chatMessage.message=documentChange.getDocument().getString("message");
                    chatMessage.dateTime=getReadableDateTime(documentChange.getDocument().getDate("messageTime"));
                    chatMessages.add(chatMessage);
                }
            }
            chatMessages.sort(Comparator.comparing(obj -> obj.dateTime));
            if (count==0){
                chatAdapter.notifyDataSetChanged();
                Log.v("list size",String.valueOf(chatMessages.size()));

            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                helpCenterBinding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
        }
        helpCenterBinding.progressBar.setVisibility(View.GONE);
    };

    public String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, hh:mm a", Locale.getDefault()).format(date);
    }*/
}