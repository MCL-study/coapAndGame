#include "LoginScene.h"
#include "FrontHallScene.h"
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
#include "platform\android\jni\JniHelper.h"
#endif
USING_NS_CC;

Scene* Login::createScene()
{
	auto scene = Scene::create();
	auto layer = Login::create();
	scene->addChild(layer);
	return scene;
}

bool Login::init()
{
	if (!Layer::init())
	{
		return false;
	}

	Size visibleSize = Director::getInstance()->getVisibleSize();
	Vec2 origin = Director::getInstance()->getVisibleOrigin();

	EventDispatcher* dispatcher = Director::getInstance()->getEventDispatcher();
	auto positionListener = EventListenerTouchOneByOne::create();
	positionListener->setSwallowTouches(true);
	positionListener->onTouchBegan = CC_CALLBACK_2(Login::onTouchBegan, this);
	positionListener->onTouchEnded = CC_CALLBACK_2(Login::onTouchEnded, this);
	dispatcher->addEventListenerWithSceneGraphPriority(positionListener, this);

	auto keyListener = EventListenerKeyboard::create();
	keyListener->onKeyPressed = CC_CALLBACK_2(Login::onKeyPressed, this);
	dispatcher->addEventListenerWithSceneGraphPriority(keyListener, this);
	
	initGameScene();

	return true;
}

void Login::initGameScene(void)
{
#if CC_TARGET_PLATFORM == CC_PLATFORM_WIN32 
#pragma execution_character_set("utf-8") 
#endif
	auto _screenSize = Director::getInstance()->getWinSize();
	msgLabel = Label::createWithTTF("자동으로 로그인 합니다.\n시작하려면\n아무 곳이나 터치하세요.", "fonts/NanumPen.ttf", 80);
	msgLabel->setHorizontalAlignment(TextHAlignment::CENTER);
	msgLabel->setPosition(Vec2(_screenSize.width * 0.5f,_screenSize.height * 0.6f));
	this->addChild(msgLabel, 1);

	errorLabel = Label::createWithTTF("서버와의 연결을 실패했습니다.", "fonts/NanumPen.ttf", 80);
	errorLabel->setHorizontalAlignment(TextHAlignment::CENTER);
	errorLabel->setPosition(Vec2(_screenSize.width * 0.5f, _screenSize.height * 0.2f));
	errorLabel->setVisible(false);
	this->addChild(errorLabel, 1);

	connectingLabel = Label::createWithTTF("서버와 연결중입니다...", "fonts/NanumPen.ttf", 80);
	connectingLabel->setHorizontalAlignment(TextHAlignment::CENTER);
	connectingLabel->setPosition(Vec2(_screenSize.width * 0.5f, _screenSize.height * 0.2f));
	connectingLabel->setVisible(false);
	this->addChild(connectingLabel, 1);
}

void Login::changeScene(void)
{
	Scene* pScene = FrontHall::createScene();
	TransitionScene* pTran = TransitionFade::create(1.0f, pScene);
	Director::getInstance()->replaceScene(pTran);
}

bool Login::onTouchBegan(Touch* touch, Event* event) {
	errorLabel->setVisible(false);
	connectingLabel->setVisible(true);
	return true;
}

void Login::onTouchEnded(cocos2d::Touch * touch, cocos2d::Event * event)
{
	int id = requestID();
	UserDefault::getInstance()->setIntegerForKey("id", id);
	if (id != -1) {
		changeScene();
	}
	else {
		//	changeScene();//예외 처리 할것
		connectingLabel->setVisible(false);
		errorLabel->setVisible(true);
	}
}

void Login::onKeyPressed(EventKeyboard::KeyCode keyCode, Event * event)
{
	if (keyCode == EventKeyboard::KeyCode::KEY_BACK) {
		Director::getInstance()->end();
	}
}

int Login::requestID() {
#if CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID
	JniMethodInfo t;
	jint ret;
	log("RequestID Call");
	if (JniHelper::getStaticMethodInfo(t, "org/cocos2dx/cpp/AppActivity", "login", "()I")) {
		ret = (jint)t.env->CallStaticIntMethod(t.classID, t.methodID);
	}
	log("RequestID Call : return %d", ret);
	return ret;
#endif
	return -1;
}