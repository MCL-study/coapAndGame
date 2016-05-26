#ifndef __GAME_SCECE_H_
#define __GAME_SCECE_H_

#include "cocos2d.h"
#include "UserDataSprite.h"
#include "LocData.h"
#include "RoomConfig.h"
#include <math.h>
class GameClient : public cocos2d::Layer
{
private:
	std::list<UserDataSprite*> userList;
	UserDataSprite* playerSprite;
	RoomConfig* enterRoom(int roomid, int  id, int  properties);
	void startGame(int roomid, int  id, int  properties);
	void requestStartGame(int roomid, int id, int properties);
	void close();
	void replaceHallScene();

	void updatePosition();

	float fProgressTime;
	void startTimer();
	void endTimer();
	void onTimer(float dt);


	cocos2d::Texture2D* redPlayer;
	cocos2d::Texture2D* red;
	cocos2d::Texture2D* greenPlayer;
	cocos2d::Texture2D* green;
	cocos2d::Texture2D* dead;
	void initSprite(int id, int properties);

	float pixelPerMeter =1.0f;


	virtual void onKeyPressed(cocos2d::EventKeyboard::KeyCode keyCode, cocos2d::Event* event);
	virtual void onTouchesBegan(const std::vector<cocos2d::Touch*>& touches, cocos2d::Event *unused_event);
	virtual void onTouchesMoved(const std::vector<cocos2d::Touch*>& touches, cocos2d::Event *unused_event);
	virtual void onTouchesEnded(const std::vector<cocos2d::Touch*>& touches, cocos2d::Event *unused_event);
	float beforeTouchLength;
	bool touchFlag=false;
	bool aliveFlag = false;

	void checkCollision();
	void drawBetweenDistance();
	void diePlayer(int playerId);
	void catchFugitive(int fugitiveId);

	cocos2d::DrawNode* drawNode;
	int roomScale;
	LocData roomCenter;
	void drawRoomScale();
	
	virtual bool init();
	virtual void onEnter();
public:
	CREATE_FUNC(GameClient);
	~GameClient();
	virtual void update(float delta);

	void addUserData(UserData* user);
	void setPlayerLoc(LocData loc);
};

#endif