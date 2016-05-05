#ifndef __USER_DATA_H_
#define __USER_DATA_H_

#include"LocData.h"

class UserData {
protected:
	const int id;
	const int userProperties;
	LocData locData;
public:
	~UserData() {
	}
	UserData(int _id, int _userProperties, LocData location):id(_id), userProperties(_userProperties) {
		this->locData = location;
	}
	UserData(int _id, int _userProperties) :id(_id), userProperties(_userProperties) {
		locData = LocData(0, 0);
	}


	static int getSize(){
		return 4 + 4 + 16;
	}

	int getId() const{
		return id;
	}

	void setLocData(LocData locData) {
		(this->locData) = locData;
	}

	LocData getLocData() {
		return locData;
	}

	int getUserProperties() const {
		return userProperties;
	}
};

#endif
