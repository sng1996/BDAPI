package com.example.main.requests;

/**
 * Created by sergeigavrilko on 18.10.16.
 */
public class UpdateUser {
    private String about;
    private String user;
    private String name;


    public UpdateUser() {
    }

    public UpdateUser(String about, String user, String name) {
        this.about = about;
        this.user = user;
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }
}
