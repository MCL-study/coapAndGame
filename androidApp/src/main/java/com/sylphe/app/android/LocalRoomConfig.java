package com.sylphe.app.android;

import com.sylphe.app.dto.RoomConfig;

import java.io.Serializable;

/**
 * Created by myks7 on 2016-05-19.
 */
class LocalRoomConfig implements Serializable{
    private int connectedRoomID;
    private double centerLocLat,centerLocLng;
    private int scale;

    LocalRoomConfig(RoomConfig roomConfig){
        connectedRoomID = roomConfig.getRoomID();
        centerLocLat = roomConfig.getCenterLoc().getLat();
        centerLocLng = roomConfig.getCenterLoc().getLng();
        scale = roomConfig.getScale();
    }

    int getConnectedRoomID() {
        return connectedRoomID;
    }

    int getScale() {
        return scale;
    }

    double getCenterLocLat() {
        return centerLocLat;
    }

    double getCenterLocLng() {
        return centerLocLng;
    }
}
