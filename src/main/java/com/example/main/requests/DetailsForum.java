package com.example.main.requests;

import java.lang.reflect.Array;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public class DetailsForum {
    private String forum;
    private String[] related = new String[1];


    public DetailsForum() {
    }

    public DetailsForum(String forum, String[] related) {
        this.forum = forum;
        this.related = related;
    }

    public String getForum() {
        return forum;
    }

    public String[] getRelated() {
        return related;
    }
}
