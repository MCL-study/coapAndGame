#include "GamePopupScene.h"
#include "FrontHallScene.h"

USING_NS_CC;
void GamePopup::closePopup()
{
	if (this->parrentLayer) {
		Director::getInstance()->getEventDispatcher()->resumeEventListenersForTarget(parrentLayer, true);
	}
	this->removeFromParentAndCleanup(true);
}

void GamePopup::selectGoOut()
{
	closePopup();
	replaceHallScene();
}

void GamePopup::replaceHallScene()
{
	auto scene = FrontHall::createScene();
	TransitionScene* pTran = TransitionFade::create(1.0f, scene);
	Director::getInstance()->replaceScene(pTran);
}

cocos2d::Scene * GamePopup::createScene()
{
	auto scene = Scene::create();
	auto layer = GamePopup::create();
	scene->addChild(layer);
	return scene;
}

bool GamePopup::init()
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

	auto closeMenuItem = MenuItemImage::create("btn_cancel.png", "btn_cancel_click.png", CC_CALLBACK_0(GamePopup::closePopup, this));
	closeMenuItem->setPosition(size.width / 2, size.height / 3);


	auto goOutMenuItem = MenuItemImage::create("btn_go_out.png", "btn_go_out_click.png", CC_CALLBACK_0(GamePopup::selectGoOut, this));
	goOutMenuItem->setPosition(size.width / 2, size.height / 1.8);

	auto menu = Menu::create(closeMenuItem, goOutMenuItem, NULL);
	menu->setPosition(Vec2::ZERO);
	this->addChild(menu);

	return true;
}

void GamePopup::showPopup(cocos2d::Layer * _parrentLayer)
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
