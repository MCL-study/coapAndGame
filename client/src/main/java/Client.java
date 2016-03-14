import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;

/**
 * Created by myks7 on 2016-03-14.
 */
public class Client {
    private boolean aliveFlag;
    private final Login login;

    public Client(URI uri){
        aliveFlag=true;
        login = new Login(uri);

    }

    public void close(){
        aliveFlag = false;
    }

    public boolean isAlive(){
        return aliveFlag;
    }

    private void login(){
        login.requestID();
    }


    public void process(){
        login();
    }



}
