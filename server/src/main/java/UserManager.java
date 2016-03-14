import java.util.ArrayList;
import java.util.List;

/**
 * Created by myks7 on 2016-03-15.
 */
public class UserManager {

    private List<User> userList;
    private int id;
    public UserManager(){
        userList = new ArrayList<User>();
        id=0;
    }

    public User createUser(){
        User user = new User(id);
        id++;
        addUser(user);
        return user;
    }

    private void addUser(User user){
        userList.add(user);
    }
}
