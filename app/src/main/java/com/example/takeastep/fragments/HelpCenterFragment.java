package com.example.takeastep.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.takeastep.R;
import com.example.takeastep.adapters.ChatAdapter;
import com.example.takeastep.databinding.FragmentHelpCenterBinding;
import com.example.takeastep.models.ChatMessage;
import com.example.takeastep.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class HelpCenterFragment extends Fragment {
    FragmentHelpCenterBinding helpCenterBinding;
    ArrayList<ChatMessage> chatMessages;
    ChatAdapter chatAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth mFirebaseAuth;

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
        helpCenterBinding=FragmentHelpCenterBinding.inflate(inflater,container,false);
        chatMessages=new ArrayList<>();

        firestore=FirebaseFirestore.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();

        chatAdapter=new ChatAdapter(chatMessages, Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid());
        helpCenterBinding.chatRecyclerView.setAdapter(chatAdapter);

        helpCenterBinding.layoutSend.setOnClickListener(v -> {
            sendMessage();
        });

        listenMessages();

        return helpCenterBinding.getRoot();
    }

    public void sendMessage(){
        HashMap<String,Object> message =new HashMap<>();
        message.put("sender id",mFirebaseAuth.getCurrentUser().getUid());
        message.put("receiver id","ALQyPwPRatn1H3oGIaOo");
        message.put("message",helpCenterBinding.messageEditText.getText().toString());
        message.put("messageTime",new Date());

        firestore.collection("chat").add(message);
        helpCenterBinding.messageEditText.setText(null);
    }

    private void listenMessages(){
        firestore.collection("chat")
                .whereEqualTo("sender id",mFirebaseAuth.getCurrentUser().getUid())
                .whereEqualTo("receiver id","ALQyPwPRatn1H3oGIaOo")
                .addSnapshotListener(eventListener);
        firestore.collection("chat")
                .whereEqualTo("sender id","ALQyPwPRatn1H3oGIaOo")
                .whereEqualTo("receiver id",mFirebaseAuth.getCurrentUser().getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener=(value, error) ->{
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
            Collections.sort(chatMessages,(obj1,obj2) ->obj1.dateTime.compareTo(obj2.dateTime));
            if (count==0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                helpCenterBinding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
        }
        helpCenterBinding.progressBar.setVisibility(View.GONE);
    };

    public String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}