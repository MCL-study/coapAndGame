package com.sylphe.app.dto;

/**
 * Created by myks7 on 2016-03-16.
 */

public enum UserProperties {
    FUGITIVE(2),
    CHASER(3),
    GHOST(4),
    NOT_DEFINE(5),
    UNKNOWN_CODE(6);
    public final int value;
    private UserProperties(int value) {
        this.value = value;
    }

    public static UserProperties valueOf(int value) {
        switch (value) {
            case 2:return FUGITIVE;
            case 3:return CHASER;
            case 4:return GHOST;
            case 5:return NOT_DEFINE;
            default:return UNKNOWN_CODE;
        }
    }

    public static boolean isValidProperties(int value) {
        return UserProperties.valueOf(value) != UserProperties.UNKNOWN_CODE;
    }
}