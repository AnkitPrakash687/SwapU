package com.example.swapu.model;

import android.graphics.Bitmap;


import java.util.Date;

public class ItemModel {

String title;
Bitmap image;
Date postDate;

public ItemModel(String title, Date postDate, Bitmap image){
    this.title = title;
    this.image = image;
    this.postDate = postDate;
}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    @Override
    public String toString() {
        return title;
    }
}
