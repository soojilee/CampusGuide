package com.example.campusguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


public class HomeActivity extends Activity implements SensorEventListener, OnClickListener {

	
    private Camera mCamera;
    private CameraPreview mPreview;
//    private double viewAngle;

    
    private Location location;
    private LocationManager lm;
    private SensorManager mSensorManager;
    private Sensor magnet;
    private Sensor gravity;
    
    private Calculations calc = new Calculations();
    private LocationXmlParser parser;
    private ArrayList<LocationEntry> data;
    
    private List<float[]> mRotHist = new ArrayList<float[]>();
    private int mRotHistIndex;
    // Change the value so that the azimuth is stable and fit your requirement
    private int mHistoryMaxLength = 40;
    float[] mGravity;
    float[] mMagnetic;
    float[] mRotationMatrix = new float[9];
    // the direction of the back camera, only valid if the device is tilted up by
    // at least 25 degrees.
    private float mFacing = Float.NaN;
    
    public static final float TWENTY_FIVE_DEGREE_IN_RADIAN = 0.436332313f;
    public static final float ONE_FIFTY_FIVE_DEGREE_IN_RADIAN = 2.7052603f;
	private static final String TAG = null;
	private static final boolean DEBUG = false;
	
	private String locationTitle= "";
	private String locationDescription = "";
	
	HashMap<LocationEntry,Button> bHash = new HashMap<LocationEntry, Button>();
	Button theButton;
    
//    private LocationEntry sampleloc = new LocationEntry();

      
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //get sensor service
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gravity =mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        magnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    
        // Create an instance of Camera
        if (checkCameraHardware(this)){
            mCamera = getCameraInstance();
            mCamera.setDisplayOrientation(90);
        
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        
            preview.addView(mPreview);
        
        }else{
            System.out.println("hardware unavail");
        }
    
        //get current location
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     // Register the listener with the Location Manager to receive location updates
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
     // Register the listener with the Location Manager to receive location updates
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //get data of building locations
        parser = new LocationXmlParser(this);
        data = parser.getArray();
        
        theButton = new Button(this);
        
		theButton.setText("tb");
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		theButton.setLayoutParams(rlp);
		theButton.setOnClickListener(this);
		
		ViewGroup container = (ViewGroup)findViewById(R.id.buttonsContainer);
		container.addView(theButton);
		
		Button tourText =(Button)findViewById(R.id.tour);
    	
		tourText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	Intent intent = new Intent(getApplicationContext(), TourCreateActivity.class);
		    	HomeActivity.this.startActivity(intent);
			}
		});
		
		mSensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_FASTEST);
		find();
    }
    
//    @Override
//    protected void onStart(){
//    	super.onStart();
//        //find button
//        ;
////        sampleloc.setName("MC");
//        double dist = calc.distance(43.472161, -80.543926, 43.47383287, -80.52901793);
//        
////        sampleloc.setDist(dist);
////        sampleloc.setLatitude(43.472161);
////        sampleloc.setLongitude(-80.543926);
//        System.out.println("dist is: " + dist);
//        //display building names;
////        dynamicdisplay(location);
////        sylverdisplay();
//    }
//    
//    @Override
//    protected void onResume(){
//        super.onResume();
//        mSensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_FASTEST);
//        mSensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_FASTEST);
//     
//    }
//    
//    @Override
//    protected void onPause(){
//	    super.onPause();
//	    mSensorManager.unregisterListener(this);
//    }
//
    @Override
    protected void onDestroy(){
        super.onDestroy();
		if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }
    
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        	
          // Called when a new location is found by the network location provider.
//         dynamicdisplay(location);
        	sylverdisplay();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
      };

    
    
    @Override
    public void onSensorChanged(SensorEvent event)
    {	
    	
    //	TextView locText = ((TextView)findViewById(R.id.locText));
    	
    	
    	
//    	locText.setText("Genius "+
//    			location.getLatitude()  + " "+location.getLongitude()
//    	        );
    	
    	ImageView locText =(ImageView)findViewById(R.id.locText);
    	
    	locText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String uri = String.format(Locale.ENGLISH, "geo:%f,%f", location.getLatitude(), location.getLongitude());
		    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		    	HomeActivity.this.startActivity(intent);
			}
		});
    	
    			
    	
        if (event.sensor == gravity)
        {
            mGravity = event.values.clone();
        }
        else
        {
            mMagnetic = event.values.clone();

        }
        
        if (mGravity != null && mMagnetic != null)
        {
            if (SensorManager.getRotationMatrix(mRotationMatrix, null, mGravity, mMagnetic))
            {
            	
            	
                // inclination is the degree of tilt by the device independent of orientation (portrait or landscape)
                // if less than 25 or more than 155 degrees the device is considered lying flat
                float inclination = (float) Math.acos(mRotationMatrix[8]);
                if (inclination < TWENTY_FIVE_DEGREE_IN_RADIAN
                    || inclination > ONE_FIFTY_FIVE_DEGREE_IN_RADIAN)
                {
                    // mFacing is undefined, so we need to clear the history
                    clearRotHist();
                    mFacing = Float.NaN;
                }
                else
                {
                    setRotHist();
                    // mFacing = azimuth is in radian
                    mFacing = findFacing();
//                    dynamicdisplay(location);
                    sylverdisplay();
                  //  System.out.println(mFacing);
                }
            }
        }
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//nothing
    }
    
    private void clearRotHist()
    {
        if (DEBUG) {Log.d(TAG, "clearRotHist()");}
        mRotHist.clear();
        mRotHistIndex = 0;
    }
    
    private void setRotHist()
    {
        if (DEBUG) {Log.d(TAG, "setRotHist()");}
        float[] hist = mRotationMatrix.clone();
        if (mRotHist.size() == mHistoryMaxLength)
        {
            mRotHist.remove(mRotHistIndex);
        }
        mRotHist.add(mRotHistIndex++, hist);
        mRotHistIndex %= mHistoryMaxLength;
    }
    
    private float findFacing()
    {
        if (DEBUG) {Log.d(TAG, "findFacing()");}
        float[] averageRotHist = average(mRotHist);
        return (float) Math.atan2(-averageRotHist[2], -averageRotHist[5]);
    }
    
    public float[] average(List<float[]> values)
    {
        float[] result = new float[9];
        for (float[] value : values)
        {
            for (int i = 0; i < 9; i++)
            {
                result[i] += value[i];
            }
        }
        
        for (int i = 0; i < 9; i++)
        {
            result[i] = result[i] / values.size();
        }
        
        return result;
    }
    
    private void find(){
        //find button
        Button findButton = (Button)findViewById(R.id.find);
        findButton.setOnClickListener(new OnClickListener() {
            
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MapView.class);
				startActivity(i);
			}
        	
        });
    }
   
    
    private void sylverdisplay(){
    	if (location == null) {
			System.out.println("no location");
			return;
		}
    	
    	ViewGroup container = (ViewGroup)findViewById(R.id.buttonsContainer);
		
		
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		
		class BldgAndTheta{
			LocationEntry bldg;
			float theta;
			float distance;
		}
		
		ArrayList<BldgAndTheta> bnta = new ArrayList<BldgAndTheta>(); 
		
		for(LocationEntry bldgLoc : data){
			double y = calc.distance(bldgLoc.getLatitude(), 0, latitude, 0);
	    	double x = calc.distance(0, bldgLoc.getLongitude(), 0, longitude);
	    	double bld;
	    	
	    	if (bldgLoc.getLatitude() < latitude){
	    		y = -y;
	    	}
	    	if (bldgLoc.getLongitude() < longitude){
	    		x = -x;
	    	}
	    	
	    	bld = Math.PI - (Math.atan2(y,x)+Math.toRadians(60));
	    	
			double theta = (mFacing - bld) % (Math.PI*2);
			
			
			
//			if (bldgLoc.getName().startsWith("DC")){
//				findViewById(R.id.dcdirectioneer).setRotation((float)Math.toDegrees(bld));
//			}else if (bldgLoc.getName().startsWith("STJ")){
//				findViewById(R.id.stjdirectioneer).setRotation((float)Math.toDegrees(bld));
//			}else if (bldgLoc.getName().startsWith("LIB")){
//				findViewById(R.id.libdirectioneer).setRotation((float)Math.toDegrees(bld));
//			}else if (bldgLoc.getName().startsWith("E2")){
//				findViewById(R.id.e2directioneer).setRotation((float)Math.toDegrees(bld));
//			}else if (bldgLoc.getName().startsWith("V1")){
//				findViewById(R.id.v1directioneer).setRotation((float)Math.toDegrees(bld));
//			}
//			findViewById(R.id.directioneer).setRotation((float) Math.toDegrees(mFacing));
			
			if(Math.abs(theta) < 0.3){
				
				BldgAndTheta bnt = new BldgAndTheta();
				bnt.bldg = bldgLoc;
				bnt.theta = (float) theta;
				bnta.add(bnt);
				
				bnt.distance = (float) calc.distance(bldgLoc.getLatitude(), bldgLoc.getLongitude(), latitude, longitude);
			}

		}

		
		BldgAndTheta theBntToDisplay ;
		if(bnta.size() != 0){
			theBntToDisplay = bnta.get(0);
			
			for(BldgAndTheta bnt : bnta){
				
				if (bnt.distance < theBntToDisplay.distance){
					theBntToDisplay = bnt;
				}
			}

				Button b = theButton;
	//		if (Math.abs(theTheta) < 0.3){
				b.setAlpha(0.9f);
				b.setTranslationX((float) -(theBntToDisplay.theta / 0.3) *100);
				b.setText(theBntToDisplay.bldg.getName());
				
//				parser.getInfo(); 
				//locationTitle = theBntToDisplay.bldg.getName();
				//locationDescription = theBntToDisplay.bldg.getName() + " is a nice building.";
				locationTitle = theBntToDisplay.bldg.getName(); 
				if(locationTitle == "DC - William G. Davis Computer Research Centre"){ 
					locationDescription = "The DC, home to Waterloo's Software Engineering program, contains 2 lecture halls, each with seats for 250 people, a food court, and a library that supports Engineering, Mathematics, Science, and Computer Science."; 
				} else if(locationTitle == "EIT - Centre for Environmental and Information Technology"){ 
					locationDescription = "In CEIT, teams of experts from 4 of Waterloo's 6 faculties – Engineering, Environment, Mathematics, and Science– get together to solve complex environmental problems and expand the frontiers of information technology."; 
				} else if(locationTitle == "MC - Mathematics & Computer Building (Computer Help & Information Place)"){ 
					locationDescription = " MC houses classrooms, computer labs, the Math Society offices, the Mathematics Undergraduate Office, several tutorial centers, a student-run food services outlet and the Comfy Lounge."; 
				}

			
		}else{
			Button b = theButton;
			b.setAlpha(0.2f);
			System.out.println("BNT IS NULL"); // This shouldn't print really...
		}   	
    	
    }
    
    /** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
    /** A safe way to get an instance of the Camera object. */
	private static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	        
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    	System.out.println("Camera not avail.");
	    }
	    return c; // returns null if camera is unavailable
	}

	@Override
	public void onClick(View v) {

				// TODO Auto-generated method stub
				final Dialog dialog1 = new Dialog(HomeActivity.this);
	            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
	            dialog1.setContentView(R.layout.custom_alert);
	            
	            ((TextView)dialog1.findViewById(R.id.custom_alert_title)).setText(locationTitle);
	            ((TextView)dialog1.findViewById(R.id.custom_alert_text)).setText(locationDescription);
	            Button close = (Button) dialog1.findViewById(R.id.but_close);

	            close.setOnClickListener(new OnClickListener()
	            {
	                @Override
	                public void onClick(View v) 
	                {
	                        dialog1.dismiss();  

	                }
	            });
	            dialog1.show(); 
	            
			}
		
}
