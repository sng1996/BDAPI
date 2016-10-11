package com.example.main.requests;

import java.lang.reflect.Array;

/**
 * Created by sergeigavrilko on 11.10.16.
 */
public class DetailsForum {
    private String forum;
    private Array related;


    public DetailsForum() {
    }

    public DetailsForum(String forum, Array related) {
        this.forum = forum;
        this.related = related;
    }

    public String getForum() {
        return forum;
    }

    public Array getRelated() {
        return related;
    }
}
