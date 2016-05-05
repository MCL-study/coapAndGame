#ifndef __CUSTROM_UTIL_H_
#define __CUSTROM_UTIL_H_
#include <sstream>
using namespace std;
class CustomUtil {
public:
	static string toString(int value) {
		ostringstream convStream;
		convStream << value;
		return convStream.str();
	}

	static string toString(float value) {
		ostringstream convStream;
		convStream << value;
		return convStream.str();
	}
};

#endif