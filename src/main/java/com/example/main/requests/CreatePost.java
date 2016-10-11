package com.example.main.requests;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreatePost{

    private boolean isApproved;
    private String user;
    private String date;
    private String message;
    private boolean isSpam;
    private boolean isHighlighted;
    private Integer thread;
    private String forum;
    private boolean isDeleted;
    private boolean isEdited;



    private CreatePost(){
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
}