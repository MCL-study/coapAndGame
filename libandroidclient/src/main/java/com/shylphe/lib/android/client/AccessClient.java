package com.shylphe.lib.android.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;

/**
 * Created by myks7 on 2016-03-15.
 */
public class AccessClient {

    private final CoapClient client;


    public AccessClient(URI uri){
        client = new CoapClient(uri+"/AccessManager");
    }

    public Integer login(){
     //   client.put("",MsgType.REQUEST_ID);
        CoapResponse response = client.get();
        if (response!=null) {
            int id = Integer.valueOf(response.getResponseText());
            return id;
        }
        return null;
    }
    public void close(){
        client.delete();
    }

}
