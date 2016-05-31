package com.sylphe.app.server;

import com.sylphe.app.dto.*;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;
import org.eclipse.californium.core.server.resources.Resource;

import java.util.*;

/**
 * Created by myks7 on 2016-03-15.
 */
class RoomManagerResource extends ConcurrentCoapResource {
    private RoomManager roomManager;
    private UserManager userManager;
    private Map<Integer, Long> roomTimeLimitMap;

    RoomManagerResource(String name, RoomManager roomManager, UserManager userManager){
        super(name,SINGLE_THREADED);
        roomTimeLimitMap = new HashMap<Integer, Long>();
        this.roomManager = roomManager;
        this.userManager = userManager;
        Timer timer = new Timer();
        timer.schedule(new RoomManagerResource.CheckTimeLimitTask(), 0, 1000);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        int format = exchange.getRequestOptions().getContentFormat();
        if(format == MsgType.MAKE_ROOM){
            RoomConfig config = new RoomConfig(exchange.getRequestPayload());
            ServerMonitor.log("게임공간 생성 요청 받음; 제한시간:"+config.getTimeLimit()+"초 최대참가인원"+config.getMaxGameMember()+"명 범위"+config.getScale()+"m");
            Room room = roomManager.createRoom(config);
            exchange.respond(ResponseCode.VALID,room.getRoomConfig().getByteStream());
        }else if(format == MsgType.ENTER_ROOM){
            String payload = exchange.getRequestText();
            String[] ids = payload.split("/");
            ServerMonitor.log(Integer.parseInt(ids[0])+"번 게임공간 접속 요청 받음 = id : "+Integer.parseInt(ids[1]));
            User user = userManager.updateUserUserProperties(Integer.parseInt(ids[1]), UserProperties.valueOf(Integer.parseInt(ids[2])));
            Room room = roomManager.enterRoom(Integer.parseInt(ids[0]),user);
            if(room!=null){
                Integer key = Integer.valueOf(ids[0]);
                Resource gameObserveResource = getChild(key.toString());
                if(gameObserveResource == null){
                    gameObserveResource = new GameObserveResource(key.toString(), room);
                    add(gameObserveResource);
                    Long timeLimit = roomTimeLimitMap.get(key);
                    if(timeLimit==null){
                        long value = System.currentTimeMillis() + room.getTimeLimit() * 1000;
                        roomTimeLimitMap.put(key, value);
                    }
                }
                exchange.respond(ResponseCode.VALID,room.getRoomConfig().getByteStream());
            }else{
                exchange.respond(ResponseCode.NOT_FOUND);
            }
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        //return roomLIst
        ServerMonitor.log("게임공간 목록 정보 요청 받음");
        List<Room> roomList = roomManager.getRoomList();
        if(roomList.size() != 0){
            StreamListConverter streamListConverter = new StreamListConverter(roomList.size(),roomList.get(0).getRoomConfig().getByteStream().length);
            for (Room room : roomList) {
                streamListConverter.addStream(room.getRoomConfig().getByteStream());
            }
            ServerMonitor.log("게임 공간 목록 반환");
            exchange.respond(ResponseCode.VALID, streamListConverter.getStream());
            return;
        }
        ServerMonitor.log("게임공간 하나도 존재하지 않음");
        exchange.respond(ResponseCode.NOT_FOUND);
    }

    private class CheckTimeLimitTask extends TimerTask {
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();

            List<Map.Entry<Integer, Long>> entryList = new ArrayList<Map.Entry<Integer, Long>>(roomTimeLimitMap.entrySet());
            for (Map.Entry<Integer, Long> temp : entryList) {
                if(currentTimeMillis>temp.getValue()){
                    GameObserveResource resource = (GameObserveResource) getChild(temp.getKey().toString());
                    resource.timeout();
                    roomTimeLimitMap.remove(temp.getKey());
                    ServerMonitor.log(temp.getKey()+"번 게임공간 Timeout");
                    delete(resource);
                    roomManager.deleteRoom(temp.getKey());
                }
            }
        }
    }
}
