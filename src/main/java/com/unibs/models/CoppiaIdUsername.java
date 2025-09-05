package com.unibs.models;

public class CoppiaIdUsername {

    private final Integer id;
    private final String username;

    public CoppiaIdUsername(Integer id, String username) {
        this.id = id;
        this.username = username;
    }
    public Integer getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

}