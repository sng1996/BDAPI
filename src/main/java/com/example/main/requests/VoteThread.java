package com.example.main.requests;

/**
 * Created by sergeigavrilko on 19.10.16.
 */
public class VoteThread {
    private int vote;
    private int thread;


    public VoteThread() {
    }

    public VoteThread(int vote, int thread) {
        this.vote = vote;
        this.thread = thread;
    }

    public int getVote() {
        return vote;
    }

    public int getThread() {
        return thread;
    }
}
