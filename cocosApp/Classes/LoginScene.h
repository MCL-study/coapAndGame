#ifndef __LOGIN_SCENE_H__
#define __LOGIN_SCENE_H__

#include "cocos2d.h"

class Login : public cocos2d::Layer
{
private:
	cocos2d::Label* errorLabel;
	cocos2d::Label* connectingLabel;
	cocos2d::Label* msgLabel;
	
	virtual bool init();
	virtual bool onTouchBegan(cocos2d::Touch* touch, cocos2d::Event* event);
	virtual void onTouchEnded(cocos2d::Touch* touch, cocos2d::Event* event);

	virtual void onKeyPressed(cocos2d::EventKeyboard::KeyCode keyCode, cocos2d::Event* event);

	void initGameScene(void);
	void changeScene(void);
	int requestID();
	
public:
	CREATE_FUNC(Login);
	static cocos2d::Scene* createScene();
};

#endif 