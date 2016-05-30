package com.sylphe.app.server;

import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.util.*;

/**
 * Created by myks7 on 2016-03-15.
 */
class RoomManager {
//    private List<Integer> deleteUserList;
    private Map<Integer,Room> roomMap;
    private int roomId;

    RoomManager(){
        roomMap= new HashMap<Integer, Room>();
 //       deleteUserList = new ArrayList<Integer>();
        roomId=1;
    }
    Room createRoom(RoomConfig config){
        Room room = new Room(roomId,config);
        roomId++;
        roomMap.put(room.getRoomID(),room);
        ServerMonitor.log("게임공간 생성 됨 spaceID : "+room.getRoomId());
        return room;
    }

    Room searchRoom(int roomId){
        Room room = roomMap.get(roomId);
        if (room != null)
            return room;
        return null;
    }

    Room enterRoom(int roomId, User user){
        Room room = searchRoom(roomId);
        if (room != null) {
            room.addUser(user);
            return room;
        }
        return null;
    }

    List<Room> getRoomList(){
        return new ArrayList<Room>(roomMap.values());
    }

    void updateUserLocation(int roomId, UserData userData) {
        Room room = searchRoom(roomId);
        if (room != null) {
            room.searchUserAndUpdateLoc(userData);
        }
    }

    void dieUser(int roomId, int userId){
        Room room = searchRoom(roomId);
        room.dieUser(userId);
    }
    void exitUser(int roomId, int userId){
        Room room = searchRoom(roomId);
        room.exitUser(userId);
    }
}
