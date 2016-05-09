/****************************************************************************
Copyright (c) 2015 Chukong Technologies Inc.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.cpp;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserProperties;
import org.cocos2dx.lib.Cocos2dxActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class AppActivity extends Cocos2dxActivity {

    private static GpsInfo gpsInfo;
    private static Login login;
    private static RoomConnector roomConnector;
    private static GameClient gameClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gpsInfo = new GpsInfo(this);

        URI uri=null;
        try {
            uri = new URI("coap://117.17.102.28");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        login = new Login(uri);
        roomConnector = new RoomConnector(uri);
        gameClient = new GameClient(uri,gpsInfo);
        super.onCreate(savedInstanceState);
    }

    public static double[] getLocation(){
        Location location = gpsInfo.getLocation();
        return new double[]{location.getLatitude(), location.getLongitude()};
    }

    public static int login(){
        login.login();
        return login.getId();
    }

    public static int enterRoom(int roomId,int id,int userProperties){
        if(roomConnector.enterRoom(roomId, id, UserProperties.valueOf(userProperties)))
            return 1;
        return -1;
    }

    public static int makeRoom(int maxGameMember,int scale, int timeLimit){
        int roomId = roomConnector.makeRoom(gpsInfo.getLocData(), maxGameMember,scale, timeLimit);
        return roomId;
    }

    public static RoomConfig[] requestRoomList(){
        List<RoomConfig> roomList = roomConnector.getRoomList();
//        return roomList;
        if(roomList==null)
            return null;
        Log.d("java roomconfig : ", roomList.toArray().length+" id:"+roomList.get(roomList.toArray().length-1).getRoomID());
        RoomConfig[] roomConfigs = new RoomConfig[roomList.size()];
        for(int i=0;i<roomList.size();i++){
            roomConfigs[i]=roomList.get(i);
        }
        return roomConfigs;
    }

    public static void startGame(int roomId, int id,int properties){
        gameClient.start(roomId,id,UserProperties.valueOf(properties));
    }

    public static void catchFugitive(int fugitiveId){
        gameClient.catchFugitive(fugitiveId);
    }

    public static void diePlayer(int playerId){
        gameClient.diePlayer(playerId);
    }

    public static void closeGameClient(){
        gameClient.close();
/*        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}