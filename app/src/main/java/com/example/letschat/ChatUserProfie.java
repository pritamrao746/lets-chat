package com.example.letschat;

import com.google.firebase.database.ServerValue;

public class ChatUserProfie {
    private String user_name;
    private String thumb_image;
    private long  last_message_time;
    private String last_message;
    private String sender;
    private String seen;


    public ChatUserProfie() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
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
