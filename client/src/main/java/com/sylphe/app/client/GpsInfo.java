package com.sylphe.app.client;

import com.sylphe.app.dto.LocData;

import java.util.Random;

/**
 * Created by myks7 on 2016-03-15.
 */
public class GpsInfo {

    private Random random;
    private static double lat=0,lng=0;
    public GpsInfo(){
        random = new Random();
    }

    public LocData getLocation(){
        if(lat<1) {
            lng += 0.0001;
            lat += 0.0001;
        }else if(lat>1){
            lng = -0.5;
            lat = -0.5;
        }
        return new LocData(33.45500658+ lat,126.56507811 + lng);
    }
}
