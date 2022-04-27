package com.example.statusdemo;

public class imagemodel {
    private String image;

    public imagemodel(String image) {
        this.image = image;
    }

    public imagemodel(String uid, String IMAGEURI) {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
