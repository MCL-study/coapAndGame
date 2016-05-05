#ifndef __ROOM_CONFIG_JNI_UTIL_H_
#define __ROOM_CONFIG_JNI_UTIL_H_
#include "platform\android\jni\JniHelper.h"

class RoomCfgJNIUtil {
private:
	static RoomCfgJNIUtil* instance;
	cocos2d::JniMethodInfo getRoomIDInfo;
	cocos2d::JniMethodInfo getMaxGameMemberInfo;
	cocos2d::JniMethodInfo getScaleInfo;
	RoomCfgJNIUtil() {
		cocos2d::JniHelper::getMethodInfo(getRoomIDInfo
			, "org/cocos2dx/cpp/dto/RoomConfig"
			, "getRoomID"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getMaxGameMemberInfo
			, "org/cocos2dx/cpp/dto/RoomConfig"
			, "getMaxGameMember"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getScaleInfo
			, "org/cocos2dx/cpp/dto/RoomConfig"
			, "getScale"
			, "()I");
	}
public:
	~RoomCfgJNIUtil() {}

	static RoomCfgJNIUtil* getInstance() {
		if (instance == NULL) {
			instance = new RoomCfgJNIUtil();
		}
		return instance;
	}
	int getRoomID(jobject obj) {
		int roomID = getRoomIDInfo.env->CallIntMethod(obj, getRoomIDInfo.methodID);
		return roomID;
	}
	int getMaxGameMember(jobject obj) {
		int maxGameMember = getMaxGameMemberInfo.env->CallIntMethod(obj, getMaxGameMemberInfo.methodID);
		return maxGameMember;
	}
	int getScale(jobject obj) {
		int scale = getScaleInfo.env->CallIntMethod(obj, getScaleInfo.methodID);
		return scale;
	};
};

#endif