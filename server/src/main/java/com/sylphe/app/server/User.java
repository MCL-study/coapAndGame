package com.sylphe.app.server;

import com.sylphe.app.dto.LocData;
import com.sylphe.app.dto.UserData;
import com.sylphe.app.dto.UserProperties;

import java.net.InetAddress;

/**
 * Created by myks7 on 2016-05-30.
 */
public class User extends UserData {
    private InetAddress sourceAddress;
    private int sourcePort;
    public User(int id, UserProperties userProperties, LocData location) {
        super(id, userProperties, location);
    }

    public User(InetAddress sourceAddress, int sourcePort, int id, UserProperties userProperties) {
        super(id, userProperties);
        this.sourceAddress=sourceAddress;
        this.sourcePort = sourcePort;
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }
}
