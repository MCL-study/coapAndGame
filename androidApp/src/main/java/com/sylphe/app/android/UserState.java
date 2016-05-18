package com.sylphe.app.android;

import android.os.Parcel;
import android.os.Parcelable;
import com.shylphe.lib.android.client.AccessClient;
import com.sylphe.app.dto.UserProperties;


import java.io.Serializable;
import java.net.URI;

/**
 * Created by myks7 on 2016-03-15.
 */
class UserState implements Serializable{
    private int id;
    private int connectedRoomId;
    private UserProperties userProperties;


    int getConnectedRoomId() {
        return connectedRoomId;
    }

    public void setConnectedRoomId(int connectedRoomId) {
        this.connectedRoomId = connectedRoomId;
    }

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
