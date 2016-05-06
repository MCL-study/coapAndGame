#ifndef __USER_DATA_JNI_UTIL_H_
#define __USER_DATA_JNI_UTIL_H_
#include "platform\android\jni\JniHelper.h"
#include "UserData.h"
#include "LocData.h"
#include "LocDataJNIUtil.h"

class UserDataJNIUtil {
private:
	static UserDataJNIUtil* instance;
	cocos2d::JniMethodInfo getIDInfo;
	cocos2d::JniMethodInfo getUserPropertiesInfo;
	cocos2d::JniMethodInfo getLocDataInfo;
	UserDataJNIUtil() {
		cocos2d::JniHelper::getMethodInfo(getIDInfo
			, "com/sylphe/app/dto/UserData"
			, "getId"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getUserPropertiesInfo
			, "com/sylphe/app/dto/UserData"
			, "getUserProperties"
			, "()I");
		cocos2d::JniHelper::getMethodInfo(getLocDataInfo
			, "com/sylphe/app/dto/UserData"
			, "getLocData"
			, "()Lcom/sylphe/app/dto/LocData;");
	}

public:
	~UserDataJNIUtil() {}
	static UserDataJNIUtil* getInstance() {
		if (instance == NULL) {
			instance = new UserDataJNIUtil();
		}
		return instance;
	}
	int getId(jobject obj) {
		int id = getIDInfo.env->CallIntMethod(obj, getIDInfo.methodID);
		return id;
	}

	int getUserProperties(jobject obj) {
		int userProperties = getUserPropertiesInfo.env->CallIntMethod(obj, getUserPropertiesInfo.methodID);
		return userProperties;
	}
	LocData getLocData(jobject obj) {
		jobject resultObj = getLocDataInfo.env->CallObjectMethod(obj, getLocDataInfo.methodID);
		LocData result = LocDataJNIUtil::getInstance()->getLocData(resultObj);
		return result;
	}

	UserData* getUserData(jobject obj) {
		return new UserData(getId(obj), getUserProperties(obj), getLocData(obj));
	}
};

#endif