import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomConfig {
    private int roomID;
    private LocData centerLoc;
    private int maxGameMember;
    private int scale;

    public RoomConfig(int roomID, LocData centerLoc,int maxGameMember,int scale ){
        this.roomID = roomID;
        this.centerLoc=centerLoc;
        this.maxGameMember=maxGameMember;
        this.scale=scale;
    }

    public RoomConfig( LocData centerLoc,int maxGameMember,int scale ){
        roomID = -1;
        this.centerLoc=centerLoc;
        this.maxGameMember=maxGameMember;
        this.scale=scale;
    }
    public int getRoomID() {
        return roomID;
    }
    public LocData getCenterLoc() {
        return centerLoc;
    }

    public int getMaxGameMember() {
        return maxGameMember;
    }

    public int getScale() {
        return scale;
    }

    public RoomConfig(byte[] bytes){
        centerLoc = new LocData(ByteBuffer.wrap(bytes, 0, 16).array());
        maxGameMember = ByteBuffer.wrap(bytes, 16, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        scale = ByteBuffer.wrap(bytes, 20, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        roomID = ByteBuffer.wrap(bytes, 24, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public byte[] getByteStream(){
        byte[] bytes = new byte[28];
        byte[] locbytes = centerLoc.getByteStream();
        System.arraycopy(locbytes, 0, bytes, 0, locbytes.length);
        ToByteUtil.intToBytes_LE(maxGameMember,bytes,16);
        ToByteUtil.intToBytes_LE(scale,bytes,20);
        ToByteUtil.intToBytes_LE(roomID,bytes,24);
        return bytes;
    }

}
