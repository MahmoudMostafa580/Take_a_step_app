package com.example.takeastep.models;

public class ChatMessage {
    public String  senderId,receiverId,message;
    Long dateTime;
    boolean seen;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, Long dateTime,boolean seen) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
        this.seen=seen;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Long getDateTime() {
        return dateTime;
    }
}
