package com.sylphe.app.server;

import com.sylphe.app.dto.LocData;
import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by myks7 on 2016-03-14.
 */
class Room {
    private int roomId;
    private int maxGameMember;
//    private int currentChaserNum, currentFugitiveNum;
    private LocData centerLoc;
    private int scale;
 //   private List<UserData> userList;
    private Map<Integer,User> userMap;

    private int timeLimit;

    Room(int roomId, RoomConfig config){
      //  userList = new ArrayList<UserData>();
        userMap = new HashMap<Integer, User>(10);//maxGameMember로 변경할 것
        this.roomId = roomId;
        maxGameMember = config.getMaxGameMember();
        scale = config.getScale();
        centerLoc = config.getCenterLoc();
        timeLimit = config.getTimeLimit();
    }

    int getRoomId() {
        return roomId;
    }

    RoomConfig getRoomConfig(){
        return new RoomConfig(roomId,centerLoc,maxGameMember,scale,timeLimit);
    }

    void addUser(User user){
     //   userList.add(user);
        userMap.put(user.getId(),user);
    }

    private UserData searchUser(int userId){
/*        for(UserData userData : userList){
            if(userData.getId() == userId){
                return userData;
            }
        }
        return null;
        */
        return userMap.get(userId);
    }

    void searchUserAndUpdateLoc(UserData userData) {
        UserData user = searchUser(userData.getId());
        if (user != null) {
            user.setLocData(userData.getLocData());
        }else{
            System.out.println("searchUserAndUpdate user null error");
        }
    }

    List<UserData> getUserList() {
        return new ArrayList<UserData>(userMap.values());
    }

    void dieUser(int userId){
        UserData userData = searchUser(userId);
        assert userData==null:"missing user";
        userData.setUserProperties(UserProperties.GHOST);
    }
    void exitUser(int userId){
/*        UserData user=null;
        for(UserData userData : userList){
            if(userData.getId() == userId){
                userData.setUserProperties(UserProperties.NOT_DEFINE);
                user=userData;
                break;
            }
        }
        userList.remove(user);*/
        UserData user = searchUser(userId);
        if(user!=null){
            user.setUserProperties(UserProperties.NOT_DEFINE);
            userMap.remove(user.getId());
        }
    }

    int getRoomID() {
        return roomId;
    }

    int getMaxGameMember() {
        return maxGameMember;
    }

    int getTimeLimit() {
        return timeLimit;
    }

    int getScale() {
        return scale;
    }
}
