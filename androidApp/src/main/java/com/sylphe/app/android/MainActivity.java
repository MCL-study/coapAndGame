package com.sylphe.app.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.shylphe.lib.android.client.AccessClient;
import com.shylphe.lib.android.client.GpsInfo;
import com.shylphe.lib.android.client.RoomConnector;
import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private URI uri;
    private AccessClient accessClient;
    private RoomConnector roomConnector;
    private GpsInfo gpsInfo;
    private EditText editTextEnterRoom;
    private UserState userState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMemberObject();
        try {
            uri = new URI("coap://192.168.0.29");
    //        uri = new URI("coap://117.17.102.28");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        initClient();

        initClickListener();

        login();
    }


    private void initMemberObject() {
        editTextEnterRoom = (EditText) findViewById(R.id.editTextEnterRoom);
        gpsInfo = new GpsInfo(this);
        userState = new UserState();
    }

    private void initClient() {
        roomConnector = new RoomConnector(uri);
        accessClient = new AccessClient(uri);
    }

    private void login() {
        Integer id = accessClient.login();
        if(id == null){
            Toast.makeText(this,"login 실패 : 서버 관리자에게 문의하세요.",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userState.setId( id);
        Toast.makeText(this,"login success id : "+id,Toast.LENGTH_SHORT).show();
    }

    private void initClickListener() {
        Button btnMakeRoom = (Button) findViewById(R.id.btnMakeRoom);
        btnMakeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int scale = 600;
                int timeLimit = 9999;
                Integer roomId = roomConnector.makeRoom(gpsInfo.getLocData(), 10, scale, timeLimit);
                if(roomId != null){
                    Toast.makeText(MainActivity.this, "성공 roomid : "+roomId+"범위:"+scale+"m 제한시간:"+timeLimit+"초", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnEnterRoom =  (Button) findViewById(R.id.btnEnterroom);
        btnEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editTextEnterRoom.getText().toString();
                if(!str.equals("")){
                    int roomid = Integer.parseInt(str);
                    userState.setUserProperties(UserProperties.valueOf(roomid));
                    startGame(roomid);
                }
            }
        });
        Button btnSearchRoom = (Button) findViewById(R.id.btnSearchRoom);
        btnSearchRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RoomConfig> roomConfigList = roomConnector.getRoomList();
                if(roomConfigList!=null){
                    String str = "";
                    for(RoomConfig cfg : roomConfigList){
                        str = str+"방번호: "+cfg.getRoomID()+"\n";
                    }
                    alert(MainActivity.this,"확인",str);
                }else{
                    Toast.makeText(MainActivity.this, "방 없음", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startGame(int roomId){
        RoomConfig roomConfig = enterRoom(roomId);

        if(roomConfig!=null){
            LocalRoomConfig localRoomConfig = new LocalRoomConfig(roomConfig);
            //Intent intent = new Intent(this, GameClientActivity.class);
            Intent intent = new Intent(this, augmentedReality.MainActivity.class);
            intent.putExtra("userState",userState);
            intent.putExtra("localRoomConfig",localRoomConfig);
            intent.putExtra("uri", uri.toString());
            startActivity(intent);
        }
    }

    private RoomConfig enterRoom(int roomId) {
        EditText properties = (EditText) findViewById(R.id.editTextProperties);
        String text = properties.getText().toString();
        if(text.equals("")){
            Toast.makeText(this,"도망자 추척자중 선택 필요",Toast.LENGTH_SHORT).show();
            return null;
        }else{
            int i= Integer.parseInt(text);

            if(UserProperties.isValidProperties(i)){
                userState.setUserProperties(UserProperties.valueOf(i));
                return roomConnector.enterRoom(roomId,userState.getId(),UserProperties.valueOf(i));
            }
            return null;
        }
    }

    private static void alert(Context context, String ok_key_str, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(ok_key_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setCancelable(false);
        alert.setMessage(msg);
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gpsInfo.getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsInfo.stopUsingGPS();
        accessClient.close();
        roomConnector.close();
    }
}
