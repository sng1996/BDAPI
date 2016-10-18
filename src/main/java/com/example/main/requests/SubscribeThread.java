package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public class SubscribeThread {
    private int thread;
    private String user;

    public SubscribeThread() {
    }

    public SubscribeThread(int thread, String user) {
        this.thread = thread;
        this.user = user;
    }

    public int getThread() {
        return thread;
    }

    public String getUser() {
        return user;
    }
}
