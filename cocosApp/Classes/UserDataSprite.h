#ifndef __USER_DATA_SPRITE_H_
#define __USER_DATA_SPRITE_H_
#include "cocos2d.h"
#include "UserData.h"
#include "UserProperties.h"

class UserDataSprite : public UserData{
private:
	cocos2d::Sprite* sprite = NULL;
	bool aliveFlag = true;

public:
	void die() {
		aliveFlag = false;
	}
	bool isAlive() {
		return aliveFlag;
	}
	UserDataSprite(UserData user):UserData(user) {
	}
	~UserDataSprite() {
		sprite->release();
	}
	void setSprite(cocos2d::Sprite* sprite) {
		this->sprite = sprite;
		sprite->setAnchorPoint(cocos2d::Vec2(0.5f, 0.5f));
	//	sprite->setPosition(-2000, -2000);
	}

	cocos2d::Sprite* getSprite() {
		return sprite;
	}

	void setPosition(cocos2d::Vec2 pos) {
		sprite->setPosition(pos);
	}
};

#endif