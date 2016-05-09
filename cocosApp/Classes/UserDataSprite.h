#ifndef __USER_DATA_SPRITE_H_
#define __USER_DATA_SPRITE_H_
#include "cocos2d.h"
#include "UserData.h"
#include "UserProperties.h"

class UserDataSprite : public UserData{
private:
	cocos2d::Sprite* sprite = NULL;
	bool aliveFlag = true;
	cocos2d::Label* distanceLabel = NULL;

public:
	void die() {
		aliveFlag = false;
	}
	bool isAlive() {
		return aliveFlag;
	}
	UserDataSprite(UserData user):UserData(user) {
		distanceLabel = cocos2d::Label::createWithTTF("", "fonts/NanumPen.ttf", 30);
		distanceLabel->setAnchorPoint(cocos2d::Vec2(0.5, 0.5));
		distanceLabel->setColor(cocos2d::Color3B(0, 0, 0));
	}
	~UserDataSprite() {
		sprite->release();
		distanceLabel->release();
	}
	void setDistanceLabel(cocos2d::Vec2 pos,std::string text) {
		distanceLabel->setPosition(pos);
		distanceLabel->setString(text);
	}
	void addChild(cocos2d::Layer* context,int layer) {
		context->addChild(sprite, layer);
		context->addChild(distanceLabel,layer);
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