package org.cocos2dx.cpp;

import android.os.Handler;
import android.os.Message;
import com.shylphe.lib.android.client.GameClient;
import com.shylphe.lib.android.client.GpsInfo;
import com.sylphe.app.dto.UserData;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

import java.net.URI;

/**
 * Created by myks7 on 2016-05-12.
 */
class CocosGameClient  extends GameClient{
    private Cocos2dxGLSurfaceView mGLView;
    CocosGameClient(URI uri, GpsInfo gpsInfo, Cocos2dxGLSurfaceView GLView) {
        super(uri, gpsInfo);
        mGLView = GLView;
    }
    private double[] locationData;
    private UserData[] locationDatas;

    @Override
    protected void finishNotifyLocation(double[] locData) {
        locationData=locData;
        mGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                finishNotifyLocationNative(locationData);
            }
        });
    }

    @Override
    protected void finishUpdateAllUserData(UserData[] locData) {
        locationDatas=locData;
        mGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                finishUpdateAllUserDataNative(locationDatas);
            }
        });
    }

    private native void finishNotifyLocationNative(double[] locData);
    private native void finishUpdateAllUserDataNative(UserData[] locData);

}
