import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class UserManager {

    private List<UserData> userList;
    private int id;
    public UserManager(){
        userList = new ArrayList<UserData>();
        id=0;
    }

    public UserData createUser(){
        UserData user = new UserData(id,-1);
        id++;
        addUser(user);
        return user;
    }

    private void addUser(UserData user){
        userList.add(user);
    }
}
