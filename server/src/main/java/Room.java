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
    private int timeLimit;

    public Room(int roomId ,RoomConfig config){
        userList = new ArrayList<UserData>();
        this.roomId = roomId;
        maxGameMember = config.getMaxGameMember();
        scale = config.getScale();
        centerLoc = config.getCenterLoc();
        timeLimit = config.getTimeLimit();
    }

    public int getRoomId() {
        return roomId;
    }

    public RoomConfig getRoomConfig(){
        return new RoomConfig(roomId,centerLoc,maxGameMember,scale,timeLimit);
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
        }else{
            System.out.println("searchUserAndUpdate user null error");
        }
    }

    public List<UserData> getUserList() {
        return userList;
    }

    public void deleteUser(int userId){
        UserData userData = searchUser(userId);
        userList.remove(userData);
    }
}
