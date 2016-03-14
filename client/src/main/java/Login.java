import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;

/**
 * Created by myks7 on 2016-03-15.
 */
public class Login {
    private int id;
    private final CoapClient client;

    public Login(URI uri){
        client = new CoapClient(uri+"/LoginManager");
    }

    public void requestID(){
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
