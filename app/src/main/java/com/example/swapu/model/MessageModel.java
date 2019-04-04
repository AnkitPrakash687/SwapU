package com.example.swapu.model;

import android.graphics.Bitmap;

import java.util.Date;

public class MessageModel {

String lastMessage;
Bitmap image;
String postDate;
String receiver;
String objectId;
String receiverName;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public MessageModel(String receiver, String receiverName, String lastMessage, String postDate, Bitmap image, String objectId){
    this.receiver = receiver;
    this.receiverName = receiverName;
    this.lastMessage = lastMessage;
    this.image = image;
    this.postDate = postDate;
    this.objectId = objectId;
}

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return receiver;
    }
}
