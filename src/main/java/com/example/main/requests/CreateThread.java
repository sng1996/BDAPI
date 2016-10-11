package com.example.main.requests;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreateThread {
    private String forum;
    private String title;
    private boolean isClosed;
    private String user;
    private String date;
    private String message;
    private String slug;
    private boolean isDeleted;


    private CreateThread() {
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

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
