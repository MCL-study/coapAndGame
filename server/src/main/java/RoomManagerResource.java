import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomManagerResource extends CoapResource {
    private RoomManager roomManager;
    public RoomManagerResource(String name,RoomManager roomManager){
        super(name);
        this.roomManager = roomManager;
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
            roomManager.enterRoom(Integer.parseInt(ids[0]),Integer.parseInt(ids[1]),Integer.parseInt(ids[2]));
            exchange.respond(ResponseCode.VALID);
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        List<Room> roomList = roomManager.getRoomList();

        StreamListConverter streamListConverter = new StreamListConverter(roomList.size(),roomList.get(0).getRoomConfig().getByteStream().length);
        for (Room room : roomList) {
            streamListConverter.addStream(room.getRoomConfig().getByteStream());
        }
        exchange.respond(ResponseCode.VALID, streamListConverter.getStream());
    }
}
