import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomConnector {
    private final CoapClient client;
    private RoomConfig roomConfig;
    private Login login;
    private List<RoomConfig> configs;

    public RoomConnector(URI uri,Login login){
        client = new CoapClient(uri+"/RoomManager");
        this.login=login;
    }

    public Integer makeRoom(LocData centerLoc,int maxGameMember,int scale){
        RoomConfig config = new RoomConfig(centerLoc,maxGameMember,scale);
        CoapResponse response = client.put(config.getByteStream(),MsgType.MAKE_ROOM);
        if(response!=null){
            if(response.getCode() == ResponseCode.VALID){
                System.out.print("방 만들기 성공");
                roomConfig = new RoomConfig(response.getPayload());
                return roomConfig.getRoomID();
            }
        }else{
            System.out.print("error");
        }
        return null;
    }

    public void enterRoom(int roomId){
        CoapResponse response = client.put(roomId+"/"+login.getId(),MsgType.ENTER_ROOM);
        if(response!=null) {
            if (response.getCode() == ResponseCode.VALID) {
                System.out.println("접속 요청 완료");
            }
        }
    }

    public void requestRoomList(){
        configs = new ArrayList<RoomConfig>();
        CoapResponse response = client.get();
        if(response!=null){
            StreamList streamList = new StreamList(response.getPayload());
            List<byte[]> byteStreamList = streamList.getStreamList();
            for(byte[] stream : byteStreamList){
                configs.add(new RoomConfig(stream));
            }
        }else{
            System.out.println("방 없음");
        }
        //동기화 관련 처리 필요 개발 시간 관계상 sleep으로 대체
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
    }
    public List<RoomConfig> getRoomCfgList(){
        return configs;
    }
}
