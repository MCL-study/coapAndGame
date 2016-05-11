package com.shylphe.lib.android.client;

import com.sylphe.app.dto.UserProperties;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;

/**
 * Created by myks7 on 2016-03-15.
 */
public class Login {
    private int id=-1;
    private final CoapClient client;
    private int connectedRoomId;
    private UserProperties userProperties;

    public Login(URI uri){
        client = new CoapClient(uri+"/LoginManager");
    }

    public void login(){
     //   client.put("",MsgType.REQUEST_ID);
        CoapResponse response = client.get();
        if (response!=null) {
            id = Integer.valueOf(response.getResponseText());
        }
    }

    public int getId(){
        return id;
    }

    public int getConnectedRoomId() {
        return connectedRoomId;
    }

    public void setConnectedRoomId(int connectedRoomId) {
        this.connectedRoomId = connectedRoomId;
    }

    public UserProperties getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(UserProperties userProperties) {
        this.userProperties = userProperties;
    }
}
