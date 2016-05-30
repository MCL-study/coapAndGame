package com.sylphe.app.server;

import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
class UserManager {

    private List<User> userList;
    private int id;
    UserManager(){
        userList = new ArrayList<User>();
        id=1;
    }

    UserData createUser(InetAddress sourceAddress, int sourcePort){
        ServerMonitor.log("사용자 생성 id:"+id);
        //UserData user = new UserData(id, UserProperties.NOT_DEFINE);
        User user = new User(sourceAddress,sourcePort,id, UserProperties.NOT_DEFINE);
        id++;
        userList.add(user);
        return user;
    }

    private User searchUser(int id){
        for(User user : userList){
            if(user.getId() == id){
                return user;
            }
        }
        return null;
    }


    User updateUserUserProperties(int id, UserProperties userProperties){
        User user = searchUser(id);
        assert(user==null):"assert updateUserUserProperties use==null";
        ServerMonitor.log("update UserProperties");
        user.setUserProperties(userProperties);
        return user;
    }
    List<User> getUserList(){
        ArrayList<User> result = new ArrayList<User>();
        result.addAll(userList);
        return result;
    }
}
