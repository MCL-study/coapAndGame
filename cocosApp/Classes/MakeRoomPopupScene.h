#ifndef __MAKE_ROOM_POPUP_SCENE_H_
#define __MAKE_ROOM_POPUP_SCENE_H_
#include "cocos2d.h"
#include "FrontHallScene.h"

class MakeRoomPopup :public cocos2d::Layer {
private:
	void closePopup();
	FrontHall* parrentLayer;
	void okPopup();
	int makeRoom();
	int maxGameMember = 99999;
	int scale = 2000;
	int timeLimit = 20;
public:
	void showPopup(FrontHall* parrentLayer);
	static cocos2d::Scene* createScene();
	virtual bool init();
	CREATE_FUNC(MakeRoomPopup);

};

#endif