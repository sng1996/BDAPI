package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public class FollowUser {
    private String follower;
    private String followee;


    public FollowUser() {
    }

    public FollowUser(String follower, String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public String getFollower() {
        return follower;
    }

    public String getFollowee() {
        return followee;
    }
}
