package com.shylphe.lib.android.client;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * Created by myks7 on 2016-05-27.
 */
class ListenerResource extends CoapResource {

    ListenerResource() {
        // set resource identifier
        super("listener");
        // set display name
        getAttributes().setTitle("listener Resource");
    }

    @Override
    public void handleGET(CoapExchange exchange) {

        // respond to the request
        exchange.respond("Hello Android!");
    }
}