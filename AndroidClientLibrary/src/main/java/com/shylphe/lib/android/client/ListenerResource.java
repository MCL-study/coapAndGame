package com.shylphe.lib.android.client;

import com.sylphe.app.dto.MsgType;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * Created by myks7 on 2016-05-27.
 */
abstract class ListenerResource extends CoapResource {

    ListenerResource() {
        // set resource identifier
        super("listener");
        // set display name
        getAttributes().setTitle("listener Resource");
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        int contentFormat = exchange.getRequestOptions().getContentFormat();
        if(contentFormat == MsgType.TIME_OUT){
            System.out.println("time out");
            onTimeout();
        }
        exchange.respond(CoAP.ResponseCode.VALID);
    }
    abstract void onTimeout();

}