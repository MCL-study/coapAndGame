#ifndef __LOC_DATA_H_
#define __LOC_DATA_H_

class LocData {
private:
	 double lat, lng;
public:
	LocData():lat(0),lng(0) {
		
	}
	LocData(const double _lat,const double _lng):lat(_lat),lng(_lng) {}

	double getLat() const {
		return lat;
	}

	double getLng() const {
		return lng;
	}
	
	LocData operator-(LocData &ref) {
		return LocData(lat - ref.lat, lng - ref.lng);
	}

	static float computeDistance(double lat1, double lon1,double lat2, double lon2);
	static float computeDistanceLat(double lat1, double lat2);
	static float computeDistanceLng(double lon1, double lon2);

};
#endif