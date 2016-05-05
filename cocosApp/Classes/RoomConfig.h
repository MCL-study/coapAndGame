#ifndef __ROOM_CONFIG_H_
#define __ROOM_CONFIG_H_
#include "LocData.h"
class RoomConfig {
private:
	int roomID;
	LocData centerLoc;
	int maxGameMember;
	int scale;
public:
	RoomConfig(int roomID, LocData centerLoc, int maxGameMember, int scale) {
		this->roomID = roomID;
		this->centerLoc = centerLoc;
		this->maxGameMember = maxGameMember;
		this->scale = scale;
	}

	RoomConfig(int roomID,int maxGameMember, int scale) {
		this->roomID = roomID;
		this->maxGameMember = maxGameMember;
		this->scale = scale;
	}

	RoomConfig(LocData centerLoc, int maxGameMember, int scale) {
		roomID = -1;
		this->centerLoc = centerLoc;
		this->maxGameMember = maxGameMember;
		this->scale = scale;
	}
	int getRoomID() const {
		return roomID;
	}
	LocData getCenterLoc() {
		return centerLoc;
	}

	int getMaxGameMember() const{
		return maxGameMember;
	}

	int getScale() const {
		return scale;
	}
};

#endif