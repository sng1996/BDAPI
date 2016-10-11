package com.example.main.requests;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreateUser{
    private String username;
    private String about;
    private boolean isAnonymous;
    private String name;
    private String email;

    private CreateUser(){
    }

    public CreateUser(String username, String about, boolean isAnonymous, String name, String email) {
        this.username = username;
        this.about = about;
        this.isAnonymous = isAnonymous;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public boolean getIsAnonymous() {
        return isAnonymous;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

