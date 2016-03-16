import java.util.Random;

/**
 * Created by myks7 on 2016-03-15.
 */
public class GpsInfo {

    private Random random;
    public GpsInfo(){
        random = new Random();
    }

    public LocData getLocation(){
        return new LocData(127.001+ random.nextDouble(),36.001+ random.nextDouble());
    }
}
