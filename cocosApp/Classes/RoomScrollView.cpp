#include "RoomScrollView.h"
#include "HallPopupScene.h"
USING_NS_CC;
USING_NS_CC::ui;

RoomScrollView::RoomScrollView(Layer* layer,Size size)
{
	log("RoomScrollView call");
	view = ui::ScrollView::create();
	view->setContentSize(size);
	layer->addChild(view,1);
	superView = layer;
}

void RoomScrollView::updateRoomConfigs(std::list<RoomConfig*>*  roomList)
{
	log("updateRoomConfigs call");
	if (roomList->size() != 0) {
		Button* btn = createRoomButton(roomList->front());
		Vec2 size = btn->getContentSize();
		view->setInnerContainerSize(Size(size.x * 2, size.y * ceil(roomList->size()/2+1)));
		while (buttonList.size() != 0) {
			Button* btn = buttonList.front();
			view->removeChild(btn);
			buttonList.pop_front();
		}
		for (int i = roomList->size(); i >0; i--) {
			Button* btn = createRoomButton(roomList->front());
			btn->setAnchorPoint(Vec2(0.0, 0.0));
			view->addChild(btn);
			Vec2 size = btn->getContentSize();
			btn->setPosition(Vec2((size.x)*(1-i%2), (size.y)* floor(i/2)));
			buttonList.push_back(btn);
			roomList->pop_front();
		}
	}
}

ui::Button* RoomScrollView::createRoomButton(const RoomConfig * roomConfig)
{
	log("createRoomButton call");
	ui::Button* btn = ui::Button::create("room_panel.png", "room_panel_click.png");
	Vec2 size = btn->getContentSize();
	String str;
	int roomid = roomConfig->getRoomID();
	str.appendWithFormat("%d\n%dkm\n?/%d", roomid, roomConfig->getScale(), roomConfig->getMaxGameMember());
	auto label = Label::createWithTTF(str._string, "fonts/NanumPen.ttf", 46);
	label->setColor(Color3B(0, 0, 0));
	label->setPosition(Vec2(size.x * 0.43f, size.y * 0.48f));
	btn->addChild(label);
	btn->addClickEventListener(CC_CALLBACK_1(RoomScrollView::onBtnClickListener,this, roomid));
	return btn;
}

void RoomScrollView::onBtnClickListener(cocos2d::Ref * pSender, int roomid)
{
	UserDefault::getInstance()->setIntegerForKey("roomid", roomid);
	HallPopup* popup = HallPopup::create();
	popup->showPopup(superView);
}
