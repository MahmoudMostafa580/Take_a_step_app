package com.example.takeastep.activities.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeastep.R;
import com.example.takeastep.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatMessage> chatMessages;
    private final String senderId;
    private Context mContext;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(ArrayList<ChatMessage> chatMessages, Context mContext, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_container_sent_message, parent, false));
        } else {
            return new ReceivedMessageViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_container_recieved_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ChatMessage currentMessage=chatMessages.get(position);

            ((SentMessageViewHolder) holder).messageText.setText(currentMessage.getMessage());
            ((SentMessageViewHolder) holder).timeText.setText(currentMessage.getDateTime());
        } else {
            ChatMessage currentMessage=chatMessages.get(position);
            ((ReceivedMessageViewHolder) holder).messageText.setText(currentMessage.getMessage());
            ((ReceivedMessageViewHolder) holder).timeText.setText(currentMessage.getDateTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
            timeText = itemView.findViewById(R.id.textDateTime);
        }

    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textMessage);
            timeText = itemView.findViewById(R.id.textDateTime);
        }


    }
}
