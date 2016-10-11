package com.example.main.requests;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreateForum{
    private String name;
    private String short_name;
    private String user;

    private CreateForum(){
    }

    private CreateForum(String name, String short_name, String user){
        this.name = name;
        this.short_name = short_name;
        this.user = user;
    }


    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getUser() {
        return user;
    }
}
