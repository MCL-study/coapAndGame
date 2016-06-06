#ifndef __HALL_POPUP_SCENE_H_
#define __HALL_POPUP_SCENE_H_
#include "cocos2d.h"
#include "FrontHallScene.h"

class HallPopup :public cocos2d::Layer{
private:
	void closePopup();
	void selectChaser();
	void selectFugitive();
	FrontHall* parrentLayer;
	void startGame();
public:
	void showPopup(FrontHall* _parrentLayer);
	static cocos2d::Scene* createScene();
	virtual bool init();
	CREATE_FUNC(HallPopup);
};

#endif