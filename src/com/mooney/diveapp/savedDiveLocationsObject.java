package com.mooney.diveapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class savedDiveLocationsObject implements Serializable {

	/*
	 * Instances of this class are created whhen loading map MapDiveLocation.java
	 * Locations are saved to the instance and wriiten to phone directorty
	 * New instance created in onCrete of Maps and cordintes taken form 
	 * Array list of object and markers set on Map of 
	 * all previous dive locations
	 * 
	 * 
	 * 
	 */
	
	
	// fields, context must be transient as context is-a activity and is non serizable
	
	ArrayList<Double> latitudeArray= new ArrayList<Double>();
	ArrayList<Double> longtitudeArray= new ArrayList<Double>();
	transient Context objectConetxActivity;
	String fileName="savedDiveLocations";
	File theFileName; 	
	final String TAG="LOCAT_OBJECT";
	
	
	public savedDiveLocationsObject(Context context){
		
		//addf coordinates to array list
		//latitudeArray.add(latitude);
		//longtitudeArray.add(longtitude);
		objectConetxActivity=context;
		this.theFileName= new File(objectConetxActivity.getFilesDir(), fileName);
		
		// this returns /data/data/com.mooney.diveapp/files/savedDiveLocations
		if(!theFileName.exists()){
			try {
				theFileName.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d(TAG+"51", "ERROR CREATING FILE");
				e.printStackTrace();
			}
		}
	}
	
	
	// getters
	public double getLatitude(int index){
		
		return latitudeArray.get(index);
		
	}
	
public double getLongtitude(int index){
		
		return longtitudeArray.get(index);
		
	}
	

// setters
public void addLatitude(double lat){
	
	this.latitudeArray.add(lat);
	
}


public void addLongigtude(double longtit){
	
	this.longtitudeArray.add(longtit);
	
}

public ArrayList getLatitudeArray(){
	
	return this.latitudeArray;
}

public ArrayList getLongtitudeArray(){
	
	return this.longtitudeArray;
}

// write object to file on a phone directory

public void writeObjectToFileOnPhone(Object mapLocationsObject){
	
	
	ObjectOutputStream oos = null;
	FileOutputStream fout = null;
	
	
	// save to internal durectpry as opposed to exteranl SD card
	try{
		
		// NPE at objectConetxActivity
		String fileAddress = objectConetxActivity.getFilesDir()+ "/"+fileName;
		fout = new FileOutputStream(fileAddress); // filepath
		oos = new ObjectOutputStream(fout);
		oos.writeObject(mapLocationsObject);
		
	}catch(Exception ex){
		
		ex.printStackTrace();
		Log.d("LOCAT_OBJECT 90", "ERROR SAVING OBJECT: "+ ex.getMessage().toString());
		
	}finally{
		
		if(oos!=null){
			try {
				oos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	}
	
	
	// get the object and return it form phone directory
	
	public Object getSavedLocationsObjectFromPhone(){
		
		// file address is /data/data/com.mooney.diveapp/files/savedDiveLocations
		String fileAddress = objectConetxActivity.getFilesDir()+ "/"+fileName;
		ObjectInputStream ois = null;
		FileInputStream streamIn=null;
		Object locationObject = null;
		
		try{
			streamIn = new FileInputStream(fileAddress); // address of file
			ois = new ObjectInputStream(streamIn);
			locationObject = ois.readObject();
		}catch (Exception e){
			
			e.printStackTrace();
			Log.d("LOCAT_OBJECT", "ERROR READING FROM DIR: " +e.getMessage().toString());
		}finally{
			
			if(ois!=null){
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return locationObject;
		}// finally

	}
	
	
	
	// when object is not craryed from a contrctor we set the conetx manually
	public void setObjectContext(Context myContext){
		
		this.objectConetxActivity=myContext;
	}
} // class
