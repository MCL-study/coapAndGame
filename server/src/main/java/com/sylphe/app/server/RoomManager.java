package com.sylphe.app.server;

import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
class RoomManager {
    private List<Integer> deleteUserList;
    private List<Room> roomList;
    private int roomId;
    RoomManager(){
        roomList= new ArrayList<Room>();
        deleteUserList = new ArrayList<Integer>();
        roomId=0;
    }
    Room createRoom(RoomConfig config){
        Room room = new Room(roomId,config);
        roomId++;
        roomList.add(room);
        ServerMonitor.log("방 생성 됨 spaceID"+room.getRoomId());
        return room;
    }

    Room searchRoom(int roomId){
        for (Room room : roomList) {
            if (room.getRoomId() == roomId)
                return room;
        }
/*        com.sylphe.app.server.Room room = roomList.get(roomId);
        if(room != null)
            return room;*/
        return null;
    }

    void enterRoom(int roomId, UserData userData){
        Room room = searchRoom(roomId);
        if (room != null) {
            room.addUser(userData);
        }
    }

    List<Room> getRoomList(){
        List<Room> rooms = new ArrayList<Room>();
        rooms.addAll(roomList);
        return rooms;
    }

    void updateUserData(int roomId, UserData userData) {
        Room room = searchRoom(roomId);
        if (room != null) {
            room.searchUserAndUpdate(userData);
        }
    }

    void deleteUser(int roomId, int userId){
        Room room = searchRoom(roomId);
        room.deleteUser(userId);
        deleteUserList.add(userId);
    }

    boolean existDeleteUser(int userId){
        return deleteUserList.contains(userId);
    }

    void removeDeleteUser(Integer userId){
        deleteUserList.remove(userId);
    }
}
