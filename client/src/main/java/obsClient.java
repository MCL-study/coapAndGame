import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;

/**
 * Created by myks7 on 2016-03-15.
 */
public class ObsClient {
    private boolean aliveFlag;
    private CoapObserveRelation relation;
    private final CoapClient obsClient;
    public ObsClient(URI uri){
        aliveFlag=true;
        obsClient = new CoapClient(uri + "/obsRoom");
    }

    private void obsTest(){
        relation = obsClient.observe(new handler());
    }

    public void close(){
        relation.reactiveCancel();
        aliveFlag = false;
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
