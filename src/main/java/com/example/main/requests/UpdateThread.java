package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public class UpdateThread {
    private String message;
    private String slug;
    private int thread;

    public UpdateThread() {
    }

    public UpdateThread(String message, String slug, int thread) {
        this.message = message;
        this.slug = slug;
        this.thread = thread;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public int getThread() {
        return thread;
    }
}
