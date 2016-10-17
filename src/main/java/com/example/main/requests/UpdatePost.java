package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public class UpdatePost {
    private int post;
    private String message;


    public UpdatePost() {
    }

    public UpdatePost(int post, String message) {
        this.post = post;
        this.message = message;
    }

    public int getPost() {
        return post;
    }

    public String getMessage() {
        return message;
    }
}
