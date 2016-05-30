package com.sylphe.app.server;

import com.sylphe.app.dto.UserData;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.ConcurrentCoapResource;

/**
 * Created by myks7 on 2016-03-15.
 */
class AccessManagerResource extends ConcurrentCoapResource {
    private UserManager userManager;
    AccessManagerResource(String name, UserManager userManager){
        super(name,SINGLE_THREADED);
        this.userManager = userManager;
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        ServerMonitor.log("로그인 요청 받음");
        UserData user =  userManager.createUser(exchange.getSourceAddress(),exchange.getSourcePort());
        Integer integer = user.getId();
        ServerMonitor.log("respond");
        exchange.respond(CoAP.ResponseCode.VALID,integer.toString());
    }
}
