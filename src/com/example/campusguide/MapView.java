package com.example.campusguide;


import java.util.ArrayList;

import org.w3c.dom.Document;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class MapView extends Activity implements OnMarkerClickListener {
	GoogleMap map;
    Directions md;
    GPSService gpscoords;
    
    LocationXmlParser xmlloc;// = new LocationXmlParser(getBaseContext());
	LatLng toPosition = new LatLng(43.685400079263206, -80.537133384495975);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_view);
		
		final AutoCompleteTextView actv;
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
		//LocationXmlParser 
		xmlloc = new LocationXmlParser(getBaseContext());
		
		String[] buildings = new String[xmlloc.getSize()];
		
		for (int i = 0 ; i < xmlloc.getSize() ; i++) {
			buildings[i] = xmlloc.getNameOf(i);
		}
		
		//String[] buildings = getResources().getStringArray(R.array.list_of_buildings);
		ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, buildings);

		actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		actv.setAdapter(adapter);
		
		actv.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// Perform action on click   
            	AutoCompleteTextView searchtext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            	String str = searchtext.getEditableText().toString();
        		if ( str == null || str.isEmpty())
        		{
        			return false;
        		}
            	doMySearch(str);
            	
            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
            	imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0); 
            	return true;
			}
		});
		
		actv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Perform action on click   
            	AutoCompleteTextView searchtext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            	String str = searchtext.getEditableText().toString();
        		if ( str == null || str.isEmpty())
        		{
        			return;
        		}
            	doMySearch(str);
            	
            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
            	imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
				
			}
		});
		
		actv.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// Perform action on click   
            	AutoCompleteTextView searchtext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            	String str = searchtext.getEditableText().toString();
        		if ( str == null || str.isEmpty())
        		{
        			return;
        		}
            	doMySearch(str);
            	
            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
            	imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		final Button search;
		search = (Button) findViewById(R.id.btnSearch);
		
		search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click   
            	AutoCompleteTextView searchtext = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            	String str = searchtext.getEditableText().toString();
        		if ( str == null || str.isEmpty())
        		{
        			return;
        		}
            	doMySearch(str);
            	
            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
            	imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0); 

            }
        });
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setOnMarkerClickListener(this);
		
		gpscoords = new GPSService(getBaseContext());
		md = new Directions();
		
		ArrayList<Integer> tourArr = getIntent().getIntegerArrayListExtra("tour_list");
		
		if (tourArr != null) {
			setupTourWaypoints(tourArr);
		} else {
			LatLng fromPosition = new LatLng(gpscoords.getLatitude(), gpscoords.getLongitude());	
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 16));
			map.addMarker(new MarkerOptions().position(fromPosition).title("You Are Here!"));
		}
		
		map.setMyLocationEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public boolean onMarkerClick(final Marker marker) {
		String info = "No Information Available.";
		String pic = "lib1";
		
		for (int i = 0 ; i < xmlloc.getSize() ; i++) {
			if (marker.getTitle().equals(xmlloc.getNameOf(i))) {
				info = xmlloc.getDescriptionOf(i);
				//info = "INFO INFO INFOINFO KHFSDKLS S F K jfldss skjslfINFO INFOINFO KHFSDKLS S F K jfldss skjslfINFO INFOINFO KHFSDKLS S F K jfldss skjslfFO INFO INFOINFO KHFSDKLS S F K jfldss skjslfINFO INFOINFO KHFSDKLS S F K jfldss skjslfINFO INFOINFO KHFSDKLS S F K jfldss FO INFO INFOINFO KHFSDKLS S F K jfldss skjslfINFO INFOINFO KHFSDKLS S F K jfldss skjslfINFO INFO";
				//pic = xmlloc.getPic(i);
			}
		}
		
		
		CustomDialog cdd=new CustomDialog(this, marker, info);
		cdd.show(); 
		
		int drawableResourceId = this.getResources().getIdentifier("lib1", "drawable", this.getPackageName());
		//int drawableResourceId = this.getResources().getIdentifier(pic, "drawable", this.getPackageName());
		
		Drawable markerBackground = getResources().getDrawable( drawableResourceId );
		markerBackground.setAlpha(80);
		cdd.getWindow().setBackgroundDrawable(markerBackground);
        return false;
    }
	
	void doMySearch(String query) {
		if ( query == null || query.isEmpty())
		{
			return;
		}
		
		double toLat = 0;
		double toLong = 0;
		String title = "";
		
		for (int i = 0 ; i < xmlloc.getSize() ; i++) {
			if (query.equals(xmlloc.getNameOf(i))) {
				title = xmlloc.getNameOf(i);
				toLat = xmlloc.getLatitudeOf(i);
				toLong = xmlloc.getLongitudeOf(i);
			}
		}
		
		LatLng fromPosition = new LatLng(gpscoords.getLatitude(), gpscoords.getLongitude());
		
		Document doc;
		
		if (toLat == 0) {
			doc = md.getDocument(fromPosition, query, Directions.MODE_WALKING);
		} else {
			LatLng toPosition = new LatLng(toLat, toLong);
			doc = md.getDocument(fromPosition, toPosition, Directions.MODE_WALKING);
		}

		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
		
		for(int i = 0 ; i < directionPoint.size() ; i++) {			
			rectLine.add(directionPoint.get(i));
			
			if (i == directionPoint.size()-1) {
				toPosition = directionPoint.get(i);
			}
		}
		
		map.clear();
		
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 16));		
		map.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
		map.addMarker(new MarkerOptions().position(toPosition).title(title));
		
		map.addPolyline(rectLine);
		
	}

	void setupTourWaypoints(ArrayList<Integer> arr) {
		double toLat = 0;
		double toLong = 0;
		String title;
		String description;
		
		map.clear();
		
		LatLng fromPosition = new LatLng(gpscoords.getLatitude(), gpscoords.getLongitude());
		map.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
		//map.animateCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 16));
		
		
		ArrayList<LatLng> waypoints = new ArrayList<LatLng>();
		
		for (Integer s : arr) {
//			for (int i = 0 ; i < xmlloc.getSize() ; i++) {
//				if (s.equals(xmlloc.getNameOf(i))) {
//					toLat = xmlloc.getLatitudeOf(i);
//					toLong = xmlloc.getLongitudeOf(i);
//				}
//			}
			title = xmlloc.getNameOf(s);
			toLat = xmlloc.getLatitudeOf(s);
			toLong = xmlloc.getLongitudeOf(s);
			
			LatLng toPosition = new LatLng(toLat, toLong);
			waypoints.add(toPosition);
			map.addMarker(new MarkerOptions().position(toPosition).title(title));
		}
		
		Document doc;
		doc = md.getDocument(fromPosition, fromPosition, waypoints, Directions.MODE_WALKING);
		
		
		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
		
//		LatLng firstpoint = directionPoint.get(1);
//		float bearing = (float) Math.toDegrees(Math.atan((firstpoint.latitude - fromPosition.latitude)/(firstpoint.longitude - fromPosition.longitude)));
		
		for(int i = 0 ; i < directionPoint.size() ; i++) {			
			rectLine.add(directionPoint.get(i));
		}
		map.addPolyline(rectLine);
		
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(fromPosition)      // Sets the center of the map to the start
	    .zoom(17)                   // Sets the zoom
	    //.bearing(bearing)                // Sets the orientation of the camera to east
	    //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
	    .build();                   // Creates a CameraPosition from the builder
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		
//		for (String s : arr) {
//			for (int i = 0 ; i < xmlloc.getSize() ; i++) {
//				if (s.equals(xmlloc.getNameOf(i))) {
//					toLat = xmlloc.getLatitudeOf(i);
//					toLong = xmlloc.getLongitudeOf(i);
//				}
//			}
//			
//			Document doc;
//			
//			if (toLat == 0) {
//				doc = md.getDocument(fromPosition, s, Directions.MODE_WALKING);
//			} else {
//				LatLng toPosition = new LatLng(toLat, toLong);
//				doc = md.getDocument(fromPosition, toPosition, Directions.MODE_WALKING);
//			}
//	
//			ArrayList<LatLng> directionPoint = md.getDirection(doc);
//			PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);
//			
//			for(int i = 0 ; i < directionPoint.size() ; i++) {			
//				rectLine.add(directionPoint.get(i));
//				
//				if (i == directionPoint.size()-1) {
//					toPosition = directionPoint.get(i);
//				}
//			}
//			
//			map.addMarker(new MarkerOptions().position(toPosition).title(s.toString()));
//			
//			map.addPolyline(rectLine);
//			fromPosition = toPosition;
//		}
	}
}
