#include "GameScene.h"
#include "UserProperties.h"
#include "GamePopupScene.h"
#include "ResultDataBuffer.h"
#include "FrontHallScene.h"
USING_NS_CC;
GameClient* gameClient = NULL;


bool GameClient::init()
{
	if (!Layer::init())
	{
		return false;
	}
	auto _screenSize = Director::getInstance()->getWinSize();
	// back ground image
	Sprite* background = Sprite::create("game_bg.png");
	background->setPosition(Vec2(_screenSize.width * 0.5f, _screenSize.height * 0.5f));
	double scale = _screenSize.width / background->getContentSize().width;
	background->setScale(scale);
	this->addChild(background, 0);

	auto ins = UserDefault::getInstance();
	int roomid = ins->getIntegerForKey("roomid");
	int id = ins->getIntegerForKey("id");
	int properties = ins->getIntegerForKey("properties");

	initSprite(id, properties);

	startGame(roomid,id, properties);

	EventDispatcher* dispatcher = Director::getInstance()->getEventDispatcher();
	auto listener = EventListenerTouchAllAtOnce::create();
	listener->onTouchesBegan = CC_CALLBACK_2(GameClient::onTouchesBegan, this);
	listener->onTouchesMoved = CC_CALLBACK_2(GameClient::onTouchesMoved, this);
	dispatcher->addEventListenerWithFixedPriority(listener, 1);
	
	auto keyListener = EventListenerKeyboard::create();
	keyListener->onKeyPressed = CC_CALLBACK_2(GameClient::onKeyPressed, this);
	dispatcher->addEventListenerWithSceneGraphPriority(keyListener, this);
	
	drawNode = DrawNode::create();
	drawNode->setPosition(Vec2::ZERO);
	addChild(drawNode, 1);
	aliveFlag = true;
	this->scheduleUpdate();
	return true;
}

void GameClient::onKeyPressed(EventKeyboard::KeyCode keyCode, Event * event)
{
	log("onKeyPressed");
	if (keyCode == EventKeyboard::KeyCode::KEY_BACK) {
		GamePopup* popup = GamePopup::create();
		popup->showPopup(this);
	}
}
void GameClient::replaceHallScene()
{
	log("replaceHallScene call");
//	auto scene = FrontHall::createScene();
	Director::getInstance()->popScene();
}


GameClient::~GameClient()
{
	cocos2d::log("~gameClient");
	this->unscheduleUpdate();
	endTimer();
	gameClient = NULL;
	close();
}

void GameClient::update(float delta)
{
	Layer::update(delta);
	updatePosition();
	drawRoomScale();
	drawBetweenDistance();
}

void GameClient::setPlayerLoc(LocData loc)
{
	playerSprite->setLocData(loc);
	checkCollision();
}

void GameClient::addUserData(UserData* user)
{
	log("addUserData call");
	bool exist = false;
	for (auto i = userList.begin(); i != userList.end(); i++) {
		if ((*i)->getId() == user->getId()) {
			if ((*i)->isAlive()) {
				(*i)->setLocData(user->getLocData());
				(*i)->setUserProperties(user->getUserProperties());
			}
			exist = true;
			break;
		}
	}
	if (!exist) {
		auto sprite = new UserDataSprite(*user);
		if (user->getUserProperties() == UserProperties::CHASER) {
			auto tempSprite = Sprite::createWithTexture(red);
			sprite->setSprite(tempSprite);
		}else if (user->getUserProperties() == UserProperties::FUGITIVE){
			auto tempSprite = Sprite::createWithTexture(green);
			sprite->setSprite(tempSprite);
		}
		else if (user->getUserProperties() == UserProperties::GHOST) {
			auto tempSprite = Sprite::createWithTexture(dead);
			sprite->setSprite(tempSprite);
		}
		sprite->addChild(this, 2);
		userList.push_back(sprite);
	}
	checkCollision();
}

void GameClient::startTimer()
{
	fProgressTime = 0.0f;
	this->schedule(schedule_selector(GameClient::onTimer), 0.25f);
}

void GameClient::endTimer()
{
	this->unschedule(schedule_selector(GameClient::onTimer));
}

void GameClient::onTimer(float dt)
{
	fProgressTime = fProgressTime + dt;

	ResultDataBuffer::getInstance()->setPlayTime(fProgressTime);
}

void GameClient::onEnter()
{
	Layer::onEnter();
	startTimer();
}

void GameClient::startGame(int roomid, int  id, int  properties) {
	RoomConfig* roomConfig= enterRoom(roomid, id, properties);
	if (roomConfig == nullptr) {
		log("enterRoom error");
	}
	else {
		requestStartGame(roomid, id, properties);
		roomCenter = roomConfig->getCenterLoc();
		roomScale = roomConfig->getScale();
		delete roomConfig;
	}
}

void GameClient::requestStartGame(int roomid, int id, int properties) {
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
	JniMethodInfo t;
	log("startGame Call");
	if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/cpp/AppActivity", "startGame", "(III)V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID, roomid,id, properties);
	}
#endif
}

void GameClient::updatePosition()
{
	log("updatePosition call");
	auto visibleSize = Director::getInstance()->getVisibleSize();
	playerSprite->setPosition(visibleSize / 2);
	auto playerLoc = playerSprite->getLocData();
	auto screenCenter = visibleSize / 2;

	for (auto i = userList.begin(); i != userList.end(); i++) {
		if (playerSprite->getId() != (*i)->getId()) {
			LocData locData = (*i)->getLocData();
			float dY = LocData::computeDistanceLat(playerLoc.getLat(), locData.getLat());
			float dX = LocData::computeDistanceLng(playerLoc.getLng(), locData.getLng());
			(*i)->setPosition(Vec2(screenCenter.width + dX*pixelPerMeter, screenCenter.height + dY*pixelPerMeter));
		}
	}
	
}

void GameClient::drawRoomScale()
{
	log("drawRoomScale call");
	auto visibleSize = Director::getInstance()->getVisibleSize();
	auto screenCenter = visibleSize / 2;
	auto playerLoc = playerSprite->getLocData();
	float dY = LocData::computeDistanceLat(playerLoc.getLat(), roomCenter.getLat());
	float dX = LocData::computeDistanceLng(playerLoc.getLng(), roomCenter.getLng());
	drawNode->clear();
	drawNode->drawDot(Vec2(screenCenter.width + dX*pixelPerMeter, screenCenter.height+ dY*pixelPerMeter), roomScale*pixelPerMeter, Color4F::GRAY);
}

void GameClient::initSprite(int id,int properties)
{
	greenPlayer = Director::getInstance()->getTextureCache()->addImage("point_green_player.png");
	green = Director::getInstance()->getTextureCache()->addImage("point_green.png");
	redPlayer = Director::getInstance()->getTextureCache()->addImage("point_red_player.png");
	red = Director::getInstance()->getTextureCache()->addImage("point_red.png");
	dead = Director::getInstance()->getTextureCache()->addImage("point_dead.png");

	playerSprite = new UserDataSprite(UserData(id, properties));
	if (properties == UserProperties::CHASER) {
		auto sprite = Sprite::createWithTexture(redPlayer);
		playerSprite->setSprite(sprite);
	}
	else if (properties == UserProperties::FUGITIVE) {
		auto sprite = Sprite::createWithTexture(greenPlayer);
		playerSprite->setSprite(sprite);
	}
	playerSprite->addChild(this, 3);
//	addChild(playerSprite->getSprite(), 3);
}

void GameClient::checkCollision()
{
	log("checkCollision call");
	for (auto i = userList.begin(); i != userList.end(); i++) {
		if ((*i)->isAlive()) {
			LocData locData = (*i)->getLocData();
			LocData playerLocData = playerSprite->getLocData();
			float distance =LocData::computeDistance(playerLocData.getLat(), playerLocData.getLng(), locData.getLat(), locData.getLng());

			if (distance < 5) {
				if (playerSprite->getUserProperties() == UserProperties::CHASER) {
					if ((*i)->getUserProperties() == UserProperties::FUGITIVE) {
						log("catch : %d", (*i)->getId());
						catchFugitive((*i)->getId());
						(*i)->die();
						removeChild((*i)->getSprite());
						Sprite* sprite = Sprite::createWithTexture(dead);
						(*i)->setSprite(sprite);
						addChild(sprite);
						ResultDataBuffer* buffer = ResultDataBuffer::getInstance();
						buffer->appendCatchMessage((*i)->getId());
					}
				}
				else if (playerSprite->getUserProperties() == UserProperties::FUGITIVE) {
					if ((*i)->getUserProperties() == UserProperties::CHASER) {
						log("die player : %d", (*i)->getId());
						ResultDataBuffer* buffer = ResultDataBuffer::getInstance();
						buffer->appendDieMessage((*i)->getId());
						diePlayer(playerSprite->getId());
					}
				}
			}
		}
	}
}

void GameClient::drawBetweenDistance()
{
	log("drawBetweenDistance call");
	for (auto i = userList.begin(); i != userList.end(); i++) {
		if ((*i)->isAlive()) {
			LocData locData = (*i)->getLocData();
			LocData playerLocData = playerSprite->getLocData();
			float distance = LocData::computeDistance(playerLocData.getLat(), playerLocData.getLng(), locData.getLat(), locData.getLng());

			//distance label
			float dY = LocData::computeDistanceLat(playerLocData.getLat(), locData.getLat());
			float dX = LocData::computeDistanceLng(playerLocData.getLng(), locData.getLng());
			auto visibleSize = Director::getInstance()->getVisibleSize();
			auto screenCenter = visibleSize / 2;
			Vec2 pos(screenCenter.width + dX / 2 * pixelPerMeter, screenCenter.height + dY / 2 * pixelPerMeter);
			//drawNode->drawDot(pos, 20, Color4F::BLACK);
			String str;
			str.appendWithFormat("%dm ", (int)distance);
			(*i)->setDistanceLabel(pos, str._string);
			//this->addChild(distanceLabel, 1);
		}
	}
}

void GameClient::onTouchesBegan(const std::vector<Touch*>& touches, Event * unused_event)
{
	log("onTouchesBegan call");
	if (touches.size() == 2) {
		beforeTouchLength = (touches.front()->getLocation() - touches.back()->getLocation()).length();
		touchFlag = true;
	}
}

void GameClient::onTouchesMoved(const std::vector<Touch*>& touches, Event * unused_event)
{
	log("onTouchesMoved call");
	if (touches.size() == 2) {
		if (!touchFlag) {
			beforeTouchLength = (touches.front()->getLocation() - touches.back()->getLocation()).length();
			touchFlag = true;  
		}else {
			auto currentTouchLength = (touches.front()->getLocation() - touches.back()->getLocation()).length();
			if (beforeTouchLength > currentTouchLength)
				pixelPerMeter *= 0.99f;
			else if(beforeTouchLength < currentTouchLength)
				pixelPerMeter *= 1.01f;
			beforeTouchLength = currentTouchLength;
		}
	}
}

void GameClient::onTouchesEnded(const std::vector<Touch*>& touches, Event * unused_event)
{
	log("onTouchesEnded call");
	touchFlag = false;
}

#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform\android\jni\JniHelper.h"
#endif
void GameClient::diePlayer(int playerId)
{
	if (aliveFlag) {
		aliveFlag = false;
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
		JniMethodInfo t;
		log("diePlayer Call");
		if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/cpp/AppActivity", "diePlayer", "(I)V")) {
			t.env->CallStaticVoidMethod(t.classID, t.methodID, playerId);
		}
#endif
		endTimer();
		close();
		replaceHallScene();
	}
}

void GameClient::close()
{
	aliveFlag = false;
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
	JniMethodInfo t;
	log("close Call");
	if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/cpp/AppActivity", "closeGameClient", "()V")) {
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
	}
#endif
}

void GameClient::catchFugitive(int fugitiveId)
{
	if (aliveFlag) {
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
		JniMethodInfo t;
		log("catchFugitive Call");
		if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/cpp/AppActivity", "catchFugitive", "(I)V")) {
			t.env->CallStaticVoidMethod(t.classID, t.methodID, fugitiveId);
		}
#endif
	}
}

#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform\android\jni\JniHelper.h"
#include "UserDataJNIUtil.h"

#ifdef __cplusplus
extern "C"
#endif
void Java_org_cocos2dx_cpp_CocosGameClient_finishNotifyLocationNative(JNIEnv* ent, jobject obj, jdoubleArray arr) {
	if (gameClient != NULL) {
		log("call c finishNotifyLocation");
		jboolean bol = JNI_TRUE;
		double* loc = (double*)ent->GetDoubleArrayElements(arr, &bol);
		gameClient->setPlayerLoc(LocData(loc[0], loc[1]));
	}
}

#ifdef __cplusplus
extern "C"
#endif
void Java_org_cocos2dx_cpp_CocosGameClient_finishUpdateAllUserDataNative(JNIEnv* ent, jobject obj, jobjectArray userDatas) {
	if (gameClient != NULL) {
		int id = UserDefault::getInstance()->getIntegerForKey("id");
		log("call c finishUpdateAllUserData");
		int length = ent->GetArrayLength(userDatas);
		UserDataJNIUtil* jniUtil = UserDataJNIUtil::getInstance();
		for (int i = 0; i < length; i++) {
			jobject data = ent->GetObjectArrayElement(userDatas, i);
			UserData* userData = jniUtil->getUserData(data);
			if (userData->getId() != id)
				gameClient->addUserData(userData);
		}
	}
}

#endif

#include "RoomConfigJNIUtil.h"
RoomConfig* GameClient::enterRoom(int roomid, int  id, int  properties) {
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
	JniMethodInfo t;
	jobject jobj = NULL;
	RoomConfig* roomConfig;
	log("call enter room");
	if (JniHelper::getStaticMethodInfo(t
		, "org/cocos2dx/cpp/AppActivity"
		, "enterRoom"
		, "(III)Lcom/sylphe/app/dto/RoomConfig;")) {
		jobj = (jobject)t.env->CallStaticObjectMethod(t.classID, t.methodID, roomid, id, properties);

		RoomCfgJNIUtil *roomUtil = RoomCfgJNIUtil::getInstance();
		
		int roomid = roomUtil->getRoomID(jobj);
		int maxGameMember = roomUtil->getMaxGameMember(jobj);
		int scale = roomUtil->getScale(jobj);
		LocData centerLoc = roomUtil->getCenterLocData(jobj);
		roomConfig = new RoomConfig(roomid, centerLoc, maxGameMember, scale);
	}
	t.env->DeleteLocalRef(t.classID);
	return roomConfig;
#endif
	return nullptr;
}