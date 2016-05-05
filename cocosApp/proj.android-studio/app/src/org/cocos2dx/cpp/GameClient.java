package org.cocos2dx.cpp;

import android.util.Log;

import org.cocos2dx.cpp.dto.LocData;
import org.cocos2dx.cpp.dto.LocationMessage;
import org.cocos2dx.cpp.dto.MsgType;
import org.cocos2dx.cpp.dto.UserData;
import org.cocos2dx.cpp.dto.UserProperties;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.VALID;

/**
 * Created by myks7 on 2016-03-15.
 */
public class GameClient{
    private GpsInfo gpsInfo;
    private boolean aliveFlag;
    private CoapObserveRelation relation;
    private CoapClient client;
    private Timer timer;
    private int roomId;
    private UserData player;


    public GameClient(URI uri, GpsInfo gpsInfo){
        aliveFlag = false;
        client = new CoapClient(uri + "/gameObserve");
        this.gpsInfo = gpsInfo;
    }

    public void start(int roomId, int id,int userProperties) {
        this.roomId = roomId;
        player = new UserData(id,userProperties);
        aliveFlag = true;
        relation = client.observe(new handler(), roomId);
//        timer = new Timer();
//        timer.schedule(new NotifyLocationTask(), 0, 1000);
    }

    public void close() {
//        timer.cancel();
        relation.reactiveCancel();
        aliveFlag = false;
    }

    public boolean isAlive() {
        return aliveFlag;
    }

    class handler implements CoapHandler {
        public void onLoad(CoapResponse response) {
            if (response.getCode() == ResponseCode.VALID) {
                byte[] payload = response.getPayload();
                LocationMessage locationMessage = new LocationMessage(payload, payload.length);
                List<UserData> userDataList = locationMessage.getUserDataList();
                updateAllLocation(userDataList);
            }
            notifyLocation();
        }
        public void onError() {
            System.err.println("-Failed--------");
        }
    }

    private void updateAllLocation(List<UserData> userDataList) {
        UserData[] userDatas = new UserData[userDataList.size()];
        for(int i=0; i<userDataList.size();i++){
            userDatas[i] = userDataList.get(i);
        }
        finishUpdateAllLocation(userDatas);
    }

    public void catchFugitive(int fugitiveId){
        Log.d("shylphe d", "catchFugitive: "+fugitiveId);
        CoapResponse response = client.put(roomId + "/" + fugitiveId, MsgType.CATCH_FUGITIVE);
        Log.d("shylphe d", "catchFugitive: end; code : "+(response==null? "error" :response.getCode()));
    }

    public void diePlayer(int playerId){
        Log.d("shylphe d", "diePlayer: "+playerId);
        client.put(roomId + "/" + playerId, MsgType.DIE_PLAYER);
        Log.d("shylphe d", "diePlayer: end");
    }

    private void notifyLocation(){
        LocData location = gpsInfo.getLocData();
        UserData userData = new UserData(player.getId(), player.getUserProperties(), location);
        LocationMessage locationMessage = new LocationMessage(roomId, 1, UserData.getSize());
        locationMessage.addUserDataStream(userData.getStream());
        client.put(new HandlerSendResult(), locationMessage.getStream(), MsgType.USER_DATA);

    }
    class HandlerSendResult implements CoapHandler{

        @Override
        public void onLoad(CoapResponse response) {
            if(response!=null){
                if(response.getCode() == DELETED){
                    endGame();
                }else if(response.getCode() == VALID){
                    LocData location = gpsInfo.getLocData();
                    UserData userData = new UserData(player.getId(), player.getUserProperties(), location);
                    LocData locData = userData.getLocData();
                    player.setLocData(locData);
                    double[] loc =new double[2];
                    loc[0]= locData.getLat();
                    loc[1]= locData.getLng();
                    finishNotifyLocation(loc);
                }
            }
        }

        @Override
        public void onError() {

        }
    }
/*
    class NotifyLocationTask extends TimerTask {
        @Override
        public void run() {
            LocData location = gpsInfo.getLocData();
            UserData userData = new UserData(player.getId(), player.getUserProperties(), location);
            LocationMessage locationMessage = new LocationMessage(roomId, 1, UserData.getSize());
            locationMessage.addUserDataStream(userData.getStream());
            CoapResponse response = client.put(locationMessage.getStream(), MsgType.USER_DATA);
            if(response!=null){
                if(response.getCode() == DELETED){
                    endGame();
                }else if(response.getCode() == VALID){
                    LocData locData = userData.getLocData();
                    player.setLocData(locData);
                    double[] loc =new double[2];
                    loc[0]= locData.getLat();
                    loc[1]= locData.getLng();
                    finishNotifyLocation(loc);
                }
            }
        }
    }*/

    private void endGame() {
        //Toast.makeText(GameClientActivity.this, "잡혔습니다.", Toast.LENGTH_LONG);
        timer.cancel();
        relation.reactiveCancel();
        aliveFlag =false;
    }

    private native void finishNotifyLocation(double[] locData);
    private native void finishUpdateAllLocation(UserData[] locData);
}