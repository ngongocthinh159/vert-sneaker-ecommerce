package com.rmit.ecommerce.fragment;

import com.google.firebase.Timestamp;

public class NotificationModel implements Comparable<NotificationModel> {
    private String title;
    private String content;
    private Timestamp createdAt;

    public NotificationModel(String title, String content, Timestamp createdAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public NotificationModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public int compareTo(NotificationModel o) {
        return -this.getCreatedAt().compareTo(o.getCreatedAt());
    }
}
