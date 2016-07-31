package com.example.campusguide;



public class Calculations{
	
	//Helper functions

	//distance component calculator
	public double distcomp(double a, double b){
		double dlat = Math.abs(Math.toRadians(a-b));
		double dist = Math.sin(dlat/2)*2;
		return dist;
	}

	//calculate distance
	public double distance(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371; //kilometers
	    double dLat = Math.abs(Math.toRadians(lat2-lat1));
	    double dLng = Math.abs(Math.toRadians(lng2-lng1));
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }
//		public double distance(double lat1, double lon1, double lat2, double lon2) {
////		    double a = distcomp(lat1, lat2);
////		    double b = distcomp(lon1, lon2);
////		    double dist = Math.sqrt(Math.pow(a,2) + Math.pow(b,2));
////		    return dist;
////			
//			double theta = lon1 - lon2;
//		      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//		      dist = Math.acos(dist);
//		      dist = rad2deg(dist);
//		      dist = dist * 60 * 1.1515;
//		       return (dist);
//		}
//
//		private double deg2rad(double deg) {
//			return (deg * Math.PI / 180.0);
//		}
//		private double rad2deg(double rad) {
//			return (rad * 180.0 / Math.PI);
//		}
//		   
		
		


		
	
}
