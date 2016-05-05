#include "ResultPopupScene.h"
#include "ResultDataBuffer.h"

USING_NS_CC;
void ResultPopup::closePopup()
{
	if (this->parrentLayer) {
		Director::getInstance()->getEventDispatcher()->resumeEventListenersForTarget(parrentLayer, true);
	}
	this->removeFromParentAndCleanup(true);
}

void ResultPopup::selectGoOut()
{
	closePopup();
}

cocos2d::Scene * ResultPopup::createScene()
{
	auto scene = Scene::create();
	auto layer = ResultPopup::create();
	scene->addChild(layer);
	return scene;
}

bool ResultPopup::init()
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
	auto label2 = Label::createWithTTF("게임 결과", "fonts/NanumPen.ttf", 75);
	label2->setHorizontalAlignment(TextHAlignment::CENTER);
	label2->setColor(Color3B(0, 0, 0));
	label2->setPosition(Vec2(size.width * 0.5f, size.height * 0.75f));
	this->addChild(label2);

	ResultDataBuffer* buffer = ResultDataBuffer::getInstance();
	std::string playTimeStr = "플레이 타임 : ";
	playTimeStr.append(buffer->popPlayTimeString());
	playTimeStr.append(" 초");
	auto label1 = Label::createWithTTF( playTimeStr, "fonts/NanumPen.ttf", 55);
	label1->setColor(Color3B(0, 0, 0));
	label1->setPosition(Vec2(size.width * 0.5f, size.height * 0.67f));
	this->addChild(label1);

	if (buffer->isData()) {
		auto label3 = Label::createWithTTF(*(buffer->popString()), "fonts/NanumPen.ttf", 45);
		label3->setColor(Color3B(0, 0, 0));
		label3->setPosition(Vec2(size.width * 0.5f, size.height * 0.53f));
		this->addChild(label3);
	}

	auto closeMenuItem = MenuItemImage::create("btn_ok_red.png", "btn_ok_click_red.png", CC_CALLBACK_0(ResultPopup::closePopup, this));
	closeMenuItem->setPosition(size.width / 2, size.height / 3.5);

	auto menu = Menu::create(closeMenuItem, NULL);
	menu->setPosition(Vec2::ZERO);
	this->addChild(menu);

	return true;
}

void ResultPopup::showPopup(cocos2d::Layer * _parrentLayer)
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
