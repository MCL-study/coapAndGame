package com.sylphe.app.client;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by myks7 on 2016-03-14.
 */
public class ClientMain {
    public static void main(String[] args){
        URI uri = null; // URI parameter of the request

        if (args.length > 0) {
            // input URI from command line arguments
            try {
                uri = new URI(args[0]);
            } catch (URISyntaxException e) {
                System.err.println("Invalid URI: " + e.getMessage());
                System.exit(-1);
            }
            Client client = new Client(uri);
            client.process();
            try {
                while (client.isAlive())
                    Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }else{
            System.exit(-1);
        }

    }
}
