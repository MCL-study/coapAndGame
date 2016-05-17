#ifndef __ROOM_CONFIG_JNI_UTIL_H_
#define __ROOM_CONFIG_JNI_UTIL_H_
#include "platform\android\jni\JniHelper.h"
#include "LocDataJNIUtil.h"

class RoomCfgJNIUtil {
private:
	static RoomCfgJNIUtil* instance;
	cocos2d::JniMethodInfo getRoomIDInfo;
	cocos2d::JniMethodInfo getMaxGameMemberInfo;
	cocos2d::JniMethodInfo getScaleInfo;
	cocos2d::JniMethodInfo getCenterLocInfo;
	RoomCfgJNIUtil() {
		cocos2d::JniHelper::getMethodInfo(getRoomIDInfo
			, "com/sylphe/app/dto/RoomConfig"
			, "getRoomID"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getMaxGameMemberInfo
			, "com/sylphe/app/dto/RoomConfig"
			, "getMaxGameMember"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getScaleInfo
			, "com/sylphe/app/dto/RoomConfig"
			, "getScale"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getCenterLocInfo
			, "com/sylphe/app/dto/RoomConfig"
			, "getCenterLoc"
			, "()Lcom/sylphe/app/dto/LocData;");
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
	LocData getCenterLocData(jobject obj) {
		jobject jobj = getCenterLocInfo.env->CallObjectMethod(obj, getCenterLocInfo.methodID);
		LocDataJNIUtil *locDataJNIUtil = LocDataJNIUtil::getInstance();
		LocData result = locDataJNIUtil->getLocData(jobj);
		return result;
	}
};

#endif