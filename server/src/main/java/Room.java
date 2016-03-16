import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-14.
 */
public class Room {
    private int roomId;
    private int maxGameMember;
//    private int currentChaserNum, currentFugitiveNum;
    private LocData centerLoc;
    private int scale;
    private List<UserData> userList;

    public Room(int roomId ,RoomConfig config){
        userList = new ArrayList<UserData>();
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

    public void addUser(UserData user){
        userList.add(user);
    }
    private UserData searchUser(int userId){
        for(UserData userData : userList){
            if(userData.getId() == userId){
                return userData;
            }
        }
        return null;
    }

    public void searchUserAndUpdate(UserData userData) {
        UserData user = searchUser(userData.getId());
        if (user != null) {
            user.setLocData(userData.getLocData());
        }
    }

    public List<UserData> getUserList() {
        return userList;
    }
}
