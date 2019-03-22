package com.mooney.diveapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapController;

/*
 * Sept ISSUE IS GETCURRENTLOCATION() DOES EXACYTLY THAT
 * GETS CURRENT OCATION OF USER DEVOCE THEN SENDS THIS BACK TO SERLIZED OBJECT FOR SAVING
 * MUST FOND THE LAT LONG OF WHERE PIN IS DROPPED IN CODE AND THEN SAVE THESE CORDS TO 
 * OBJECT, PROB NEAR WHERE MARKER DROPPED AND GEOCODING METHOD TO GET STRING ADDRESS OF MARKER
 * 
 * SEE ON MAP CLCK AND GET ADDRESS FORM ON MARKER CLICK METHODS
 * 
 */

/*
 * 
 * Orfginal code see ref to documentation https://developers.google.com/maps/documentation/android/
 * 
 * 14 Spet 2014
 * We want to store every loaction as a lat plus longtitude double into an array
 * and saved via sharde prefernce that is availble to entre map
 * Wneh Save bitton is clicked we will get the geo points and save
 * In onCrate we want to check if any data in this array, if so 
 * we will plot the ponts via this tutprial here http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
 * MarkerOptions marker = new MarkerOptions().position(new LatLng(lattitude, longtitude)).title("Previous Dive");
 * googleMap googleMap.addMarker(marker);
 * 
 */

public class MapDiveLocation<saveDiveLocationsObject> extends FragmentActivity implements
		OnMarkerDragListener, OnMapClickListener {

	private GoogleMap mMap;

	UiSettings settings;
	//MapController mc;
	//GeoPoint geopoint;
	RectF oval;
	Canvas canvas;
	Button getLocationButton;
	int mRadius = 5;
	SharedPreferences prefs;
	String prefFilename = "MyPref";
	double savedLatitude;
	double savedLongtitude;
	EditText myLocation;
	boolean markerMovedLocation;
	private ProgressBar mActivityIndicator;

	//check for connectivity
	boolean isGPSEnabled=false;
	boolean isNetWorkEnabled=false;
	boolean canGetConnection=false;
	boolean isInterNetConnected=false;
	
	private LatLng touchLatLng;

	private MarkerOptions markerOptions;
	static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);
	static final LatLng DUBLIN = new LatLng(56, 6);
	static final LatLng PERTH = new LatLng(-31.90, 115.86);
	
	// adding and saving froming dive locations, saved in on Map click
	private double saveLastDiveLatitude;
	private double saveLastDiveLongtitude;
	private MarkerOptions lastDiveMarker;
	public static final String PREFS_NAME="SAVED DIVE CORDINATES";
	SharedPreferences savedDivesCo_ords_SharedPrefsFile;
	private int numberOfDiveOcations;
	private String longLatPrefKeyValue="";
	private String diveLocationKeyValue;

	private String TAG="MAP";
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		//check for internet connectivty
		isInterNetConnected=checkInternetConnection();
		if(!isInterNetConnected){
			alertDialog();
		}else if(isInterNetConnected){
			Log.d("Google maps Class", "Internet connection established");
		}
		
		
		try{
		setUpMap();
		mMap.setOnMapClickListener(this);
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Your device may not be configured to use\n google maps..\ncheck settings and Try again later.", Toast.LENGTH_SHORT).show();
		}
		markerMovedLocation = false;// set true if moved and save this address
		myLocation = (EditText) this.findViewById(R.id.et_locationOnMap);
		
		this.mActivityIndicator = (ProgressBar) this
				.findViewById(R.id.address_progress);
		getLocationButton = (Button) this.findViewById(R.id.button_getLocation);

		getLocationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				// get current location and send bundle data back to
				// LogDive.class
				
				if (markerMovedLocation) {
					
					// save address from marker and add to Sahrder Prefs array 
					// and send teh string location nback to former actovity
					
					sendDataBack();

				}
				// get address from current location if marker not moved
				getCurrentLocation();// calls inner asynch class to retrun
										// address of user device
				
				
				// Show the activity indicator
				mActivityIndicator.setVisibility(View.VISIBLE);
				
				// this is hidden when post Execyte is called form inner asyn
				// class
				
				
				
				
				
			}// end
		});// end find current location

		
		try{
			
		// basix map settings
			
		mMap.setOnMarkerDragListener(this);
		mMap.setBuildingsEnabled(true);
		//getting googple play servovces unavailnle error here when this method is called
		//
		mMap.setMyLocationEnabled(true);
	
		
		// show icon that when clicked redirects to current position
		mMap.getUiSettings().setMyLocationButtonEnabled(true);

		// zoom to current location using Location Manager if GPS/Wi-Fi avaailble
		zoomToCurrentLocation();
		}catch(Exception exc){
			exc.printStackTrace();
			Toast.makeText(getApplicationContext(), "Your device may not be configured to use\n google maps..\ncheck settings and Try again later.", Toast.LENGTH_SHORT).show();
		}
		
		
		// test the object write to file mehod, for debug only
		//this.saveLatAndLongCordinateToArray(53.344, -6.26);
		//this.saveLatAndLongCordinateToArray(40.344, -8.26);
		//addMarkersForPreviousDives();
		
		
	}// end onCreate
	
	





	private void alertDialog() {
		// called when nointernet connection isInterNetConnected=false
		
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle("Internet Check");
		alert.setMessage("No internet connection found!! Quit and try again later...");
		alert.setButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// quit this activity and return to logdve to avoid null pointer http exceptions
				finish();
				
			}
		});
		
		alert.show();
	}//end alertDialog



	private boolean checkInternetConnection() {
		// if no connection catch exceptions use a alert dialog to get user to force quit actovoty
		
		 ConnectivityManager connectivity = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
         if (connectivity != null) 
         {
             NetworkInfo[] info = connectivity.getAllNetworkInfo();
             if (info != null) 
                 for (int i = 0; i < info.length; i++) 
                     if (info[i].getState() == NetworkInfo.State.CONNECTED)
                     {
                         return true;
                     }

         }
         return false;
   
		
	}//end checkInternetConnection

	
	
	
	
	private void zoomToCurrentLocation() {
		
		// called from OnCreate
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		
		//check fro connectovoty
		
		isGPSEnabled =locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetWorkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(!isGPSEnabled && !isNetWorkEnabled){
			//no network provoder avaible
			myLocation.setText("No Network! Enter location mannually..");
			Toast.makeText(getBaseContext(), "No Network! Enter location mannually..", Toast.LENGTH_LONG);
			
		}else{
			Toast.makeText(getBaseContext(), "GPS/Wi Fi Enabled!", Toast.LENGTH_SHORT);
		Location location1 = locationManager
				.getLastKnownLocation(locationManager.getBestProvider(criteria,
						false));
		if (location1 != null) {

			Toast.makeText(getBaseContext(), "Zooming to your dive site baby!",
					Toast.LENGTH_SHORT).show();
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location1.getLatitude(), location1.getLongitude()), 13));

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(location1.getLatitude(), location1
							.getLongitude())) // Sets the center of the map to
												// location user
					.zoom(8) // Sets the zoom, 2 max zoom out and 21 max zoom
								// in
					.bearing(0) // Sets the orientation of the camera to east
					.tilt(90) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder

			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			mMap.addMarker(new MarkerOptions()
					.draggable(true)
					.position(
							new LatLng(location1.getLatitude(), location1
									.getLongitude()))
					.title("Dive Site!")
					.visible(true)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pushpin4)));
			
			// get long and lat cords and set as global vars
			Log.d("MAPS: ZOOM TO CURRENT LOCATION:LAT AND LONG CORDS:", String.format("Lat %f Long: %f",location1.getLatitude(), location1.getLongitude()) );
			
			
			
			//saveLatAndLongCordinateToArray(location1.getLatitude(), location1.getLongitude());

			// update edittext by resever geoding these lat/lon cordinates
			//getAddress(location1.getLatitude(), location1.getLongitude());
			//new getAddressFromMarkerAsynch(this).execute(markerDragged);
			(new GetAddressTask(this)).execute(location1);
			

		} else {
			Toast.makeText(
					getBaseContext(),
					"Cannot find current location!\nEnable GPS/Wi-Fo or manually \n enter your location",
					Toast.LENGTH_LONG).show();
			myLocation.setText("No GPS! Enter manually or refresh page..");
		}//end inner if else
		
		}//end outer if else
	}// end zoomToCurrentLocation

	
	
	
	
	
	private void saveLatAndLongCordinateToArray(double latitude,
			double longitude) {
		
		
		/*
		 * This is called from getCurrentLocation() just before Send data back is called
		 * 
		 * Save co odrnites to locationObject which
		 * is then written to Phone directory
		 * First get the orginal object from file
		 * holding previous coords, if not there it will return a null object
		 * 
		 */
		
savedDiveLocationsObject locationObject = new savedDiveLocationsObject(this);
		

		try{
			
			locationObject = (savedDiveLocationsObject)locationObject.getSavedLocationsObjectFromPhone();
			
			// if not null we must set the context as object not cretaed with contructor
			if(locationObject!=null){
			locationObject.setObjectContext(this);
			}
			
			
			
			if(locationObject==null){
				
				// if no obect saved 
				locationObject = new savedDiveLocationsObject(this);
			}
			//ArrayList latitudesArray = locationObject.getLatitudeArray();
			//ArrayList longtitideArray = locationObject.getLongtitudeArray();
		//savedDiveLocationsObject locationObject = new savedDiveLocationsObject (this);
		locationObject.addLatitude(latitude);
		locationObject.addLongigtude(longitude);
		
		
SharedPreferences.Editor editor;
		
		savedDivesCo_ords_SharedPrefsFile = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = savedDivesCo_ords_SharedPrefsFile.edit();
		int noOfDivesSavedLocations =savedDivesCo_ords_SharedPrefsFile.getInt("numberOfDiveOcations", 0);
		
		//Log.d("MAP 339", "SAVED LOACTIOS TO OBJECT LAT: "+locationObject.getLatitude(noOfDivesSavedLocations));
		
		// save new coords to phone
		locationObject.writeObjectToFileOnPhone(locationObject);
		
		Log.d(TAG, "381 SAVUNG NEW COORD TO PHONE LAT: "+ latitude+ "LONG: "+ longitude);
		
		this.numberOfDiveOcations = numberOfDiveOcations+1;
		editor.putInt("numberOfDiveOcations", numberOfDiveOcations);
		editor.commit();
		
		}catch(Exception ex){
			
			ex.printStackTrace();
			Log.d("MAP 368", "EROOR GETTOG OBJECT AND WRITING TO IT: "+ locationObject.toString());
		}
		/* takes lat and long co-ords from Location object myLOc
		 *  in getCurrentLocation() method, this is called when Save Bitton clicked
		 *  Must save these details into Shared Pref array so can be loaded
		 *  in onCreate and plot markers fro previous dives
		 */
	
	} //saveLongTitudeAndLat



	protected void addMarkersForPreviousDives() {
		
		
		
		/*
		 * Get locatiopn object from file where loctions are added to
		 * ArrayList, and iterate to get coordinated and draw markers on map
		 * Object os saved and written to file when save location button is pressed
		 * 
		 * 
		 */
		savedDiveLocationsObject locationObject = new savedDiveLocationsObject (this);
		
		try{
			
			locationObject = (savedDiveLocationsObject)locationObject.getSavedLocationsObjectFromPhone();
			
			ArrayList latitudesArray = locationObject.getLatitudeArray();
			ArrayList longtitideArray = locationObject.getLongtitudeArray();
			
			for(int i=0; i<latitudesArray.size(); i++){
				
				Log.d(TAG+"472", "Nof of Lats: "+latitudesArray.size()+ " \n No of Longs saved: "+longtitideArray.size());
				
				double lat = (Double) latitudesArray.get(i);
				double longtit = (Double) longtitideArray.get(i);
				
				Log.d("MAP 452", "Retrived Lat "+ lat+" lontit "+longtit);
				
				// adding marker
				//googleMap.addMarker(marker);
				markerOptions = new MarkerOptions()
				.position(new LatLng(lat, longtit))
				.title("Dive "+ numberOfDiveOcations)
				.visible(true)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.pushpin3));
				
				mMap.addMarker(markerOptions);
				
				
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			//Log.d("MAP 446", "ERROR GETTOMG LOC OBJECT: "+ ""+e.getMessage().toString());
			
		}
		
		
		// terate object
		
		/*
		 *  get lat and long co-ords form shared prefs and add markers
		 *  Get sharde pref object, each string is delimited by \ to give the lat and long value
		 *  then converted to double and marker drawn. Each co oddinate Key:value key is the noOfDives+int noFODves
		 *  
		 *  First get from sharde pre file the int noSavedDivesLactions so we cam iterate the pref
		 *  file and plot 
		 */
		
		
	} // addMarkersPrevDives
	
	
	
	
	protected void getCurrentLocation() {
		
		// Get location of current usre devoce only, not marker!
		
		// called when Save button clicked, send data to dive log and save
		// shared pref of last dive
		// and send location string data in bundle to LogDive.class

		// aynch class takes generics: Location object, void indaicates that
		// progress units are not used and String-represting the return address
		// Ensure that a Geocoder services is available
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Geocoder.isPresent()) {
			// Show the activity indicator
			mActivityIndicator.setVisibility(View.VISIBLE);
			/*
			 * Reverse geocoding is long-running and synchronous. Run it on a
			 * background thread. Pass the current location to the background
			 * task. When the task finishes, onPostExecute() displays the
			 * address.
			 */
			
			// issue is line 603 retunrs only location of the user device!!!
			
			Location myLoc = mMap.getMyLocation();
			(new GetAddressTask(this)).execute(myLoc);

			if(myLoc!=null){
				
			
			double lat = myLoc.getLatitude();
			double longtitude=myLoc.getLongitude();
			
			
			saveLatAndLongCordinateToArray(lat, longtitude);
			// send the data retruend from aych class to edittext to Log Dive
			// Activity
			}// if not null
			
			sendDataBack();
		}// edn if

	}// end get current location
	

	// handles getting STRING location with a location object holding lat/long cords, called from Save
	// Button/getCurrentLocation()
	
	private class GetAddressTask extends AsyncTask<Location, Void, String> {
		Context mContext;

		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		
		protected String doInBackground(Location... myLoc) {
			// get the current loaction from input paramters location object
			mActivityIndicator.setVisibility(View.VISIBLE);
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = myLoc[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
				 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("No GPS/Wi-Fi detected! \nTurn on Wi-Fi or Return to Log \nand enter location manually.");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			} catch(Exception nullpointer){
				nullpointer.printStackTrace();
				return "Opps! Something went wrong with Google Maps.....try agan..";
				//Toast.makeText(getApplicationContext(), "Opps! Something went wrong with Google Maps.....try agan..", Toast.LENGTH_SHORT).show();
			}
		
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */
				String addressText = String.format(
						" %s, %s, %s, %s, %s, %s, %s, %s",
						
						// lose the street address!!
						
//						// If there's a street address, add it
//						address.getMaxAddressLineIndex() > 0 ? "": address
//								.getAddressLine(0) ,
								
						// Locality is usually a city
						address.getLocality() == null ? "" : address
								.getLocality(),
								address.getLocale() == null ? "":address.getLocale(),
						// get landamrk
						address.getFeatureName() == null ? "" : address
								.getFeatureName(),
						// get negibour hood name or null if none
						address.getSubLocality() == null ? "" : address
								.getSubLocality(),
						// The country of the address
								address.getSubAdminArea()==null ? "":address.getSubAdminArea(),
										address.getSubLocality()==null ? "":address.getSubLocality(),
												address.getThoroughfare()==null ? "": address.getThoroughfare(),
						address.getCountryName());
				// Return the text
				/*
				 * if (addresses.size() > 0) {
				 * myLocation.setText(addresses.get(0).getFeatureName() + ", " +
				 * addresses.get(0).getLocality() + ", " +
				 * addresses.get(0).getAdminArea() + ", " +
				 * addresses.get(0).getCountryName()); }
				 */
				return addressText;
			} else {
				return "No address found";
			}
		}// end doInBackground

		/**
		 * A method that's called once doInBackground() completes. Turn off the
		 * indeterminate activity indicator and set the text of the UI element
		 * that shows the address. If the lookup failed, display the error
		 * message.
		 */
		@Override
		protected void onPostExecute(String address) {
			// Set activity indicator visibility to "gone"
			mActivityIndicator.setVisibility(View.GONE);
			// Display the results of the lookup.
			myLocation.setText(address);
		}// edn onPostExecute
	}// end async class

	
	
	
	private void sendDataBack() {
		
		/*
		 * save dive cords to phone with serlized object where they are wriiten to file
		 * Lat and Long are taken from onClick placing marker on map
		 */
		
		this.saveLatAndLongCordinateToArray(saveLastDiveLatitude, saveLastDiveLongtitude);
		
		
		
		// send data back to LogDive setartActivity for result call

		Intent data = new Intent();
		data.setData(Uri.parse(this.myLocation.getText().toString()));
		setResult(RESULT_OK, data);
		
		
		// sav edive site coords to srliazed object
		//this.saveLatAndLongCordinateToArray(latitude, longitude)
		// iterate the noOfSvedLocations and add to shared prefs fro mapping prev dive loactionsloater
		
		numberOfDiveOcations+=1;
		savedDivesCo_ords_SharedPrefsFile = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		Editor editor = savedDivesCo_ords_SharedPrefsFile.edit();
		editor.putInt("numberOfDiveOcations", numberOfDiveOcations);
		editor.commit();
	
		// get the current numberOfDiveLocations int form shared prefs
		int locs = savedDivesCo_ords_SharedPrefsFile.getInt("numberOfDiveOcations", 0);
		Log.d("MAPS:", "ADDING ANOTHER DIVE LOACTION TO sHARED PREDFS, TOTAL IN SP= "+ locs+ " SHOULD BE: "+numberOfDiveOcations );
		

		Toast.makeText(getBaseContext(), "Saved!", Toast.LENGTH_SHORT).show();
		// run a 3s therad so tiast can display bfefore actovity is closed
		Thread threadCLose = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					// sleep for 5 seconds to let splash activity display
					Thread.sleep(5000);// milliseconds

				} catch (InterruptedException ex) {
					// if interruoted thread
					ex.printStackTrace();
				}// end catch
			}
		});// end thread
		threadCLose.start();
		// close this activity
		
		// free resources and detroy activity
				this.mMap=null;
				finish();
				System.gc();
	}// end sednDataBack

	private void getAddress(double latitude, double longitude) {

		// get the string address from lat/lat returned from find my location in
		// onCreate
		//perform this task asynchronously in background as may take time
		
		
		
		try {
			Geocoder geo = new Geocoder(
					MapDiveLocation.this.getApplicationContext(),
					Locale.getDefault());
			List<Address> addresses = geo.getFromLocation(latitude, longitude,
					1);
			if (addresses.isEmpty()) {
				myLocation.setText("Waiting for Location");
			} else {
				if (addresses.size() > 0) {
					myLocation.setText(addresses.get(0).getFeatureName() + ", "
							+ addresses.get(0).getLocality() + ", "
							+ addresses.get(0).getCountryName());
				}
			}// end of else
		} catch (Exception e) {
			Toast.makeText(getBaseContext(),
					"Map could not retrive dive site,  try later :(",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace(); // getFromLocation() may sometimes fail
		}// end catch/try
	}// end getAddress
	
	
	
	private class getAddressFromMarkerAsynch extends AsyncTask<LatLng, Void, String>{
		
		
		/*
		 * Reverse Geocoding, return string address from lat long object
		 * 
		 * Called from onClick Marker, when user clicks on map we return the address as a
		 * String and deisplay in  textfoeld
		 * asynch innner class to handle reverse geocode with lat lng object form marker set value
		 */
		//called from getAddress/onMarkerDrag
		
		Context mContext;
		//constructor 
		
		public getAddressFromMarkerAsynch(Context context){
			super();
			mContext = context;
		}//end inner class const
		
		
		@Override
		protected String doInBackground(LatLng... params) {
			
			// get address and return it as string to onExecute
			
			 Geocoder geocoder = new Geocoder(mContext);
	            double latitude = params[0].latitude;
	            double longitude = params[0].longitude;
	 
	            List<Address> addresses = null;
	            String addressText="";
	 
	            try {
	                addresses = geocoder.getFromLocation(latitude, longitude,1);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	 
	            if(addresses != null && addresses.size() > 0 ){
	                Address address = addresses.get(0);
	 
	                addressText = String.format("%s, %s",
	                		//lose street address, so first %s on formated string deleted too
	                //address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                address.getLocality(),
	                address.getCountryName());
	            }
	 
	            return addressText;
	        }
		protected void onPostExecute (String addressText){
			//send to edittext 
		
			try{
			if(addressText!=null && !addressText.isEmpty()){
				
			
			myLocation.setText(addressText);
			markerOptions.title(addressText);
			//place a marker on toucehd position
			mMap.addMarker(markerOptions);
		}else{
			myLocation.setText("Not Found");
			markerOptions.title("Not Found");
			//place a marker on toucehd position
			mMap.addMarker(markerOptions);
		}
			}catch(Exception ex){
				
				Toast.makeText(getApplicationContext(), "Slow Down!! Try Agian", Toast.LENGTH_LONG);
			}
		}//end on Post Execute
		//takes a latlng obj, doesnt use a intermediate step and onExecute reruns a strig
		
	}//end getAddressFromMarkerAsynch

	@Override
	protected void onResume() {
		// in the event andother phone activity is created ie phone call, we can
		// resume after
		super.onResume();
		setUpMap();
	}// end onResume

	private void setUpMap() {
		if (mMap != null) {
			return;
		}
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (mMap == null) {
			return;
		}

		mMap.setTrafficEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

	}// end setUpMap

	
	// handle on drag listener evenst when dragging marker and setting to ne
	// wlocation
	@Override
	public void onMarkerDrag(Marker marker) {
		// This method call when we drop the marker at desired position. and
		// this method we use for drag geofence so now where we drop
		// marker we can get latitude and longitude of that position
		markerMovedLocation = true;
		LatLng markerDragged = marker.getPosition();
		//getAddress(markerDragged.latitude, markerDragged.longitude);
		new getAddressFromMarkerAsynch(this).execute(markerDragged);

	}// end onMarkerDrag

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng point) {
		
		/*
		 * save the lat and long co ords a global floats for saving to srrlised object
		 * this is then written to file when save button is clicked
		 * the lat lng corditnates when map is clicked
		 *Getting the Latitude and Longitude of the touched location
		 *
		 */
		
		
		Toast.makeText(getBaseContext(), "Droping Pin!", Toast.LENGTH_SHORT).show();
        touchLatLng = point;

        // Clears the previously touched position
        mMap.clear();

        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(touchLatLng));

        // Creating a marker
        markerOptions = new MarkerOptions();
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.pushpin3));

        // Setting the position for the marker
        markerOptions.position(touchLatLng);

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
        
        
        // set the lat and long globals fro savung to serrlized object on phone
        saveLastDiveLatitude = touchLatLng.latitude;
        saveLastDiveLongtitude = touchLatLng.longitude;
        
        Log.d(TAG, "972: SAVING CO ORDS OF MARKER FOR OBJECT: lat "+ saveLastDiveLatitude 
        		+ " long : " + saveLastDiveLongtitude);
        
       

        // Adding Marker on the touched location with address, get address using the cp ords form marker
        
        new getAddressFromMarkerAsynch(this).execute(touchLatLng);

		
	}//end onMapClick







	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		// free resources and detroy activity
		this.mMap=null;
		finish();
		System.gc();
	}

	
	

}// end class