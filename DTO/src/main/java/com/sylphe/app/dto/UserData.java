package com.sylphe.app.dto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by myks7 on 2016-03-16.
 */
public class UserData {
    private final int id;
    private final UserProperties userProperties;
    private LocData locData;

    public UserData(int id, UserProperties userProperties, LocData location){
        this.id = id;
        this.userProperties = userProperties;
        this.locData = location;
    }

    public UserData(byte[] userStream) {
        id = ByteBuffer.wrap(userStream,0,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        userProperties =UserProperties.valueOf(ByteBuffer.wrap(userStream,4,4).order(ByteOrder.LITTLE_ENDIAN).getInt());
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(userStream,8,16).get(bytes,0,bytes.length);
        locData = new LocData(bytes);
    }

    public UserData(int userId, UserProperties userProperties) {
        this.id = userId;
        this.userProperties = userProperties;
        locData = new LocData(0,0);
    }

    public byte[] getStream(){
        byte[] stream = new byte[4+4+16];
        ToByteUtil.intToBytes_LE(id,stream,0);
        ToByteUtil.intToBytes_LE(userProperties.value,stream,4);
        System.arraycopy(locData.getByteStream(),0,stream,8,16);
        return stream;
    }

    public static int getSize() {
        return 4+4+16;
    }

    public int getId() {
        return id;
    }

    public void setLocData(LocData locData) {
        this.locData = locData;
    }

    public LocData getLocData() {
        return locData;
    }

    public UserProperties getUserProperties() {
        return userProperties;
    }
    public int getUserPropertiesValue() {
        return userProperties.value;
    }
}
