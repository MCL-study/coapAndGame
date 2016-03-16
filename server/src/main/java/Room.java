import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-14.
 */
public class Room {
    private int roomId;
    private int maxGameMember;
    private LocData centerLoc;
    private int scale;
    private List<User> Users;

    public Room(int roomId ,RoomConfig config){
        Users = new ArrayList<User>();
        this.roomId = roomId;
        maxGameMember = config.getMaxGameMember();
        scale = config.getScale();
        centerLoc = config.getCenterLoc();
    }

    public int getRoomId() {
        return roomId;
    }

    public RoomConfig getRoomConfig(){
        return new RoomConfig(roomId,centerLoc,maxGameMember,scale);
    }

    public void addUser(User user){
        Users.add(user);
    }
}
