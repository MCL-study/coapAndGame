#ifndef __GAME_POPUP_SCENE_H_
#define __GAME_POPUP_SCENE_H_
#include "cocos2d.h"

class GamePopup :public cocos2d::Layer{
private:
	void closePopup();
	void selectGoOut();
	cocos2d::Layer* parrentLayer;
	void replaceHallScene();
public:
	void showPopup(cocos2d::Layer* parrentLayer);
	static cocos2d::Scene* createScene();
	virtual bool init();
	CREATE_FUNC(GamePopup);
};

#endif