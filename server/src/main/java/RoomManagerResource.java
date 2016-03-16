import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomManagerResource extends CoapResource {
    private RoomManager roomManager;
    private UserManager userManager;
    public RoomManagerResource(String name,UserManager userManager){
        super(name);
        this.userManager = userManager;
        roomManager = new RoomManager();
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        int format = exchange.getRequestOptions().getContentFormat();
        if(format == MsgType.MAKE_ROOM){
            RoomConfig config = new RoomConfig(exchange.getRequestPayload());
            Room room = roomManager.createRoom(config);
            exchange.respond(ResponseCode.VALID,room.getRoomConfig().getByteStream());
        }else if(format == MsgType.ENTER_ROOM){
            String payload = exchange.getRequestText();
            String[] ids = payload.split("/");
            roomManager.enterRoom(Integer.parseInt(ids[0]),Integer.parseInt(ids[1]));
            exchange.respond(ResponseCode.VALID);
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        List<Room> roomList = roomManager.getRoomList();

        StreamList streamList = new StreamList(roomList.size(),roomList.get(0).getRoomConfig().getByteStream().length);
        for (Room room : roomList) {
            streamList.addStream(room.getRoomConfig().getByteStream());
        }
        exchange.respond(ResponseCode.VALID,streamList.getStream());
    }
}
