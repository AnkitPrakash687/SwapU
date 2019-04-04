package com.example.swapu.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class ItemModel implements Serializable {

String title;
Bitmap image;
Date postDate;
    String price;

    String objectId;

    public ItemModel(String title, Date postDate, Bitmap image, String objectId, String price) {
    this.title = title;
    this.image = image;
    this.postDate = postDate;
        this.objectId = objectId;
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}
