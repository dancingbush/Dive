package com.mooney.diveapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * This class will display a map and drop markers for all previous dove site
 * Dive sites are saved in MapDiveLocation when user selcets save location
 * Lat and Long floast are saved to Lat Long Arraylists as part of a serizlized 
 * object that is saved to the phone interbnal emnu in MapDieLocation
 * 
 * This class will retruve the object, lat long coords and plot the points
 * as a marker
 * 
 * Follwong AndroidHve tutorial http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
 * 
 * Also, allow zoom function when a marker is selected, and a back and forwadrd TV obnClickListener
 * that iterates thriugh the dives, is switched off when we reah end of dives or start (cant go back)
 */

public class Map_DiveSites extends FragmentActivity implements
		OnMarkerClickListener, OnClickListener {

	private boolean isInterNetConnected;
	private final String TAG = "Map_DIVESITES";
	private GoogleMap googleMap;
	private String stringMarkerAddress;
	private TextView tv_displayMarkerAddress, backDiveButton; //former doubles up as a forward button
	private int noOfDiveMarkers;
	private ArrayList latitudesArray;
	private  ArrayList longtitideArray;
	private int currentMakerNumber;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.divesite_map);

		
		
		// get text voew and set it underlined and onclicklistener fro iterating doves back/forward

		tv_displayMarkerAddress = (TextView) this
				.findViewById(R.id.textViewDiveSiteMarkerAddress);
		tv_displayMarkerAddress.setPaintFlags(tv_displayMarkerAddress.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		backDiveButton = (TextView) this.findViewById(R.id.tv_backButtonDiveMaps); 

		
		
		// first check for internet connection
		this.isInterNetConnected = this.checkInternetConnection();
		if (!isInterNetConnected) {
			Log.d(TAG, "NO Internect Connection Estabished");
			alertDialog("No Internet Connection Established!. Please check your connection "
					+ " and try again.");

			// debugging call only
			displayMap();
		} else if (isInterNetConnected) {
			Log.d(TAG, "Internect Connection Estabished");
			displayMap();
		}
		
		
		// load the dive markers asynchrously as bg task
		
		

	} // onCreate

	
	
	
	private void displayMap() {
		// if intenet connected display the map

		// lazy intoilsation

		if (googleMap == null) {

			googleMap = ((SupportMapFragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.mapDiveSites)).getMap();

			
			googleMap.setOnMarkerClickListener(this);
			googleMap.setTrafficEnabled(true);
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(true);
			googleMap.getUiSettings().setZoomGesturesEnabled(true);
			googleMap.getUiSettings().setRotateGesturesEnabled(true);
			
			
			// allow for button to  zoom to current location
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			
			// check if map created proprerly
			if (googleMap == null) {
				Toast.makeText(getBaseContext(), "Unable to crate Map!",
						Toast.LENGTH_LONG).show();
			} else if (googleMap != null) {

				Log.d(TAG, "MAP CREATED: DISPLAYING MARKERS CALLED");
				//displayMarkers();
				
				// call aycn class to load markers
				new displayMarkers().execute();
				
				// set on cick listeners once dives are loaded
				this.backDiveButton.setOnClickListener(this);
				this.tv_displayMarkerAddress.setOnClickListener(this);
			}

		}// if

	} // display map

	
	
	
	private void displayMarkers() {
		/*
		 * Internet connected and googleMap craeted and displayed Get serlised
		 * object from phone and get lat./long floats from arraylists on objec
		 * set markers from these floats
		 */

		/*
		 * Get locatiopn object from file where loctions are added to ArrayList,
		 * and iterate to get coordinated and draw markers on map Object os
		 * saved and written to file when save location button is pressed
		 * 
		 * When loaded and all displayed caall zoom functuoin to zoom to first ever dive
		 */

		savedDiveLocationsObject locationObject = new savedDiveLocationsObject(
				this);

		try {

			locationObject = (savedDiveLocationsObject) locationObject
					.getSavedLocationsObjectFromPhone();

			// if null then no data in object saved to phone form
			// MapDiveLocation.java
			if (locationObject == null || locationObject.latitudeArray.size()==0) {
				this.alertDialog("No dive site locations saved!\n"
						+ "When logging a dive, save your loaction in Map provided when \n"
						+ " setting the dive loaction (Press Search Icon)");
			}

			latitudesArray = locationObject.getLatitudeArray();
			longtitideArray = locationObject.getLongtitudeArray();

			for (int i = 0; i < latitudesArray.size(); i++) {

				Log.d(TAG + "472", "No of Lats: " + latitudesArray.size()
						+ " \n No of Longs saved: " + longtitideArray.size());

				double lat = (Double) latitudesArray.get(i);
				double longtit = (Double) longtitideArray.get(i);

				Log.d("MAP 452", "Retrived Lat " + lat + " lontit " + longtit);

				// adding marker
				// googleMap.addMarker(marker);
				MarkerOptions markerOptions = new MarkerOptions()
						.position(new LatLng(lat, longtit))
						.visible(true)

						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_map));

				markerOptions.getPosition();

				// get text address (global) form reverse geocoding inner class
				// (coords to address conversion)

				googleMap.addMarker(markerOptions);
				
				
				// zoom to first marker
				if(i==0){
					zoomToSelctedMarker_DiveSite(lat, longtit);
				}

			}

		} catch (Exception e) {

			this.alertDialog("No dive site locations saved!\n"
					+ "When logging a dive, save your loaction in Map provided when \n"
					+ " setting the dive loaction (Press Search Icon)");
			e.printStackTrace();
			// Log.d("MAP 446", "ERROR GETTOMG LOC OBJECT: "+
			// ""+e.getMessage().toString());

		}

	}

	private boolean checkInternetConnection() {
		// if no connection catch exceptions use a alert dialog to get user to
		// force quit actovoty

		ConnectivityManager connectivity = (ConnectivityManager) getBaseContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;

	}// end checkInternetConnection

	private void alertDialog(String message) {
		// called when nointernet connection isInterNetConnected=false

		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle("Opps!");
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// quit this activity and return to logdve to avoid null pointer
				// http exceptions
				finish();

			}
		});

		alert.show();
	}// end alertDialog

	private class getAddressFromMarkerAsynch extends
			AsyncTask<LatLng, Void, String> {

		/*
		 * Reverse Geocoding, return string address from lat long object
		 * 
		 * Called from onClick Marker, when user clicks on map we return the
		 * address as a String and deisplay in textfoeld asynch innner class to
		 * handle reverse geocode with lat lng object form marker set value
		 */
		// called from getAddress/onMarkerDrag

		Context mContext;

		// constructor

		public getAddressFromMarkerAsynch(Context context) {
			super();
			mContext = context;
		}// end inner class const

		@Override
		protected String doInBackground(LatLng... params) {

			// get address and return it as string to onExecute

			Geocoder geocoder = new Geocoder(mContext);
			double latitude = params[0].latitude;
			double longitude = params[0].longitude;

			List<Address> addresses = null;
			String addressText = "";

			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);

				addressText = String.format(
						"%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address.getLocality(),
						address.getCountryName());
			}

			return addressText;
		}

		protected void onPostExecute(String addressText) {
			// send to edittext
			// myLocation.setText(addressText);
			// marker.title(addressText);
			// place a marker on toucehd position
			// mMap.addMarker(markerOptions);
			stringMarkerAddress = addressText;

			tv_displayMarkerAddress.setText("Site :  "+ (currentMakerNumber+1) + "  " + addressText);

		}// end on Post Execute
			// takes a latlng obj, doesnt use a intermediate step and onExecute
			// reruns a strig

	}// end getAddressFromMarkerAsynch

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.displayMap();
		Log.d(TAG, "ON RESUME");
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		// free resources and detroy activity
		if(googleMap!=null){
		this.googleMap=null;}
		
		if(latitudesArray!=null){
			latitudesArray=null;
		}
		if(longtitideArray!=null){
			longtitideArray=null;
		}
		finish();
		System.gc();
	}

	@Override
	public boolean onMarkerClick(Marker markerClicked) {
		/*
		 * When makrek is clicked dispaly the address of it by calaing reverse
		 * geocoding (lat/lng to address) inner class asynch
		 * 
		 * Global String address is set in aych task and then dipslayed as
		 * marker title here
		 */

		// Adding Marker on the touched location with address, get address using
		// the cp ords form marker
		// and set global stringMarkerAddress to address of marker

		
		LatLng pos = markerClicked.getPosition();
		
		// set the currentMarKerIntger so from arralist index, this is used in onClick
		double index = pos.latitude;
		currentMakerNumber = this.latitudesArray.indexOf(index);
		
		
		// update text box with address
		
		new getAddressFromMarkerAsynch(this).execute(pos);
		
		
		// once marker clicked and we have cords zoom in 
		zoomToSelctedMarker_DiveSite(pos.latitude, pos.longitude);

		// change color of marker
		
		
		
	// GREEN color icon
	markerClicked.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


		Log.d(TAG, "336: Marker clicked, setting address as title");
		return false;
	}

	
	
	
	public void zoomToSelctedMarker_DiveSite(double lat, double longtitude){
		
		// when user selects a marker via touch or via touching next/back buttons
		// we zoom in to site, passing coords from marker been touched
		// called from onMarkerClicked
		
		
		
//		Toast.makeText(getBaseContext(), "Zooming to your dive site baby!",
//				Toast.LENGTH_SHORT).show();
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				lat, longtitude), 13));

		int zoomForFirstDIveOnly=3;
		
		
		// if first dive zooming too 2 
		if(this.currentMakerNumber==0){
		 zoomForFirstDIveOnly = 3;
			
		}else{
		     zoomForFirstDIveOnly=5;
		}
		
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(lat, longtitude)) // Sets the center of the map to
											// location user
				.zoom(zoomForFirstDIveOnly) // Sets the zoom, 2 max zoom out and 21 max zoom
							// in
				.bearing(10) // Direction that the camera is pointing in, in degrees clockwise from north.
				.tilt(10) // The angle, in degrees, of the camera angle from the nadir (directly facing the Earth).
				.build(); // Creates a CameraPosition from the builder

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
		
		
		// get long and lat cords and set as global vars
		Log.d("MAPS: ZOOM TO CURRENT LOCATION:LAT AND LONG CORDS:",
				String.format("Lat %f Long: %f",lat	, longtitude));
		
		
		
	}

	
	
	
	@Override
	public void onClick(View buttonClicked) {
		
		/*
		 * Once dives are loaded asynchronously, the onclick lisetners are set
		 * and we are at the first dive on in the array
		 * If on first dove we must deactovate the back button, and if on last we must 
		 * deactovate teh forward
		 * currentMakerNumber set in asynch class post execute form arraylist size to zero
		 * if first dive after all dives loaded, 
		 * and also set in onMarkerClick where it is set to index of current lat
		 * in arrallylist lattitude
		 * currentMarkerNumber is again set here to vaule+=1 so inc by one when the button is cicked
		 */
		
		
		
		
		switch(buttonClicked.getId()){
		
		case R.id.tv_backButtonDiveMaps:
			
			// move to prevoius dive as long as not at first dive
			if(currentMakerNumber!=0 && currentMakerNumber<=noOfDiveMarkers){
			
				int index=0;
				
				// check if at last dive or not in arraylist of lat co-ords
				if(currentMakerNumber!=0){
					
				index=  currentMakerNumber-1;
				
				}else if(currentMakerNumber==0){
					
					index=currentMakerNumber;
				}
				index=  currentMakerNumber-1;
				double lat = (Double) latitudesArray.get(index);
				double longtitude = (Double) longtitideArray.get(index);
				
				this.zoomToSelctedMarker_DiveSite(lat, longtitude);
				currentMakerNumber-=1;
				
				// update text box with address
				LatLng pos = new LatLng(lat, longtitude); 
				new getAddressFromMarkerAsynch(this).execute(pos);
				
				
			}
			
			
			break;
			
		case R.id.textViewDiveSiteMarkerAddress:
			
			
			// move to next  dive as long as not at first dive, noOfDiveMarkers set in doInBg loading markers
			
			int index=0;
			boolean reachedTopOfArray;
			
						if(currentMakerNumber <= latitudesArray.size()){
						
							Log.d(TAG, "499 OnClick CurrentMArkerNumber = "+currentMakerNumber+ " ARraylist Soze = "+ latitudesArray.size());
							
							/*
							 * Remember array.size = no of elments in array BUT
							 * the index starts fromm zero so index must never exceed  array.size-1!
							 */
							
							// check if at last dive or not in arraylist of lat co-ords
							if(currentMakerNumber<latitudesArray.size()){
								
							index=  (currentMakerNumber+1); 
							
							// we have moved to next dive
							currentMakerNumber+=1;
							
							// but if we have reached last pos in array and added one 
							if(currentMakerNumber==latitudesArray.size()){
								// if index not at end of arraylist
								index-=1;
								reachedTopOfArray=true;
								
							}
							
							}else if(currentMakerNumber==latitudesArray.size()){
								
								index=(latitudesArray.size())-1;//always minus 1 as array index start from 0, this will give us top position
							}
							
							// if index is the size of arraylist reset it to zero so we start from scratch ie dive no 1 at index of 0
							
							Log.d(TAG, "512: INDEX = "+ index);
							double lat = (Double) latitudesArray.get(index);
							double longtitude = (Double) longtitideArray.get(index);
							this.zoomToSelctedMarker_DiveSite(lat, longtitude);
							
							
							// update text box with address
							LatLng pos = new LatLng(lat, longtitude); 
							new getAddressFromMarkerAsynch(this).execute(pos);
						}
			
			break;
		} // switch
	
	} // onclick
	
	
	private class displayMarkers extends AsyncTask <Void, Void, Void>{
		
		private ProgressDialog pb;
		private boolean noSitesLogged;

		
		
		@Override
		protected void onPreExecute() {
			// display pb
			
			
			super.onPreExecute();
			pb = new ProgressDialog(Map_DiveSites.this);
			pb.setTitle("Lodaing Dive Sites......");
			pb.show();
			
		}

	

		@Override
		protected Void doInBackground(Void... arg0) {
			/*
			 * Internet connected and googleMap craeted and displayed Get serlised
			 * object from phone and get lat./long floats from arraylists on objec
			 * set markers from these floats
			 */

			/*
			 * Get locatiopn object from file where loctions are added to ArrayList,
			 * and iterate to get coordinated and draw markers on map Object os
			 * saved and written to file when save location button is pressed
			 * 
			 * When loaded and all displayed caall zoom functuoin to zoom to first ever dive
			 */

			savedDiveLocationsObject locationObject = new savedDiveLocationsObject(
					Map_DiveSites.this);

			try {

				locationObject = (savedDiveLocationsObject) locationObject
						.getSavedLocationsObjectFromPhone();

				// if null then no data in object saved to phone form
				// MapDiveLocation.java
				if (locationObject == null || locationObject.latitudeArray.size()==0) {
					
					// cannot gall any GUI views here or will get looper crash, insetad set a boolean
//					pb.cancel();
//					alertDialog("No dive site locations saved!\n"
//							+ "When logging a dive, save your loaction in Map provided when \n"
//							+ " setting the dive loaction (Press Search Icon)");
					noSitesLogged=true;
				}else{
					
				

				latitudesArray = locationObject.getLatitudeArray();
				longtitideArray = locationObject.getLongtitudeArray();
				}
				
			} catch (Exception e) {

				// ant display views in do in backgrouns
//				alertDialog("No dive site locations saved!\n"
//						+ "When logging a dive, save your loaction in Map provided when \n"
//						+ " setting the dive loaction (Press Search Icon)");
				e.printStackTrace();
				// Log.d("MAP 446", "ERROR GETTOMG LOC OBJECT: "+
				// ""+e.getMessage().toString());

			} 

			
			return null;
		} // doInBg



		@Override
		protected void onPostExecute(Void result) {
			//cancel dialog and display markers
			
			super.onPostExecute(result);
			
			
			if(pb.isShowing()){
				pb.cancel();
			}
		
			if(this.noSitesLogged==false){
			// set no of dive sites int
			noOfDiveMarkers = latitudesArray.size();
			
			
			// iterate coords and plot markers
			
			for (int i = 0; i < latitudesArray.size(); i++) {

				Log.d(TAG + "472", "No of Lats: " + latitudesArray.size()
						+ " \n No of Longs saved: " + longtitideArray.size());

				double lat = (Double) latitudesArray.get(i);
				double longtit = (Double) longtitideArray.get(i);

				Log.d("MAP 452", "Retrived Lat " + lat + " lontit " + longtit);

				// adding marker
				// googleMap.addMarker(marker);
				MarkerOptions markerOptions = new MarkerOptions()
						.position(new LatLng(lat, longtit))
						.visible(true)

						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_map));

				markerOptions.getPosition();

				// get text address (global) form reverse geocoding inner class
				// (coords to address conversion)

				googleMap.addMarker(markerOptions);
				
				
				// zoom to first marker if first set of coords from list
				if(i==0){
					zoomToSelctedMarker_DiveSite(lat, longtit);
					currentMakerNumber=0; // onClick
					LatLng pos = new LatLng(lat, longtit);
					
					
					// populate text view
					new getAddressFromMarkerAsynch(Map_DiveSites.this).execute(pos);
				}

			}

			}else if(this.noSitesLogged==true){
				
				alertDialog("No dive site locations saved!\n"
						+ "When logging a dive, save your loaction in Map provided when \n"
						+ " setting the dive loaction (Press Map Icon)");
			}
			
			
		} // onPstEx
		
	
	} // inner class
	
	
	
} // class
