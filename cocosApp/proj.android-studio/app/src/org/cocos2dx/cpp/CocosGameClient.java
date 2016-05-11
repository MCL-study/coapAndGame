package org.cocos2dx.cpp;

import com.shylphe.lib.android.client.GameClient;
import com.shylphe.lib.android.client.GpsInfo;
import com.sylphe.app.dto.UserData;

import java.net.URI;

/**
 * Created by myks7 on 2016-05-12.
 */
public class CocosGameClient  extends GameClient{
    public CocosGameClient(URI uri, GpsInfo gpsInfo) {
        super(uri, gpsInfo);
    }

    @Override
    protected void finishNotifyLocation(double[] locData) {
        finishNotifyLocationNative(locData);
    }

    @Override
    protected void finishUpdateAllLocation(UserData[] locData) {
        finishUpdateAllLocationNative(locData);
    }

    private native void finishNotifyLocationNative(double[] locData);
    private native void finishUpdateAllLocationNative(UserData[] locData);

}
