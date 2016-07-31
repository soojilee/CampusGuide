package com.example.campusguide;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class LocationXmlParser {
	private ArrayList<LocationEntry> locations;
	
	LocationXmlParser(Context context) {
		XmlPullParserFactory parserFactory;
        try{
        	parserFactory = XmlPullParserFactory.newInstance();
        	XmlPullParser parser = parserFactory.newPullParser();

        	InputStream in = context.getAssets().open("locations.xml");

        	parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        	parser.setInput(in, null);

        	locations = parseXML(parser);
        } catch(XmlPullParserException e) {
        	e.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getSize() {
		return locations.size();
	}
	
	public String getNameOf(int i) {
		return locations.get(i).getName();
	}
	
	public int getIdOf(int i) {
		return locations.get(i).getId();
	}
	
	public double getLatitudeOf(int i) {
		return locations.get(i).getLatitude();
	}
	
	public double getLongitudeOf(int i) {
		return locations.get(i).getLongitude();
	}
	
	public String getDescriptionOf(int i) {
		return locations.get(i).getDescription();
	}
	
	public ArrayList<LocationEntry> getArray(){
		return locations;
	}
	

	private ArrayList<LocationEntry> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		ArrayList<LocationEntry> entry = null;
        int eventType = parser.getEventType();
        LocationEntry currentEntry = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	entry = new ArrayList<LocationEntry>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("entry")){
                        currentEntry = new LocationEntry();
                    } else if (currentEntry != null){
                        if (name.equals("id")){
                            currentEntry.setId(Integer.parseInt(parser.nextText()));
                        } else if (name.equals("name")){
                        	currentEntry.setName(parser.nextText());
                        } else if (name.equals("latitude")){
                            currentEntry.setLatitude(Double.parseDouble(parser.nextText()));
                        } else if (name.equals("longitude")){
                            currentEntry.setLongitude(Double.parseDouble(parser.nextText()));
                        } else if (name.equals("description")){
                            currentEntry.setDescription(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("entry") && currentEntry != null){
                    	entry.add(currentEntry);
                    } 
            }
            eventType = parser.next();
        }
        return entry;
	}
}