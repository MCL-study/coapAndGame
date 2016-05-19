package com.shylphe.lib.android.client;


import com.sylphe.app.dto.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomConnector {
    private final CoapClient client;

    public RoomConnector(URI uri){
        client = new CoapClient(uri+"/RoomManager");
    }

    public Integer makeRoom(LocData centerLoc, int maxGameMember, int scale, int timeLimit){
        RoomConfig config = new RoomConfig(centerLoc,maxGameMember,scale,timeLimit);
        CoapResponse response = client.put(config.getByteStream(), MsgType.MAKE_ROOM);
        if(response!=null){
            if(response.getCode() == ResponseCode.VALID){
                RoomConfig roomConfig = new RoomConfig(response.getPayload());
                System.out.println("방 만들기 성공");
                return roomConfig.getRoomID();
            }
        }else{
            System.out.println("방 만들기 실패");
        }
        return null;
    }

    public RoomConfig enterRoom(int roomId,int id,UserProperties userProperties){
        CoapResponse response = client.put(roomId + "/" + id + "/" + userProperties.value, MsgType.ENTER_ROOM);
        if(response!=null) {
            if (response.getCode() == ResponseCode.VALID) {
                System.out.println("접속 요청 완료");
                return new RoomConfig(response.getPayload());
            }
        }
        return null;
    }

    public List<RoomConfig> getRoomList(){
        List<RoomConfig> configs = new ArrayList<RoomConfig>();
        CoapResponse response = client.get();
        if(response!=null){
            if(response.getCode() == ResponseCode.VALID){
                StreamListConverter streamListConverter = new StreamListConverter(response.getPayload());
                List<byte[]> byteStreamList = streamListConverter.getStreamList();
                for(byte[] stream : byteStreamList){
                    configs.add(new RoomConfig(stream));
                }
                return configs;
            }else if(response.getCode() == ResponseCode.NOT_FOUND){
                System.out.println("방 없음");
                return null;
            }else{
                System.out.println("알수 없는 에러");
                return null;
            }
        }else{
            System.out.println("응답 없음");
            return null;
        }
    }
    public void close(){
        client.delete();
    }
}
