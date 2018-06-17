package com.example.vikesh.chaton.models;

public class User {
   public String username,status,imageurl,thumb_image;

    public User(String username, String status, String imageurl) {
        this.username = username;
        this.status = status;
        this.imageurl = imageurl;
    }

    public User(String username, String status, String imageurl, String thumb_image) {
        this.username = username;
        this.status = status;
        this.imageurl = imageurl;
        this.thumb_image = thumb_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
