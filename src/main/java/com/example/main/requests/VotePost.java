package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public class VotePost {
    private int vote;
    private int post;

    public VotePost() {
    }

    public VotePost(int vote, int post) {
        this.vote = vote;
        this.post = post;
    }

    public int getVote() {
        return vote;
    }

    public int getPost() {
        return post;
    }
}
