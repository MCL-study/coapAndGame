import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import java.net.URI;
import java.util.List;
import java.util.Scanner;

/**
 * Created by myks7 on 2016-03-14.
 */
public class Client {
    private boolean aliveFlag;
    private final Login login;
    private final RoomConnector roomConnector;
    private GpsInfo gpsInfo;

    public Client(URI uri){
        aliveFlag=true;
        login = new Login(uri);
        roomConnector = new RoomConnector(uri,login);
        gpsInfo = new GpsInfo();
    }

    public void close(){
        aliveFlag = false;
    }

    public boolean isAlive(){
        return aliveFlag;
    }

    private void login(){
        login.requestID();
    }


    public void process(){
        login();
        while (true){
            System.out.println("1: 방만들기 2: 방 찾기 3: 방들어가기");
            Scanner scanner = new Scanner(System.in);
            int index = scanner.nextInt();
            switch (index){
                case 1:
                    Integer roomId = roomConnector.makeRoom(gpsInfo.getLocation(),12,13);
                    if(roomId != null){
                        System.out.print("방 접속 시도");
                        roomConnector.enterRoom(roomId);
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
                    roomConnector.enterRoom(i);
                    break;
                default:
                    System.out.print("switch error");
            }
        }
    }



}
