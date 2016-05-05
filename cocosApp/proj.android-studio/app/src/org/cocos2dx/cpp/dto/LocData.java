package org.cocos2dx.cpp.dto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by myks7 on 2016-03-13.
 */
public class LocData{
    private final Double lat, lng;

    public LocData(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public LocData(byte[] bytes){
        lat = ByteBuffer.wrap(bytes, 0, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        lng = ByteBuffer.wrap(bytes, 8, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    public byte[] getByteStream(){
        byte[] bytes = new byte[16];
        ToByteUtil.doubleToBytes_LE(lat, bytes, 0);
        ToByteUtil.doubleToBytes_LE(lng, bytes, 8);
        return bytes;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }


}
