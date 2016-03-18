import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class RoomManager {
    private List<Room> roomList;
    private int roomId=0;
    public RoomManager(){
        roomList= new ArrayList<Room>();
    }
    public Room createRoom(RoomConfig config){
        Room room = new Room(roomId,config);
        roomId++;
        roomList.add(room);
        return room;
    }

    public Room searchRoom(int roomId){
        for (Room room : roomList) {
            if (room.getRoomId() == roomId)
                return room;
        }
        return null;
    }

    public void enterRoom(int roomId, int userId,int userProperties){
        Room room = searchRoom(roomId);
        if (room != null) {
            room.addUser(new UserData(userId,userProperties));
        }
    }

    public List<Room> getRoomList(){
        return roomList;
    }

    public void updateUserData(int roomId, UserData userData) {
        Room room = searchRoom(roomId);
        if (room != null) {
            room.searchUserAndUpdate(userData);
        }
    }

    public void deleteUser(int roomId, int userId){
        Room room = searchRoom(roomId);
        room.deleteUser(userId);
    }
}
