package com.sylphe.app.android;

import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserProperties;


import java.io.Serializable;

/**
 * Created by myks7 on 2016-03-15.
 */
class UserState implements Serializable{
    private int id;
    private UserProperties userProperties;

    UserProperties getUserProperties() {
        return userProperties;
    }

    void setUserProperties(UserProperties userProperties) {
        this.userProperties = userProperties;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

}
