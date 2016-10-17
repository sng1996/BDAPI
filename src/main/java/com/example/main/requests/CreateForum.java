package com.example.main.requests;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public final class CreateForum{
    private String name;
    private String short_name;
    private String user;
    private int id;

    public CreateForum(){
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

    public void setName(String name) {
        this.name = name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
