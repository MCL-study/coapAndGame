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


import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private URI uri;
    private UserState userState;
    private RoomConnector roomConnector;
    private GameClientActivity gameClientActivity;
    private GpsInfo gpsInfo;
    private EditText editTextEnterRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            uri = new URI("coap://117.17.102.28");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        gpsInfo = new GpsInfo(this);
        userState = new UserState(uri);
        userState.login();
        roomConnector = new RoomConnector(uri, userState);
        //gameClientActivity = new GameClientActivity(uri,userState,this);

        Button btnMakeRoom = (Button) findViewById(R.id.btnMakeRoom);
        Button btnSearchRoom = (Button) findViewById(R.id.btnSearchRoom);
        Button btnEnterRoom = (Button) findViewById(R.id.btnEnterroom);
        editTextEnterRoom = (EditText) findViewById(R.id.editTextEnterRoom);

        btnMakeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //멕시멈 멤버수 스케일 설정 해야함
                Integer roomId = roomConnector.makeRoom(gpsInfo.getLocData(), 10, 600,9999);
                if(roomId != null){
                    Toast.makeText(MainActivity.this, "성공 roomid : "+roomId.toString(), Toast.LENGTH_SHORT).show();
    //                startGame(roomId);
                }
            }
        });
        btnEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editTextEnterRoom.getText().toString();
                if(str!= null){
                    startGame(Integer.parseInt(str.toString()));
                }
            }
        });
        btnSearchRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomConnector.requestRoomList();
                List<RoomConfig> roomConfigList = roomConnector.getRoomCfgList();
                String str = "";
                for(RoomConfig cfg : roomConfigList){
                    str = str+"방번호: "+cfg.getRoomID()+"\n";
                }
                alert(MainActivity.this,"확인",str);
            }
        });
    }


    public void startGame(int roomId){
        boolean flag = enterRoom(roomId);
        int id = userState.getId();
        if(flag){
            Intent intent = new Intent(this,GameClientActivity.class);
            intent.putExtra("id",id);
            intent.putExtra("roomid",userState.getConnectedRoomId());
            intent.putExtra("userp",userState.getUserProperties());
            intent.putExtra("uri", uri.toString());
            startActivity(intent);
        }

           // gameClientActivity.start(roomId,id);
    }

    private boolean enterRoom(int roomId) {
        EditText properties = (EditText) findViewById(R.id.editTextProperties);
        String text = properties.getText().toString();
        if(text.equals("")){
            Toast.makeText(this,"도망자 추척자중 선택 필요",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            int i= Integer.parseInt(text);

            if(UserProperties.isValidProperties(i)){
                userState.setUserProperties(UserProperties.valueOf(i));
                return roomConnector.enterRoom(roomId,i);
            }
            return false;
        }
    }

    public static void alert(Context context, String ok_key_str, String msg) {
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


}
