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
    private CoapObserveRelation relation;
    private final Login login;
    private final CoapClient obsClient;

    public Client(URI uri){
        aliveFlag=true;
        login = new Login(uri);
        obsClient = new CoapClient(uri + "/obsRoom");
    }

    public void close(){
        relation.reactiveCancel();
        aliveFlag = false;
    }

    public boolean isAlive(){
        return aliveFlag;
    }

    private void login(){
        login.requestID();
    }
    private void obsTest(){
        relation = obsClient.observe(new handler());
    }

    public void process(){
        login();
        obsTest();
    }

    class handler implements CoapHandler {
        public void onLoad(CoapResponse response) {

            String content = response.getResponseText();
          //  System.out.println("-CO01----------");
           // System.out.println(content);

        }
        public void onError() {
            System.err.println("-Failed--------");
        }
    }

}
