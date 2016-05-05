#include "HallPopupScene.h"
#include "UserProperties.h"
#include "GameScene.h"

USING_NS_CC;
void HallPopup::closePopup()
{
	if (this->parrentLayer) {
		Director::getInstance()->getEventDispatcher()->resumeEventListenersForTarget(parrentLayer, true);
	}
	this->removeFromParentAndCleanup(true);
}
void HallPopup::selectChaser()
{
	UserDefault::getInstance()->setIntegerForKey("properties", UserProperties::CHASER);
	closePopup();
	startGame();
}
void HallPopup::selectFugitive()
{
	UserDefault::getInstance()->setIntegerForKey("properties", UserProperties::FUGITIVE);
	closePopup();
	startGame();
}
//GameClient.cpp에 전역 선언됨
extern GameClient* gameClient;
void HallPopup::startGame()
{
	auto scene = Scene::create();
	gameClient = GameClient::create();
	scene->addChild(gameClient);
	Director::getInstance()->pushScene(scene);
}

cocos2d::Scene * HallPopup::createScene()
{
	auto scene = Scene::create();
	auto layer = HallPopup::create();
	scene->addChild(layer);
	return scene;
}

bool HallPopup::init()
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
	popup->setPosition(size.width/2,size.height/2);
	addChild(popup);
#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32 
#pragma execution_character_set("utf-8") 
#endif
	String str;
	int roomId = UserDefault::getInstance()->getIntegerForKey("roomid");
	str.appendWithFormat("%d번 방 접속하기", roomId);
	auto label = Label::createWithTTF(str._string, "fonts/NanumPen.ttf", 80);
	label->setHorizontalAlignment(TextHAlignment::CENTER);
	label->setColor(Color3B(0, 0, 0));
	label->setPosition(Vec2(size.width * 0.5f, size.height * 0.7f));
	this->addChild(label);
	auto label2 = Label::createWithTTF("역할을 선택해주세요.", "fonts/NanumPen.ttf", 65);
	label2->setHorizontalAlignment(TextHAlignment::CENTER);
	label2->setColor(Color3B(0, 0, 0));
	label2->setPosition(Vec2(size.width * 0.5f, size.height * 0.6f));
	this->addChild(label2);

	auto chaserMenuItem = MenuItemImage::create("btn_chaser.png", "btn_chaser_click.png", CC_CALLBACK_0(HallPopup::selectChaser, this));
	chaserMenuItem->setPosition(size.width / 8*3, size.height / 2.1);

	auto fugitiveMenuItem = MenuItemImage::create("btn_fugitive.png", "btn_fugitive_click.png", CC_CALLBACK_0(HallPopup::selectFugitive, this));
	fugitiveMenuItem->setPosition(size.width / 8 * 5, size.height / 2.1);

	auto closeMenuItem = MenuItemImage::create("btn_cancel.png", "btn_cancel_click.png", CC_CALLBACK_0(HallPopup::closePopup, this));
	closeMenuItem->setPosition(size.width / 2, size.height / 3);

	auto menu = Menu::create(closeMenuItem,chaserMenuItem,fugitiveMenuItem,NULL);
	menu->setPosition(Vec2::ZERO);
	this->addChild(menu);

	return true;
}

void HallPopup::showPopup(cocos2d::Layer * _parrentLayer)
{
	//팝업을 생성하고 실제로 화면에 보이개할때 부모 Layer는 이벤트를 못받도록 설정
	this->parrentLayer = _parrentLayer;
	if (parrentLayer){
		Director::getInstance()->getEventDispatcher()->pauseEventListenersForTarget(parrentLayer, true);
		parrentLayer->addChild(this, INT_MAX);
	}else{
		Director::getInstance()->getRunningScene()->addChild(this, INT_MAX);
	}
}
