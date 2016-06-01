#ifndef __RESULT_DATA_BUFFER_H_
#define __RESULT_DATA_BUFFER_H_
#include "cocos2d.h"
#include "CustomUtil.h"

class ResultDataBuffer {
private:
	static ResultDataBuffer* instance;
	ResultDataBuffer() {
		dataFlag = false;
	}
	float playTime;
	std::string str;
	bool dataFlag;
public:
	void appendDieMessage(int killerID) {
		dataFlag = true;
		str = "[ID:"+ CustomUtil::toString(killerID) + "]에게 잡혔습니다.\n";
	}

	void appendCatchMessage(int fugitiveID) {
		dataFlag = true;
		str += "[ID:" + CustomUtil::toString(fugitiveID) + "]을 잡았습니다.\n";
	}
	
	void appendTimeoutMessage() {
		dataFlag = true;
		str += "제한시간이 종료 되었습니다.\n";
	}

	static ResultDataBuffer* getInstance() {
		if (instance == NULL)
			instance = new ResultDataBuffer();
		return instance;
	}
	std::string* popString() {
		dataFlag = false;
		std::string* temp = new std::string(str);
		str.clear();
		return temp;
	}

	bool isData() {
		return dataFlag;
	}

	

	float popPlayTime() {
		playTime = 0;
		return playTime;
	}

	std::string popPlayTimeString() {
		auto result = CustomUtil::toString(playTime);
		auto pos = result.find_first_of("."); //스트링에서 소수점의 . 위치를 찾습니다.
		result = result.substr(0, pos + 3); //0부터 .의 위치 + 3 까지 잘라옵니다.
		playTime = 0;
		return result;
	}

	void setPlayTime(float time) {
		playTime = time;
	}
};

#endif