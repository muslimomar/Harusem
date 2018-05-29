package com.example.william.harusem.models;

/**
 * Created by william on 5/17/2018.
 */

public class User {

    public String name;
    public String image;
    public String thumb_image;

    public User(String name, String image, String thumb_image) {
        this.name = name;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public User() {

    }
}
