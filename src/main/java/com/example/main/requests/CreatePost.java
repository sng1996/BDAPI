package com.example.main.requests;

import java.sql.Timestamp;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreatePost{

    private boolean isApproved;
    private String user;
    private Timestamp tmpDate;
    private String date;
    private String message;
    private boolean isSpam;
    private boolean isHighlighted;
    private Integer thread;
    private String forum;
    private boolean isDeleted;
    private boolean isEdited;
    private int dislikes;
    private int likes;
    private int parent;
    private int poitns;
    private int id;



    public CreatePost(){
    }

    public CreatePost(boolean isApproved, String user, String date, String message, boolean isSpam, boolean isHighlighted, Integer thread, String forum, boolean isDeleted, boolean isEdited){
        this.isApproved = isApproved;
        this.user = user;
        this.date = date;
        this.message = message;
        this.isSpam = isSpam;
        this.isHighlighted = isHighlighted;
        this.thread = thread;
        this.forum = forum;
        this.isDeleted = isDeleted;
        this.isEdited = isEdited;
    }


    public boolean getIsApproved() {
        return isApproved;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public Timestamp getTmpDate() {
        return tmpDate;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsSpam() {
        return isSpam;
    }

    public boolean getIsHighlighted() {
        return isHighlighted;
    }

    public Integer getThread() {
        return thread;
    }

    public String getForum() {
        return forum;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public boolean getIsEdited() {
        return isEdited;
    }

    public int getDislikes() {return dislikes;}

    public int getLikes() {return likes;}

    public int getParent() {return parent;}

    public int getPoitns() {return poitns;}

    public void setApproved(boolean approved) {
        isApproved = approved;
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

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setPoitns(int poitns) {
        this.poitns = poitns;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTmpDate(Timestamp tmpDate) {
        this.tmpDate = tmpDate;
    }
}