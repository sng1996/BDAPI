package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public final class RemovePost {

    private int post;

    public RemovePost(){}


    public RemovePost(int post) {
        this.post = post;
    }


    public int getPost() {
        return post;
    }
}
