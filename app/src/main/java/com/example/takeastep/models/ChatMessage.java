package com.example.takeastep.models;

public class ChatMessage {
    public String  senderId,receiverId,message,dateTime;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String receiverId, String message, String dateTime) {
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

    public String getDateTime() {
        return dateTime;
    }
}
