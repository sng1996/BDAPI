package com.example.main.requests;

import java.sql.Timestamp;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreateThread {
    private String forum;
    private String title;
    private boolean isClosed;
    private String user;
    private Timestamp tmpDate;
    private String date;
    private String message;
    private String slug;
    private boolean isDeleted;
    private int dislikes;
    private int id;
    private int likes;
    private int points;
    private int posts;



    public CreateThread() {
    }


    public CreateThread(String forum, String title, boolean isClosed, String user, String date, String message, String slug, boolean isDeleted) {
        this.forum = forum;
        this.title = title;
        this.isClosed = isClosed;
        this.user = user;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.isDeleted = isDeleted;
    }

    public String getForum() {
        return forum;
    }

    public String getTitle() {
        return title;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public Timestamp getTmpDate() { return tmpDate; }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public int getDislikes() {return dislikes;}

    public int getId() {return id;}

    public int getLikes() {return likes;}

    public int getPoints() {return points;}

    public int getPosts() {return posts;}

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public void setTmpDate(Timestamp tmpDate) {
        this.tmpDate = tmpDate;
    }
}
