package com.sylphe.app.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_game);
        GpsInfo gpsInfo = new GpsInfo(this);
        Intent intent = getIntent();
        UserState userState = (UserState)getIntent().getSerializableExtra("userState");

        URI uri = null;
        try {
            uri = new URI(intent.getStringExtra("uri"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        player = new UserData(userState.getId(), userState.getUserProperties());
        userList = new ArrayList<>();
        AndroidGameClient client = new AndroidGameClient(uri, gpsInfo);
        client.start(userState.getConnectedRoomId(), userState.getId(), userState.getUserProperties());

        myView = new MyView(this);
        setContentView(myView);

    }

    private class MyView extends View {
        private Paint paintGreen;
        private Paint paintRed;
        private Paint paintBlack;
        private int centerX;
        private int centerY;
        private  float pixelPerMeter =1.0f;

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
        }
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = canvas.getWidth();
            int height = canvas.getHeight();
            centerX = width/2;
            centerY = height/2;

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
    }
    private class AndroidGameClient extends GameClient {
        AndroidGameClient(URI uri, GpsInfo gpsInfo) {
            super(uri, gpsInfo);
        }

        @Override
        protected void finishNotifyLocation(double[] locData) {
            player.setLocData(new LocData(locData[0],locData[1]));
            checkCollision();
            invalidate();
        }

        @Override
        protected void finishUpdateAllLocation(UserData[] locData) {
            userList.clear();
            Collections.addAll(userList, locData);
            checkCollision();
            invalidate();
        }

        void checkCollision()
        {
            for (UserData user : userList) {
           //     if (user.isAlive()) {
                    LocData locData = user.getLocData();
                    LocData playerLocData = player.getLocData();
                    float result[] = new float[3];
                    Location.distanceBetween(playerLocData.getLat(), playerLocData.getLng(), locData.getLat(), locData.getLng(),result);
                    float distance = result[0];
                    if (distance < 5) {
                        if (player.getUserProperties() == UserProperties.CHASER) {
                            if (user.getUserProperties() == UserProperties.FUGITIVE) {
                                 catchFugitive(user.getId());
                            //    (*i)->die();
                            //    removeChild((*i)->getSprite());
                            //    Sprite* sprite = Sprite::createWithTexture(dead);
                            //    (*i)->setSprite(sprite);
                            //    addChild(sprite);
                            //    ResultDataBuffer* buffer = ResultDataBuffer::getInstance();
                            //    buffer->appendCatchMessage((*i)->getId());
                            }
                        }
                        else if (player.getUserProperties() == UserProperties.FUGITIVE) {
                            if (user.getUserProperties() == UserProperties.CHASER) {
                                //ResultDataBuffer* buffer = ResultDataBuffer::getInstance();
                                //buffer->appendDieMessage((*i)->getId());
                                diePlayer(player.getId());
                            }
                        }
                    }
               // }
            }
        }
    }
    private void invalidate(){
        Message msg = invalidateHandler.obtainMessage();
        invalidateHandler.sendMessage(msg);
    }
    private final Handler invalidateHandler = new Handler() {
        public void handleMessage(Message msg) {
            myView.invalidate();
        }
    };


}