package com.sylphe.app.server;

import com.sylphe.app.dto.UserData;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * Created by myks7 on 2016-03-15.
 */
class LoginManagerResource extends CoapResource {
    private UserManager userManager;
    LoginManagerResource(String name, UserManager userManager){
        super(name);
        this.userManager = userManager;
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        UserData user =  userManager.createUser();
        Integer integer = user.getId();
        exchange.respond(integer.toString());
    }
}
