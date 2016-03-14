import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED;

/**
 * Created by myks7 on 2016-03-14.
 */
public class HelloWorldResource extends CoapResource {

    public HelloWorldResource() {

        // set resource identifier
        super("helloWorld");

        // set display name
        getAttributes().setTitle("Hello-World Resource");
    }
    String str = null;
    @Override
    public void handleGET(CoapExchange exchange) {

        // respond to the request
        //exchange.respond("Hello World! test test test test");
        exchange.respond(str+" ...ok!");

    }


    @Override
    public void handlePUT(CoapExchange exchange) {
        int format = exchange.getRequestOptions().getContentFormat();
        if(format == MsgType.SEND_LOCATION){
            byte[] payload = exchange.getRequestPayload();
            LocData data = new LocData(payload);
            str = data.getLat()+" "+data.getLng()+" "+format;
            System.out.println( " data : " + str);
            exchange.respond(CHANGED,str);

        }

    }
}