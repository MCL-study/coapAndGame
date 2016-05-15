package com.sylphe.app.server;

import com.sylphe.app.dto.*;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomManagerResource extends CoapResource {
    private RoomManager roomManager;
    private UserManager userManager;
    public RoomManagerResource(String name,RoomManager roomManager,UserManager userManager){
        super(name);
        this.roomManager = roomManager;
        this.userManager =userManager;
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        int format = exchange.getRequestOptions().getContentFormat();
        if(format == MsgType.MAKE_ROOM){
            RoomConfig config = new RoomConfig(exchange.getRequestPayload());
            Room room = roomManager.createRoom(config);
            exchange.respond(ResponseCode.VALID,room.getRoomConfig().getByteStream());
        }else if(format == MsgType.ENTER_ROOM){
            String payload = exchange.getRequestText();
            String[] ids = payload.split("/");
            UserData userData = userManager.updateUserUserProperties(Integer.parseInt(ids[1]), UserProperties.valueOf(Integer.parseInt(ids[2])));
            roomManager.enterRoom(Integer.parseInt(ids[0]),userData);
            exchange.respond(ResponseCode.VALID);
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        //return roomLIst
        List<Room> roomList = roomManager.getRoomList();
        if(roomList.size() != 0){
            StreamListConverter streamListConverter = new StreamListConverter(roomList.size(),roomList.get(0).getRoomConfig().getByteStream().length);
            for (Room room : roomList) {
                streamListConverter.addStream(room.getRoomConfig().getByteStream());
            }
            exchange.respond(ResponseCode.VALID, streamListConverter.getStream());
        }
        exchange.respond(ResponseCode.NOT_FOUND);
    }
}
