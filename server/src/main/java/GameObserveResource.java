import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.VALID;


/**
 * Created by myks7 on 2016-03-14.
 */
public class GameObserveResource extends CoapResource {
    private RoomManager roomManager;
    public GameObserveResource(String name,RoomManager roomManager) {
        super(name);
        this.roomManager = roomManager;
        setObservable(true); // enable observing
        setObserveType(CoAP.Type.CON); // configure the notification type to CONs
        getAttributes().setObservable(); // mark observable in the Link-Format

        // schedule a periodic update task, otherwise let events call changed()
        Timer timer = new Timer();
        timer.schedule(new UpdateTask(), 0, 2000);
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("update obs..."+getName());
            // .. periodic update of the resource
            changed(); // notify all observers
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.setMaxAge(1); // the Max-Age value should match the update interval
//        exchange.respond("update "+getName() +"  "+exchange.getRequestOptions().getAccept());
        int roomid =exchange.getRequestOptions().getAccept();
        Room room = roomManager.searchRoom(roomid);
        if(room!=null){
            List<UserData> userList = room.getUserList();
            LocationMessage locationMessage = new LocationMessage(roomid,userList.size(),UserData.getSize());
            for(UserData data : userList){
                locationMessage.addUserDataStream(data.getStream());
            }
            exchange.respond(VALID,locationMessage.getStream());
        }
    }

    @Override
    public void handleDELETE(CoapExchange exchange) {
        delete(); // will also call clearAndNotifyObserveRelations(ResponseCode.NOT_FOUND)
        exchange.respond(DELETED);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        int contentFormat = exchange.getRequestOptions().getContentFormat();
        if(contentFormat == MsgType.USER_DATA){
            byte[] requestPayload = exchange.getRequestPayload();
            LocationMessage locationMessage = new LocationMessage(requestPayload, requestPayload.length);
            int roomId = locationMessage.getRoomId();
            List<UserData> userDataList = locationMessage.getUserDataList();
            UserData userData = userDataList.get(0);
            roomManager.updateUserData(roomId,userData);
        }
        //exchange.respond(CHANGED);
//        changed(); // notify all observers
    }
}
