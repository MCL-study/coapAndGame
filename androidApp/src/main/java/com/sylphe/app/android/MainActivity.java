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

        try {
            uri = new URI("coap://117.17.102.28");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        userState = new UserState();

        gpsInfo = new GpsInfo(this);
        accessClient = new AccessClient(uri);
        int id = accessClient.login();
        userState.setId( id);

        roomConnector = new RoomConnector(uri);

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
                }
            }
        });
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
        btnSearchRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RoomConfig> roomConfigList = roomConnector.getRoomList();
                String str = "";
                for(RoomConfig cfg : roomConfigList){
                    str = str+"방번호: "+cfg.getRoomID()+"\n";
                }
                alert(MainActivity.this,"확인",str);
            }
        });
    }


    private void startGame(int roomId){
        boolean flag = enterRoom(roomId);

        if(flag){
            Intent intent = new Intent(this,GameClientActivity.class);
            intent.putExtra("userState",userState);
            intent.putExtra("uri", uri.toString());
            startActivity(intent);
        }
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
                boolean result = true;
                if(roomConnector.enterRoom(roomId,userState.getId(),UserProperties.valueOf(i))==null)
                    result = false;
                return result;
            }
            return false;
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


}
