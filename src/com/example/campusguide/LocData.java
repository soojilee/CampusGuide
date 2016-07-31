package com.example.campusguide;

public class LocData {
	String name;
	double latitude;
	double longitude;
	
	public LocData(String building, double lat, double lng){
		name = building;
		latitude = lat;
		longitude = lng;
	}
	
	public String getname(){
		return name;
	}
	
	public double getlat(){
		return latitude;
	}
	
	public double getlng(){
		return longitude;
	}
	
}
