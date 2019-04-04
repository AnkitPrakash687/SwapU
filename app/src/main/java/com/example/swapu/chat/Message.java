package com.example.swapu.chat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import static com.example.swapu.common.ComUtils.getFormattedDate;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "senderId";
    public static final String BODY_KEY = "body";
    public static final String CHAT_ID = "threadId";
    public static final String USER_NAME = "name";

    public String getUserName() {
        return getString(USER_NAME);
    }

    public void setUserName(String userName) {
        put(USER_NAME, userName);
    }

    public String getChatId() {
        return getString(CHAT_ID);
    }

    public void setChatId(String chatId) {
        put(CHAT_ID, chatId);
    }

    public String getTimestamp() {
        return getFormattedDate(getCreatedAt());
    }

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}
