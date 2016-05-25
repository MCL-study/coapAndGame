#include "MakeRoomPopupScene.h"
#include "CustomUtil.h"
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform\android\jni\JniHelper.h"
#endif
USING_NS_CC;
void MakeRoomPopup::closePopup()
{
	if (this->parrentLayer) {
		Director::getInstance()->getEventDispatcher()->resumeEventListenersForTarget(parrentLayer, true);
	}
	this->removeFromParentAndCleanup(true);
}

void MakeRoomPopup::okPopup()
{
	int roomid = makeRoom();
	if (roomid == NULL) {
		//방 만들기 실패
		log("okpopup() makeRoom error");
	}
	else {
		parrentLayer->refreshRoomCallback(NULL);
	}
	closePopup();
}

int MakeRoomPopup::makeRoom() {
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
	JniMethodInfo t;
	jint roomid = NULL;
	log("call make room");
	if (JniHelper::getStaticMethodInfo(t
		, "org/cocos2dx/cpp/AppActivity"
		, "makeRoom"
		, "(III)I")) {
		roomid = (jint)t.env->CallStaticIntMethod(t.classID, t.methodID, maxGameMember, scale, timeLimit);
	}
	t.env->DeleteLocalRef(t.classID);
	log("call make room : return %d", roomid);
	return roomid;
#endif
	return NULL;
}

cocos2d::Scene * MakeRoomPopup::createScene()
{
	auto scene = Scene::create();
	auto layer = MakeRoomPopup::create();
	scene->addChild(layer);
	return scene;
}

bool MakeRoomPopup::init()
{
	if (!Layer::init())
	{
		return false;
	}
	auto size = Director::getInstance()->getWinSize();

	auto popupBG = Sprite::create("popup_bg.png");
	popupBG->setPosition(size.width / 2, size.height / 2);
	popupBG->setOpacity(150);
	addChild(popupBG);

	auto popup = Sprite::create("popup.png");
	popup->setPosition(size.width / 2, size.height / 2);
	addChild(popup);
#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32 
#pragma execution_character_set("utf-8") 
#endif
	auto label = Label::createWithTTF("방 만들기", "fonts/NanumPen.ttf", 80);
	label->setHorizontalAlignment(TextHAlignment::CENTER);
	label->setColor(Color3B(0, 0, 0));
	label->setPosition(Vec2(size.width * 0.5f, size.height * 0.755f));
	this->addChild(label);
	std::string str = "참여 인원 : "+ CustomUtil::toString(maxGameMember)+"\n범위 : "+ CustomUtil::toString(scale)+"m \n제한시간 : " + CustomUtil::toString(timeLimit) + "초";

	auto label2 = Label::createWithTTF(str.c_str(), "fonts/NanumPen.ttf", 55);
	label2->setColor(Color3B(0, 0, 0));
	label2->setPosition(Vec2(size.width * 0.3f, size.height * 0.55f));
	this->addChild(label2);

	auto okMenuItem = MenuItemImage::create("btn_ok.png", "btn_ok_click.png", CC_CALLBACK_0(MakeRoomPopup::okPopup, this));
	okMenuItem->setPosition(size.width *0.4f, size.height / 3);

	auto closeMenuItem = MenuItemImage::create("btn_cancel.png", "btn_cancel_click.png", CC_CALLBACK_0(MakeRoomPopup::closePopup, this));
	closeMenuItem->setPosition(size.width *0.6f, size.height / 3);

	auto menu = Menu::create(closeMenuItem, okMenuItem, NULL);
	menu->setPosition(Vec2::ZERO);
	this->addChild(menu);

	return true;
}

void MakeRoomPopup::showPopup(FrontHall * _parrentLayer)
{
	//팝업을 생성하고 실제로 화면에 보이개할때 부모 Layer는 이벤트를 못받도록 설정
	this->parrentLayer = _parrentLayer;
	if (parrentLayer) {
		Director::getInstance()->getEventDispatcher()->pauseEventListenersForTarget(parrentLayer, true);
		parrentLayer->addChild(this, INT_MAX);
	}
	else {
		Director::getInstance()->getRunningScene()->addChild(this, INT_MAX);
	}
}
