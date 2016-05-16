package com.sylphe.app.server;

import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
class UserManager {

    private List<UserData> userList;
    private int id;
    UserManager(){
        userList = new ArrayList<UserData>();
        id=0;
    }

    UserData createUser(){
        ServerMonitor.log("사용자 생성 id:"+id);
        UserData user = new UserData(id, UserProperties.NOT_DEFINE);
        id++;
        addUser(user);
        return user;
    }

    private UserData searchUser(int id){
        for(UserData userData : userList){
            if(userData.getId() == id){
                return userData;
            }
        }
        return null;
    }

    private void addUser(UserData user){
        userList.add(user);
    }

    UserData updateUserUserProperties(int id, UserProperties userProperties){
        UserData user = searchUser(id);
        assert(user==null):"assert updateUserUserProperties use==null";
        ServerMonitor.log("update UserProperties");
        user.setUserProperties(userProperties);
        return user;
    }
    public List<UserData> getUserList(){
        ArrayList<UserData> result = new ArrayList<UserData>();
        result.addAll(userList);
        return result;
    }
}
