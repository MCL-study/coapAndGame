package com.shylphe.lib.android.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;

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
            if(response.getCode() == CoAP.ResponseCode.VALID){
                int id = Integer.valueOf(response.getResponseText());
                return id;
            }else{
                System.out.println("알수 없는 이유 : 로그인 실패");
            }
        }else{
            System.out.println("액세스 서버 무응답");
        }
        return null;
    }
    public void close(){
        client.delete();
    }

}
