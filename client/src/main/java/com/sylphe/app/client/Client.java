package com.sylphe.app.client;

import com.sylphe.app.dto.RoomConfig;
import com.sylphe.app.dto.UserProperties;

import java.net.URI;
import java.util.List;
import java.util.Scanner;

/**
 * Created by myks7 on 2016-03-14.
 */
public class Client {
    private boolean aliveFlag;
    private final UserState userState;
    private final RoomConnector roomConnector;
    private GpsInfo gpsInfo;
    private final GameClient gameClient;
    private Scanner scanner;

    public Client(URI uri){
        aliveFlag=true;
        userState = new UserState(uri);
        roomConnector = new RoomConnector(uri, userState);
        gameClient = new GameClient(uri,userState);
        gpsInfo = new GpsInfo();
    }

    public void close(){
        aliveFlag = false;
    }

    public boolean isAlive(){
        return aliveFlag;
    }

    public void process(){
        userState.login();
        while (true){
            System.out.println("1: 방만들기 2: 방 찾기 3: 방들어가기");
            scanner = new Scanner(System.in);
            int index = scanner.nextInt();
            switch (index){
                case 1:
                    Integer roomId = roomConnector.makeRoom(gpsInfo.getLocation(),12,13,999);
                    if(roomId != null){
                        startGame(roomId);
                    }
                    break;
                case 2:
                    roomConnector.requestRoomList();
                    List<RoomConfig> roomConfigList = roomConnector.getRoomCfgList();
                    for(RoomConfig cfg : roomConfigList){
                        System.out.println("방번호: "+cfg.getRoomID());
                    }
                    break;
                case 3:
                    System.out.println("들어갈 방 번호 :");
                    int i = scanner.nextInt();
                    startGame(i);
                    break;
                default:
                    System.out.print("switch error");
            }
        }
    }

    public void startGame(int roomId){
        boolean flag = enterRoom(roomId);
        int id = userState.getId();
        if(flag)
            gameClient.start(roomId,id);
        while (gameClient.isAlive()){
            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    private boolean enterRoom(int roomId) {
        System.out.println("0: 도망자 1:추격자");
        int i = scanner.nextInt();
        userState.setUserProperties(UserProperties.valueOf(i));
        return roomConnector.enterRoom(roomId,i);
    }
}
