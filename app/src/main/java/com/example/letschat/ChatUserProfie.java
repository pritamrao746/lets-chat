package com.example.letschat;

import com.google.firebase.database.ServerValue;

public class ChatUserProfie {
    private String chat_id;
    private String userId;
    private long  last_message_time;
    private String last_message;
    private String sender;
    private String seen;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public ChatUserProfie() {
    }



    public long getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(long last_message_time) {
        this.last_message_time = last_message_time;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
