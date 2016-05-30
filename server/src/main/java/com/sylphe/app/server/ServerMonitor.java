package com.sylphe.app.server;


import com.sylphe.app.dto.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by myks7 on 2016-05-16.
 */
class ServerMonitor {
    private RoomManager roomManager;
    private UserManager userManager;
    static private List<String> logs = new ArrayList<String>();
    ServerMonitor(RoomManager roomManager, UserManager userManager){
        this.roomManager = roomManager;
        this.userManager = userManager;
    }
    void start(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Scanner scanner = new Scanner(System.in);
                boolean aliveFlag =true;
                while (aliveFlag){
                    System.out.println("------------------------------");
                    System.out.println("선택하세요. 1:show Log, 2:show LoginUser 3:show GameSpace");
                    System.out.println("종료는 0입니다.");
                    System.out.println("------------------------------");
                    int index = scanner.nextInt();
                    switch (index){
                        case 0:
                            aliveFlag=false;
                            break;
                        case 1:
                            showLog();
                            break;
                        case 2:
                            showLoginUser();
                            break;
                        case 3:
                            showGameSpace();
                            break;
                    }
                }
            }
        });
        System.out.println("ServerMonitor 시작");
        thread.start();
    }
    private void showLog(){
        for(String str : logs){
            System.out.println(str);
        }
    }
    private void showLoginUser(){
        List<User> userList = userManager.getUserList();
        System.out.println("접속한 사용자 수 : "+userList.size());
        for(UserData user : userList){
            System.out.println("id : "+user.getId()+" ");
        }
    }
    private void showGameSpace(){
        List<Room> roomList = roomManager.getRoomList();
        System.out.println("생성된 게임 공간의 수 : "+roomList.size());
        for(Room room : roomList){
            System.out.println("spaceID : "+room.getRoomId()+" ");
            System.out.println(room.getRoomID()+"번 게임공간; 제한시간:"+room.getTimeLimit()+"초 최대참가인원"+room.getMaxGameMember()+"명 범위"+room.getScale()+"m");
        }
    }
    private void showGameProcess(int spaceID){

    }
    static void log(String msg){
        logs.add(msg);
        if(logs.size()>1000){
            logs.clear();
        }
    }
}
