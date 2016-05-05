package com.sylphe.app.dto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-16.
 */
public class StreamListConverter {
    private int cnt=0;
    private final int maxCnt;
    private final int objectSize;
    private byte[] stream;
    public StreamListConverter(int maxCnt, int objectSize){
        this.maxCnt=maxCnt;
        this.objectSize = objectSize;
        stream = new byte[maxCnt* this.objectSize];
    }
    public StreamListConverter(byte[] stream){
        maxCnt = ByteBuffer.wrap(stream,0,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        objectSize = ByteBuffer.wrap(stream,4,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        this.stream = new byte[objectSize*maxCnt];
        ByteBuffer.wrap(stream,8,maxCnt*objectSize).get(this.stream);
    }
    public void addStream(byte[] stream){
        if(maxCnt>cnt){
            int offset = objectSize *cnt;
            System.arraycopy(stream, 0, this.stream, offset, objectSize);
            cnt++;
        }
    }
    public List<byte[]> getStreamList(){
        List<byte[]> list = new ArrayList<byte[]>();
        for(int i=0;i<maxCnt;i++){
            int offset = objectSize *i;
            byte[] bytes= new byte[objectSize];
            ByteBuffer.wrap(stream,offset, objectSize).get(bytes);
            list.add(bytes);
        }
        return list;
    }

    public byte[] getStream(){
        byte[] result= new byte[stream.length+8];
        ToByteUtil.intToBytes_LE(maxCnt,result,0);
        ToByteUtil.intToBytes_LE(objectSize,result,4);
        System.arraycopy(stream,0,result,8,stream.length);
        return result;
    }
    public int getMaxCnt(){
        return maxCnt;
    }

    public int getObjectSize() {
        return objectSize;
    }
}
