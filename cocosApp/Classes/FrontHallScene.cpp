#include "FrontHallScene.h"
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform\android\jni\JniHelper.h"
#include "RoomConfigJNIUtil.h"
#endif
#include "MakeRoomPopupScene.h"
#include "ResultDataBuffer.h"
#include "ResultPopupScene.h"

USING_NS_CC;

void FrontHall::checkResult()
{
	log("checkResult call");
	if (ResultDataBuffer::getInstance()->isData()) {
		ResultPopup* popup = ResultPopup::create();
		popup->showPopup(this);
	}
}

FrontHall::~FrontHall()
{
	//log("~FrontHall");
	delete scrollView;
}

Scene* FrontHall::createScene()
{
	auto layer = FrontHall::create();
    auto scene = Scene::create();
	scene->addChild(layer);
	return scene;
}

// on "init" you need to initialize your instance
bool FrontHall::init()
{
    if ( !Layer::init())
    {
        return false;
    }

    Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
	scrollView = new RoomScrollView(this,Size(visibleSize.width,visibleSize.height*0.8));
	scrollView->setPosition(Vec2(80, 0));

	auto _screenSize = Director::getInstance()->getWinSize();

	Sprite* background = Sprite::create("hall_bg.png");
	background->setPosition(Vec2(_screenSize.width * 0.5f, _screenSize.height * 0.5f));
	double scale = _screenSize.width / background->getContentSize().width;
	background->setScale(scale);
	this->addChild(background,0);

	EventDispatcher* dispatcher = Director::getInstance()->getEventDispatcher();
	auto keyListener = EventListenerKeyboard::create();
	keyListener->onKeyPressed = CC_CALLBACK_2(FrontHall::onKeyPressed, this);
	dispatcher->addEventListenerWithSceneGraphPriority(keyListener, this);

#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32 
#pragma execution_character_set("utf-8") 
#endif
	int id =UserDefault::getInstance()->getIntegerForKey("id");
	String str;
	str.appendWithFormat("User ID : %d ",id);
	auto infoLabel = Label::createWithTTF(str._string, "fonts/NanumPen.ttf", 100);
	infoLabel->setAnchorPoint(Vec2(0.0, 0.5));
	infoLabel->setColor(Color3B(0, 0, 0));
	infoLabel->setPosition(Vec2(_screenSize.width * 0.1f, _screenSize.height * 0.90f));
	this->addChild(infoLabel, 1);

	roomClearLabel = Label::createWithTTF("방이 없습니다.\n방을 만들어 주세요.", "fonts/NanumPen.ttf", 100);
	roomClearLabel->setHorizontalAlignment(TextHAlignment::CENTER);
	roomClearLabel->setColor(Color3B(0, 0, 0));
	roomClearLabel->setPosition(Vec2(_screenSize.width * 0.5f, _screenSize.height * 0.5f));
	this->addChild(roomClearLabel, 1);

	auto makeRoomBtnItem = MenuItemImage::create(
		"btn_room.png",
		"btn_room_click.png",
		CC_CALLBACK_1(FrontHall::makeRoomCallback, this));
	auto btnSize = makeRoomBtnItem->getContentSize();
	auto btnPos = Vec2(_screenSize.width - btnSize.width / 1.65, _screenSize.height - btnSize.height / 1.65);
	makeRoomBtnItem->setPosition(btnPos);

	auto refreshBtnItem = MenuItemImage::create(
		"btn_refresh.png",
		"btn_refresh_click.png",
		CC_CALLBACK_1(FrontHall::refreshRoomCallback, this));
	refreshBtnItem->setPosition(btnPos.x - refreshBtnItem->getContentSize().width*1.35, btnPos.y);

	auto menu1 = Menu::create(makeRoomBtnItem, refreshBtnItem, NULL);
	menu1->setPosition(Vec2::ZERO);
	this->addChild(menu1, 1);

    return true;
}

void FrontHall::onKeyPressed(EventKeyboard::KeyCode keyCode, Event * event)
{
	log("onKeyPressed");
	if (keyCode == EventKeyboard::KeyCode::KEY_BACK) {
		Director::getInstance()->end();
	}
}

void FrontHall::makeRoomCallback(Ref* pSender)
{
	MakeRoomPopup* popup = MakeRoomPopup::create();
	popup->showPopup(this);
}

void FrontHall::refreshRoomCallback(Ref* pSender) {
	std::list<RoomConfig*>* roomList = refreshRoom();
	log("refreshRoomCalback call");
	if(roomList == NULL){
		log("roomClearLabel set call");
		roomClearLabel->setVisible(true);
		log("roomClearLabel set2 call");
	}else if (roomList->size() != 0) {
		roomClearLabel->setVisible(false);
		scrollView->updateRoomConfigs(roomList);
		roomList->clear();
		delete(roomList);
	}
}

std::list<RoomConfig*>* FrontHall::refreshRoom() {
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
	JniMethodInfo t;
	jobjectArray objArr;
	std::list<RoomConfig*>* roomList = NULL;
	log("refreshRoom Call");
	if (JniHelper::getStaticMethodInfo(t
		, "org/cocos2dx/cpp/AppActivity"
		, "requestRoomList"
		, "()[Lcom/sylphe/app/dto/RoomConfig;")) {
		objArr = (jobjectArray)t.env->CallStaticObjectMethod(t.classID, t.methodID);
		log("refreshRoom Call : CallStaticObjectMethod");
		if (objArr == NULL)
			return NULL;
		log("refreshRoom Call : GetArrayLength b");
		jsize objArrLength = t.env->GetArrayLength(objArr);
		log("refreshRoom Call : GetArrayLength");
		int arrSize = (int)objArrLength;
		if (arrSize == 0)
			return NULL;
		log("init refreshRoom Call : arrSize");
		roomList = new std::list<RoomConfig*>;
		RoomCfgJNIUtil *roomUtil = RoomCfgJNIUtil::getInstance();
		for (int i = 0; i < arrSize; i++) {
			jobject obj = t.env->GetObjectArrayElement(objArr, i);
			int roomid = roomUtil->getRoomID(obj);
			int maxGameMember = roomUtil->getMaxGameMember(obj);
			int scale = roomUtil->getScale(obj);
			roomList->push_back(new RoomConfig(roomid, maxGameMember, scale));
		}
		//t.env->DeleteLocalRef(t.classID);
	}
	log("refreshRoom Call : return ");
	return roomList;
#endif
	return NULL;
}

void FrontHall::onEnter()
{
	cocos2d::log("onEnter call");
	Layer::onEnter();
	refreshRoomCallback(NULL);
	checkResult();
}



