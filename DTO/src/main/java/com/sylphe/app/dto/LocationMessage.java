package com.sylphe.app.dto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-16.
 */
public class LocationMessage {
    private final int roomId;
    private StreamListConverter converter;
    private final int maxCnt;
    private final int objectSize;

    public LocationMessage(int roomId,int maxCnt, int objectSize){
        this.roomId = roomId;
        this.maxCnt=maxCnt;
        this.objectSize = objectSize;
        converter = new StreamListConverter(maxCnt,objectSize);

        //userDataList = new ArrayList<com.sylphe.app.dto.UserData>();
    }

    public void addUserDataStream(byte[] stream){
        converter.addStream(stream);
    }

    public LocationMessage(byte[] stream, int msgSize){
        roomId = ByteBuffer.wrap(stream,0,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] bytes = new byte[msgSize-4];
        System.arraycopy(stream,4,bytes,0,msgSize-4);
        converter = new StreamListConverter(bytes);
        maxCnt = converter.getMaxCnt();
        objectSize =  converter.getObjectSize();

    }

    public List<UserData> getUserDataList(){
        List<UserData> userDataList = new ArrayList<UserData>();
        List<byte[]> streamList = converter.getStreamList();
        for(byte[] userStream : streamList){
            userDataList.add(new UserData(userStream));
        }
        return userDataList;
    }

    public byte[] getStream(){
        byte[] userDataStream = converter.getStream();
        byte[] stream  = new byte[userDataStream.length + 4];
        ToByteUtil.intToBytes_LE(roomId,stream,0);
        System.arraycopy(userDataStream, 0, stream, 4, userDataStream.length);
        return stream;
    }

    public int getRoomId() {
        return roomId;
    }
}
