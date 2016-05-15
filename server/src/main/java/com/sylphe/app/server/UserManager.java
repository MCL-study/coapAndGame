package com.sylphe.app.server;

import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class UserManager {

    private List<UserData> userList;
    private int id;
    public UserManager(){
        userList = new ArrayList<UserData>();
        id=0;
    }

    public UserData createUser(){
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

    public UserData updateUserUserProperties(int id,UserProperties userProperties){
        UserData user = searchUser(id);
        if(user != null){
            user.setUserProperties(userProperties);
        }
        return user;
    }
}
