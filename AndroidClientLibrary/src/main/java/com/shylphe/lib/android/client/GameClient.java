package com.shylphe.lib.android.client;

import android.util.Log;
import com.sylphe.app.dto.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import java.net.URI;
import java.util.List;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.VALID;

/**
 * Created by myks7 on 2016-03-15.
 */
public abstract class GameClient{
    private GpsInfo gpsInfo;
    private CoapObserveRelation relation;
    private CoapClient client;
    private int roomId;
    private UserData player;
    private boolean aliveFlag;

    public GameClient(URI uri, GpsInfo gpsInfo){
        client = new CoapClient(uri + "/gameObserve");
        this.gpsInfo = gpsInfo;
    }

    public void start(int roomId, int id,UserProperties userProperties) {
        aliveFlag=true;
        this.roomId = roomId;
        player = new UserData(id,userProperties);
        relation = client.observe(new handler(), roomId);
    }

    public void close() {
        aliveFlag=false;
        relation.reactiveCancel();
    }

    class handler implements CoapHandler {
        public void onLoad(CoapResponse response) {
            if(aliveFlag) {
                if (response.getCode() == ResponseCode.VALID) {
                    byte[] payload = response.getPayload();
                    LocationMessage locationMessage = new LocationMessage(payload);
                    List<UserData> userDataList = locationMessage.getUserDataList();
                    updateAllUserData(userDataList);
                }
                notifyLocation();
            }
        }
        public void onError() {
            System.err.println("-Failed--------");
        }
    }

    private void updateAllUserData(List<UserData> userDataList) {
        UserData[] userDatas = new UserData[userDataList.size()];
        for(int i=0; i<userDataList.size();i++){
            userDatas[i] = userDataList.get(i);
        }
        finishUpdateAllUserData(userDatas);
    }

    public void catchFugitive(int fugitiveId) {
        if (aliveFlag) {
            CoapResponse response = client.put(roomId + "/" + fugitiveId, MsgType.CATCH_FUGITIVE);
            Log.d("shylphe d", "catchFugitive: end; code : " + (response == null ? "error" : response.getCode()));
        }
    }

    public void diePlayer(int playerId) {
        if (aliveFlag) {
            client.put(roomId + "/" + playerId, MsgType.DIE_PLAYER);
            Log.d("shylphe d", "diePlayer: end");
        }
    }

    private void notifyLocation(){
        LocData location = gpsInfo.getLocData();
        UserData userData = new UserData(player.getId(), player.getUserProperties(), location);
        LocationMessage locationMessage = new LocationMessage(roomId, 1, UserData.getSize());
        locationMessage.addUserDataStream(userData.getStream());
        client.put(new NotifyLocationHandler(location), locationMessage.getStream(), MsgType.USER_DATA);
        player.setLocData(location);
    }
    private class NotifyLocationHandler implements CoapHandler{
        private  LocData location;
        NotifyLocationHandler(LocData location){
            this.location =location;
        }
        @Override
        public void onLoad(CoapResponse response) {
            if(response!=null){
                if(response.getCode() == VALID){
                    double[] loc =new double[2];
                    loc[0]= location.getLat();
                    loc[1]= location.getLng();
                    finishNotifyLocation(loc);
                }
            }
        }

        @Override
        public void onError() {
        }
    }

    protected abstract void finishNotifyLocation(double[] locData);
    protected abstract void finishUpdateAllUserData(UserData[] locData);
}