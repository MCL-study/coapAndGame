package com.sylphe.app.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sylphe.app.dto.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by myks7 on 2016-03-15.
 */
public class GameClientActivity extends AppCompatActivity {
    private GpsInfo gpsInfo;
    private boolean aliveFlag;
    private CoapObserveRelation relation;
    private CoapClient client;
    private Timer timer;
    private List<UserData> userList;
    private int userId;
    private int roomId;
    private UserProperties userProperties;
    private UserData player;
    private MyView myView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_game);
        gpsInfo = new GpsInfo(this);
        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("id");
        roomId = extras.getInt("roomid");
        userProperties = UserProperties.valueOf(extras.getInt("userp"));
        URI uri = null;
        try {
            uri = new URI(extras.getString("uri"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        aliveFlag = false;
        client = new CoapClient(uri + "/gameObserve");
        userList = new ArrayList<UserData>();
        start(roomId, userId);
        myView = new MyView(this);
        setContentView(myView);
    }

  /*  public GameClientActivity(URI uri, UserState userState, Context context){
      *//*  aliveFlag = false;
        client = new CoapClient(uri + "/gameObserve");
        this.userState = userState;
        userList= new ArrayList<com.sylphe.app.dto.UserData>();
        this.context = context;
        gpsInfo = new GpsInfo(context);*//*
    }*/

    public void start(int roomId, int id) {
        aliveFlag = true;
        relation = client.observe(new handler(), roomId);
        timer = new Timer();
        timer.schedule(new NotifyLocationTask(), 0, 1000);
    }

    public void close() {
        relation.reactiveCancel();
        timer.cancel();
        aliveFlag = false;
    }

    public boolean isAlive() {
        return aliveFlag;
    }

    class handler implements CoapHandler {
        public void onLoad(CoapResponse response) {
            if (response.getCode() == CoAP.ResponseCode.VALID) {
                byte[] payload = response.getPayload();
                LocationMessage locationMessage = new LocationMessage(payload, payload.length);
                List<UserData> userDataList = locationMessage.getUserDataList();
                updateAllLocation(userDataList);
                for (UserData data : userDataList) {
                    System.out.println(data.getId() + " " + data.getLocData().getLng() + " " + data.getLocData().getLat());
                }
            }
            //모든 위치 정보 올 예정

        }

        public void onError() {
            System.err.println("-Failed--------");
        }
    }

    class NotifyLocationTask extends TimerTask {
        @Override
        public void run() {
            LocData location = gpsInfo.getLocData();
            updateCurrentLoc(location);
            UserData userData = new UserData(userId, userProperties, location);
            LocationMessage locationMessage = new LocationMessage(roomId, 1, UserData.getSize());
            locationMessage.addUserDataStream(userData.getStream());
            CoapResponse response = client.put(locationMessage.getStream(), MsgType.USER_DATA);
            if(response!=null){
                if(response.getCode() == CoAP.ResponseCode.DELETED){
                    endGame();
                }else if(response.getCode() == CoAP.ResponseCode.VALID){
                    for (UserData userData1 : userList) {
                        if (userData1.getId() == userId) {
                            userData1.setLocData(userData.getLocData());
                            player = userData1;
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    }

    private void endGame() {
        //Toast.makeText(GameClientActivity.this, "잡혔습니다.", Toast.LENGTH_LONG);
        timer.cancel();
        relation.reactiveCancel();
        aliveFlag =false;

        finish();
    }

    final Handler handler = new Handler()  {
        public void handleMessage(Message msg)  {
            myView.invalidate();
        }
    };

    private void updateCurrentLoc(LocData location) {

    }

    private void updateAllLocation(List<UserData> userDataList) {
        for (UserData data : userDataList) {
            boolean exist=false;
            int id = data.getId();
            for (UserData userData : userList) {
                if (userId != id) {
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
                            Log.d("catch", "catch :" + data.getId());
                            client.put(roomId + "/" + data.getId(), MsgType.CATCH_FUGITIVE);
                            data.setLocData(new LocData(0,0));
                        }
                    }
                }
            }
        }
    }

    class MyView extends View {
        public MyView(Context context) {
            super(context);
        }
        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            double scalePerPixel=15000;

            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int centerX  = width/2;
            int centerY = height/2;

            Paint paintGreen = new Paint();
            paintGreen.setColor(Color.GREEN);
            Paint paintRed = new Paint();
            paintRed.setColor(Color.RED);
            Paint paintBlack = new Paint();
            paintBlack.setColor(Color.BLACK);
            canvas.drawCircle(centerX, centerY, 12, paintBlack);
            if(userProperties == UserProperties.FUGITIVE){
                canvas.drawCircle(centerX,centerY, 10, paintGreen);
            }else{
                canvas.drawCircle(centerX,centerY, 10, paintRed);
            }

            for(UserData data: userList){
                if(userId != data.getId()){
                    if(player !=null) {
                        LocData playerLocData = player.getLocData();
                        LocData locData = data.getLocData();
                        double diffLat= locData.getLat() - playerLocData.getLat();
                        double diffLng= locData.getLng() - playerLocData.getLng();
                        Log.d("d", diffLat + " " + diffLng);
                        Log.d("d2",diffLat*scalePerPixel+" "+diffLng*scalePerPixel);
                        int resultX = (int) (centerX+diffLat*scalePerPixel);
                        int resultY = (int) (centerY+diffLng*scalePerPixel);
                        if(data.getUserProperties() == UserProperties.FUGITIVE)
                            canvas.drawCircle(resultX,resultY,10,paintGreen);
                        else if(data.getUserProperties() == UserProperties.CHASER)
                            canvas.drawCircle(resultX,resultY,10,paintRed);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}