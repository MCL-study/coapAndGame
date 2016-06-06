package com.sylphe.app.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;
import com.shylphe.lib.android.client.GameClient;
import com.shylphe.lib.android.client.GpsInfo;
import com.sylphe.app.dto.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by myks7 on 2016-03-15.
 */
public class GameClientActivity extends AppCompatActivity {
    private List<UserData> userList;
    private UserData player;
    private MyView myView;
    private AndroidGameClient client;
    private GpsInfo gpsInfo;
    private  float pixelPerMeter =1.0f;
    private LocalRoomConfig localRoomConfig;
    private URI uri;
    private UserState userState;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMemberObject();

        myView = new MyView(this);
        myView.setClickable(true);
        setContentView(myView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.start(localRoomConfig.getConnectedRoomID(), userState.getId(), userState.getUserProperties());
    }

    private void initMemberObject() {
        gpsInfo = new GpsInfo(this);
        userList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        userState = (UserState)extras.getSerializable("userState");
        localRoomConfig = (LocalRoomConfig) extras.getSerializable("localRoomConfig");
        try {
            uri = new URI(extras.getString("uri"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        player = new UserData(userState.getId(), userState.getUserProperties());

        client = new AndroidGameClient(uri, gpsInfo);
    }

    private class MyView extends View{
        private Paint paintGreen;
        private Paint paintRed;
        private Paint paintBlack;
        private Paint paintGray;
        private int centerX;
        private int centerY;
        float beforeTouchLength=0.0f;

        public MyView(Context context) {
            super(context);
            init();
        }
        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }
        private void init(){
            paintGreen = new Paint();
            paintGreen.setColor(Color.GREEN);
            paintRed = new Paint();
            paintRed.setColor(Color.RED);
            paintBlack = new Paint();
            paintBlack.setColor(Color.BLACK);
            paintGray = new Paint();
            paintGray.setColor(Color.GRAY);
        }
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            centerX = canvas.getWidth()/2;
            centerY = canvas.getHeight()/2;

            drawRoomScale(canvas);
            updatePosition(canvas);
        }

        void updatePosition(Canvas canvas)
        {
            canvas.drawCircle(centerX, centerY, 12, paintBlack);
            if(player.getUserProperties() == UserProperties.FUGITIVE){
                canvas.drawCircle(centerX, centerY, 10, paintGreen);
            }else{
                canvas.drawCircle(centerX, centerY, 10, paintRed);
            }
            LocData playerLoc = player.getLocData();
            if(userList.size() != 0) {
                for (UserData user : userList) {
                    if (player.getId() != user.getId()) {
                        LocData locData = user.getLocData();
                        float result1[] = new float[3];
                        Location.distanceBetween(playerLoc.getLat(), 0.0, locData.getLat(), 0.0, result1);
                        float result2[] = new float[3];
                        Location.distanceBetween(0.0, playerLoc.getLng(), 0.0, locData.getLng(), result2);
                        if (user.getUserProperties() == UserProperties.FUGITIVE)
                            canvas.drawCircle(centerX + result1[0] * pixelPerMeter, centerY + result2[0] * pixelPerMeter, 10, paintGreen);
                        else if (user.getUserProperties() == UserProperties.CHASER)
                            canvas.drawCircle(centerX + result1[0] * pixelPerMeter, centerY + result2[0] * pixelPerMeter, 10, paintRed);
                    }
                }
            }
        }

        void drawRoomScale(Canvas canvas)
        {
            LocData playerLoc = player.getLocData();
            float result1[] = new float[3];
            Location.distanceBetween(playerLoc.getLat(), 0.0, localRoomConfig.getCenterLocLat(), 0.0, result1);
            float result2[] = new float[3];
            Location.distanceBetween(0.0, playerLoc.getLng(), 0.0, localRoomConfig.getCenterLocLng(), result2);
            canvas.drawCircle(centerX + result1[0] * pixelPerMeter, centerY + result2[0] * pixelPerMeter,  localRoomConfig.getScale()*pixelPerMeter, paintGray);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            int pointerCount = event.getPointerCount();
            System.out.print(pointerCount+"is pointerCount");
            switch (action){
                case MotionEvent.ACTION_MOVE:{
                    if(pointerCount==2){
                        float diffX = event.getX(0) - event.getX(1);
                        float diffY = event.getY(0) - event.getY(1);
                        float currentTouchLength= diffX*diffX + diffY*diffY;
                        if (beforeTouchLength > currentTouchLength)
                            pixelPerMeter *= 0.99f;
                        else if(beforeTouchLength < currentTouchLength)
                            pixelPerMeter *= 1.01f;
                        beforeTouchLength = currentTouchLength;
                    }
                    break;
                }
            }
            invalidate();
            return super.onTouchEvent(event);
        }
    }
    private class AndroidGameClient extends GameClient {
        AndroidGameClient(URI uri, GpsInfo gpsInfo) {
            super(uri, gpsInfo);
        }

        @Override
        protected void onGameTimeout() {
            close();
            finish();
        }

        @Override
        protected void finishNotifyLocation(double[] locData) {
            player.setLocData(new LocData(locData[0],locData[1]));
            checkCollision();
            invalidate();
        }

        @Override
        protected void finishUpdateAllUserData(UserData[] locData) {
            userList = new ArrayList<>();
            Collections.addAll(userList, locData);
            checkCollision();
            invalidate();
        }

        void checkCollision()
        {
            for (UserData user : userList) {
                LocData locData = user.getLocData();
                LocData playerLocData = player.getLocData();
                float result[] = new float[3];
                Location.distanceBetween(playerLocData.getLat(), playerLocData.getLng(), locData.getLat(), locData.getLng(), result);
                float distance = result[0];
                if (distance < 5) { // 5m 보다 작으면 충돌
                    if (player.getUserProperties() == UserProperties.CHASER) {
                        if (user.getUserProperties() == UserProperties.FUGITIVE) {
                            catchFugitive(user.getId());
                        }
                    } else if (player.getUserProperties() == UserProperties.FUGITIVE) {
                        if (user.getUserProperties() == UserProperties.CHASER) {
                            diePlayer(player.getId());
                        }
                    }
                }

            }
        }
    }
    //화면 갱신 메소드
    private void invalidate(){
        Message msg = invalidateHandler.obtainMessage();
        invalidateHandler.sendMessage(msg);
    }
    private final Handler invalidateHandler = new Handler() {
        public void handleMessage(Message msg) {
            myView.invalidate();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        gpsInfo.getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsInfo.stopUsingGPS();
    }

    @Override
    protected void onStop() {

        Toast.makeText(GameClientActivity.this, "GameClientActivity onStop", Toast.LENGTH_SHORT).show();
        super.onStop();
        client.close();
        finish();
    }
}