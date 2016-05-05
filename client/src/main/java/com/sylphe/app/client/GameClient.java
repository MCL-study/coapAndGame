package com.sylphe.app.client;

import com.sylphe.app.dto.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.VALID;

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
    private UserData player;

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
        timer.schedule(new NotifyLocationTask(), 0, 1000);
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
            CoapResponse response = client.put(locationMessage.getStream(), MsgType.USER_DATA);
            if(response!=null){
                if(response.getCode() == DELETED){
                    endGame();
                }else if(response.getCode() == VALID){
                    for (UserData userData1 : userList) {
                        if (userData1.getId() == userState.getId()) {
                            userData1.setLocData(userData.getLocData());
                            player = userData1;
                        }
                    }
                }
            }
        }
    }

    private void endGame() {
        timer.cancel();
        relation.reactiveCancel();
        aliveFlag =false;
    }

    private void updateCurrentLoc(LocData location) {

    }

    private void updateAllLocation(List<UserData> userDataList){
        for (UserData data : userDataList) {
            boolean exist=false;
            int id = data.getId();
            for (UserData userData : userList) {
                if (userState.getId() != id) {
                    if (userData.getId() == id) {
                        userData.setLocData(data.getLocData());
                        exist=true;
                    }
                }
            }
            if(!exist){
                userList.add(data);
            }
            if(player!=null){
                if(player.getUserProperties() == UserProperties.CHASER){
                    if(data.getUserProperties() == UserProperties.FUGITIVE){
                        LocData locData = data.getLocData();
                        LocData playerLocData = player.getLocData();
                        double diffLat= locData.getLat() - playerLocData.getLat();
                        double diffLng= locData.getLng() - playerLocData.getLng();
                        double distance = Math.sqrt(diffLat*diffLat + diffLng*diffLng);
                        if(distance < 0.1){
                            client.put(userState.getConnectedRoomId()+"/"+data.getId(), MsgType.CATCH_FUGITIVE);
                        }
                    }
                }
            }
        }
    }
}
