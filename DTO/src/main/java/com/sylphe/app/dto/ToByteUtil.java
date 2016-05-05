package com.sylphe.app.dto;

/**
 * Created by myks7 on 2016-03-15.
 *
 *  Byte -> Short
 *  ByteBuffer.wrap(tmpBuffer, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getShort();
 *
 */
public class ToByteUtil {
    public static void doubleToBytes_LE(double input, byte[] output, int offset  ) {
        long bits = Double.doubleToLongBits(input);
        for(int i = 0; i < 8; i++)
            output[i+offset] = (byte)( (bits >> ( i * 8) ) & 0xff);
    }

    public static void doubleToBytes_BE(double input, byte[] output, int offset  ) {
        long bits = Double.doubleToLongBits(input);
        for(int i = 0; i < 8; i++)
            output[i+offset] = (byte)( (bits >> ( (7-i) * 8) ) & 0xff);
    }

    public static void intToBytes_LE(int input, byte[] output , int offset ) {
        for(int cnt = 0;  cnt<4; cnt++){
            output[cnt+offset] = (byte) (input   % (0xff + 1));
            input   = input   >> 8;
        }
    }

    public static void intToBytes_BE(int input, byte[] output , int offset ) {
        for(int cnt = 0;  cnt<4; cnt++){
            output[ (3-cnt) +offset] = (byte) (input   % (0xff + 1));
            input   = input   >> 8;
        }
    }
}
