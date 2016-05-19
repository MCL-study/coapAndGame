package com.sylphe.app.server;

import com.sylphe.app.dto.LocationMessage;
import com.sylphe.app.dto.MsgType;
import com.sylphe.app.dto.UserData;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.VALID;


/**
 * Created by myks7 on 2016-03-14.
 */
class GameObserveResource extends CoapResource {
    private RoomManager roomManager;

    GameObserveResource(String name, RoomManager roomManager) {
        super(name);
        this.roomManager = roomManager;
        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs
        getAttributes().setObservable(); // mark observable in the Link-Format

        // schedule a periodic update task, otherwise let events call changed()
        Timer timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 1000);
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            //System.out.println("update obs..."+getName());
            // .. periodic update of the resource
            changed(); // notify all observers
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {

      //    exchange.setMaxAge(1); // the Max-Age value should match the update interval
      //  exchange.respond("update "+getName() +"  "+exchange.getRequestOptions().getAccept());
        int roomId =exchange.getRequestOptions().getAccept();
        Room room = roomManager.searchRoom(roomId);
        if(room!=null){
            List<UserData> userList = room.getUserList();
            LocationMessage locationMessage = new LocationMessage(roomId,userList.size(),UserData.getSize());
            for(UserData data : userList){
                locationMessage.addUserDataStream(data.getStream());
            }
            ServerMonitor.log(roomId+"번 게임공간 총"+userList.size()+"개의 위치 정보 전송");
            exchange.respond(VALID,locationMessage.getStream());
        }
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        //delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
        exchange.respond(DELETED);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {

        int contentFormat = exchange.getRequestOptions().getContentFormat();
        if(contentFormat == MsgType.USER_DATA){
            byte[] requestPayload = exchange.getRequestPayload();
            LocationMessage locationMessage = new LocationMessage(requestPayload);
            List<UserData> userDataList = locationMessage.getUserDataList();
            UserData userData = userDataList.get(0);
            if(roomManager.existDeleteUser(userData.getId())){
                exchange.respond(DELETED);
                roomManager.removeDeleteUser(userData.getId());
            }else{
                int roomId = locationMessage.getRoomId();
                ServerMonitor.log(roomId+"번 게임공간 id:"+userData.getId()+" USER_DATA 메세지 받음 "+userData.getLocData().getLat()+","+userData.getLocData().getLng());
                roomManager.updateUserData(roomId, userData);
                exchange.respond(VALID);
            }
        }else if(contentFormat == MsgType.CATCH_FUGITIVE){
            String requestText = exchange.getRequestText();
            String[] split = requestText.split("/");
            int roomId = Integer.parseInt(split[0]);
            int fugitiveId = Integer.parseInt(split[1]);
            ServerMonitor.log(roomId+"번 게임공간 CATCH_FUGITIVE 메세지 받음 id:"+fugitiveId+"잡힘");
            roomManager.deleteUser(roomId,fugitiveId);
            exchange.respond(VALID);
        }else if(contentFormat == MsgType.DIE_PLAYER){
            String requestText = exchange.getRequestText();
            String[] split = requestText.split("/");
            int roomId = Integer.parseInt(split[0]);
            Integer playerId = Integer.parseInt(split[1]);
            ServerMonitor.log(roomId+"번 게임공간 DIE_PLAYER 메세지 받음 id:"+playerId+"죽음");
            roomManager.deleteUser(roomId,playerId);
            exchange.respond(DELETED);
        }
        //exchange.respond(CHANGED);
//        changed(); // notify all observers

    }
}
