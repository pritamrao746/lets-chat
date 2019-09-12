package com.example.letschat;

public class UserProfile {
    private String user_name;
    private String status;
    private String image;
    private String thumb_image;
    private String uid;



    public UserProfile() {

        //for firebase
    }

    public UserProfile(String user_name, String status, String profile_image, String thumb_image) {
        this.user_name = user_name;
        this.status = status;
        this.image = profile_image;
        this.thumb_image = thumb_image;
        this.uid = uid;
    }


   /*  public UserProfile(String name,String status){
          this.User_name=name;
          this.status=status;
     }
    */


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String profile_image) {
        this.image = profile_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

