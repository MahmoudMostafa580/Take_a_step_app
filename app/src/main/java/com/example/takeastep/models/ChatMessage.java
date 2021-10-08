package com.example.takeastep.models;

public class ChatMessage {
    public String  senderId,receiverId,message;
    Long dateTime;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, Long dateTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
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
