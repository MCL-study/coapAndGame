import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by myks7 on 2016-03-13.
 */
public class LocData {
    public static final byte format = 101;
    private Double lat, lng;

    public LocData(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public byte[] getByteData(){
        byte[] bytes = new byte[17];
        bytes[0] = format;
        doubleToBytes_LE(lat,bytes,1);
        doubleToBytes_LE(lng,bytes,9);
        return bytes;
    }
    public LocData(byte[] bytes){
        lat = ByteBuffer.wrap(bytes, 1, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        lng = ByteBuffer.wrap(bytes, 9, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    private void doubleToBytes_LE(double input, byte[] output, int offset  ) {
        long bits = Double.doubleToLongBits(input);
        for(int i = 0; i < 8; i++)
            output[i+offset] = (byte)( (bits >> ( i * 8) ) & 0xff);
    }

    private void doubleToBytes_BE(double input, byte[] output, int offset  ) {
        long bits = Double.doubleToLongBits(input);
        for(int i = 0; i < 8; i++)
            output[i+offset] = (byte)( (bits >> ( (7-i) * 8) ) & 0xff);
    }
}
