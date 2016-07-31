package com.example.campusguide;

public class LocationEntry {
	private int id;
	private String name;
	private double latitude;
	private double longitude;
	private String description;


	
	public void setId(int id){
		this.id = id;
	}
	
	public void setName(String s){
		name = s;
	}
	
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}
	public void setDescription(String d){
		description = d;
	}
	

	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public String getDescription(){
		return description;
	}
	

}
