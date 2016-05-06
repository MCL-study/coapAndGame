#ifndef __LOC_DATA_JNI_UTIL_H_
#define __LOC_DATA_JNI_UTIL_H_
#include "platform\android\jni\JniHelper.h"
#include "LocData.h"
class LocDataJNIUtil {
private:
	static LocDataJNIUtil* instance;
	cocos2d::JniMethodInfo getLatInfo;
	cocos2d::JniMethodInfo getLngInfo;
	LocDataJNIUtil() {
		cocos2d::JniHelper::getMethodInfo(getLatInfo
			, "com/sylphe/app/dto/LocData"
			, "getLat"
			, "()D");
		cocos2d::JniHelper::getMethodInfo(getLngInfo
			, "com/sylphe/app/dto/LocData"
			, "getLng"
			, "()D");
	}
public:
	~LocDataJNIUtil() {}
	static LocDataJNIUtil* getInstance() {
		if (instance == NULL)
			instance = new LocDataJNIUtil();
		return instance;
	}

	double getLat(jobject obj) {
		double lat = getLatInfo.env->CallDoubleMethod(obj, getLatInfo.methodID);
		return lat;
	}

	double getLng(jobject obj) {
		double lng = getLngInfo.env->CallDoubleMethod(obj, getLngInfo.methodID);
		return lng;
	}

	LocData getLocData(jobject obj) {
		return LocData(getLat(obj), getLng(obj));
	}
};

#endif