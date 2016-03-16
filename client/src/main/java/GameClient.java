import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by myks7 on 2016-03-15.
 */
public class GameClient {
    private boolean aliveFlag;
    private CoapObserveRelation relation;
    private final CoapClient client;
    private final UserState userState;
    private Timer timer;
    private List<UserData> userList;

    public GameClient(URI uri, UserState userState){
        aliveFlag = false;
        client = new CoapClient(uri + "/gameObserve");
        this.userState = userState;
        userList= new ArrayList<UserData>();
    }
    public void start(int roomId,int id){
        aliveFlag = true;
        relation = client.observe(new handler(),roomId);
        timer = new Timer();
        timer.schedule(new NotifyLocationTask(), 0, 5000);
    }

    public void close(){
        relation.reactiveCancel();
        timer.cancel();
        aliveFlag = false;
    }

    public boolean isAlive() {
        return aliveFlag;
    }

    class handler implements CoapHandler {
        public void onLoad(CoapResponse response) {
            if( response.getCode() == ResponseCode.VALID){
                byte[] payload = response.getPayload();
                LocationMessage locationMessage = new LocationMessage(payload, payload.length);
                List<UserData> userDataList = locationMessage.getUserDataList();
                updateAllLocation(userDataList);
                for(UserData data : userDataList){
                    System.out.println(data.getId()+" "+data.getLocData().getLng()+" "+data.getLocData().getLat());
                }
            }
            //모든 위치 정보 올 예정

        }
        public void onError() {
            System.err.println("-Failed--------");
        }
    }
    class NotifyLocationTask extends TimerTask{
        @Override
        public void run() {
            GpsInfo gpsInfo = new GpsInfo();
            LocData location = gpsInfo.getLocation();
            updateCurrentLoc(location);
            UserData userData = new UserData(userState.getId(),userState.getUserProperties(),location);
            LocationMessage locationMessage = new LocationMessage(userState.getConnectedRoomId(),1, UserData.getSize());
            locationMessage.addUserDataStream(userData.getStream());
            client.put(locationMessage.getStream(),MsgType.USER_DATA);
        }
    }

    private void updateCurrentLoc(LocData location) {

    }

    private void updateAllLocation(List<UserData> userDataList){
        for(UserData data : userDataList){
            int id = data.getId();
            for(UserData userData : userList){
                if(userState.getId() == id){
                    if(userData.getId() == id) {
                        userData.setLocData(data.getLocData());
                    }
                }
            }
        }

    }
}
