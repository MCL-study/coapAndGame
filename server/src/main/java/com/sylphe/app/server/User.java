package com.sylphe.app.server;

import com.sylphe.app.dto.UserProperties;

/**
 * Created by myks7 on 2016-03-15.
 */
public class User {
    private int id;
    private UserProperties userProperties;

    public User(int id,UserProperties userProperties){
        this.id=id;
        this.userProperties = userProperties;
    }

    public int getId() {
        return id;
    }
}
