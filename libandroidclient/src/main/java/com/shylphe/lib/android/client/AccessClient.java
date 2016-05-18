package com.shylphe.lib.android.client;

import com.sylphe.app.dto.UserProperties;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;

/**
 * Created by myks7 on 2016-03-15.
 */
public class AccessClient {
    private int id=-1;
    private final CoapClient client;


    public AccessClient(URI uri){
        client = new CoapClient(uri+"/AccessManager");
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
}
