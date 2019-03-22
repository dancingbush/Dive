package com.mooney.diveapp;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainMenu extends ListActivity {

	private static final String TAG = LogDive.class.getName();
	
	// a string array holding the list items
	String classes[] = { "Profile", "Dive History", "Log A Dive", "View Dives",
			"Checklist", "Tides", "Wind Directions & Speed", "Your Gallery", "About" };// each class is a menu Actiitem

	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//  when a list item is clicked, the list, view, position and the id
		// is apssed to here
		super.onListItemClick(l, v, position, id);
		String cheese = classes[position];// allocate list item clicked to
		
		if(cheese.contains("Wind Directions & Speed")){
			cheese = "WindDirection_Surf";
			
		}
		if(cheese.contains("Log A Dive")){
			cheese = "LogDive";
			
		}
		if(cheese.contains("View Dives")){
			cheese = "ViewListOfDives";
			
		}
		if(cheese.contains("Dive History")){
			cheese = "DiveHistory";
		}
		if(cheese.contains("Gallery")){
			cheese = "GalleryOfImages";
		}
		
		// Alternate way to create new activty via java class varible
		try {
			// get intent accroding to pacakagemname.className and start new activity
			Class ourClass = Class.forName("com.mooney.diveapp."
					+ cheese);// append the list otem clicked to path fro intent
								// new actvity
			Intent ourIntent = new Intent(MainMenu.this, ourClass);// (context,
																// classNAme)
			this.startActivity(ourIntent);
			//finish();
		
		} catch (ClassNotFoundException e) {
			Toast.makeText(this, "Class Not Found ", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}

	}//end onListItemClicked

	// ListActivity subclass of activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// this method will craete the GUI list items, passed in the ArrayAdator
		// constructor

		super.onCreate(savedInstanceState);
		
		/*Set up the screen to be full screen with no title bar etc using WindowsManager
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		*/
		
		//craete a unque arraylist and populate with string:images data setds and othen override Adapter getView() to populate the cutsomised list woth
		//images
		ArrayList<Pair<String, Integer>> namesAndImagesArray = new ArrayList<Pair<String, Integer>>();
		namesAndImagesArray.add(Pair.create("My Profile", R.drawable.profile));
		namesAndImagesArray.add(Pair.create("Dive History", R.drawable.profile2));
		namesAndImagesArray.add(Pair.create("Log A Dive", R.drawable.logdive));
		namesAndImagesArray.add(Pair.create("View Dives", R.drawable.search));
		namesAndImagesArray.add(Pair.create("Checklist", R.drawable.weather));
		namesAndImagesArray.add(Pair.create("Tides", R.drawable.tides));
		namesAndImagesArray.add(Pair.create("Wind Directions & Speed", R.drawable.weather2));
		namesAndImagesArray.add(Pair.create("Dive Gallery", R.drawable.camera4));
		namesAndImagesArray.add(Pair.create("About/Contact", R.drawable.emailmeicon));
		
		// add all items
		ArrayAdapter listadaptor = new ArrayAdapter<Pair<String, Integer>>(MainMenu.this,
		            R.layout.single_list_row, R.id.title, namesAndImagesArray) {
		    public View getView(int position, View convertView, ViewGroup container) {
		        if(convertView == null) {
		            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_list_row, container, false);
		        }
		        TextView title = (TextView) convertView.findViewById(R.id.title);
		        Pair<String, Integer> item = getItem(position);
		        title.setText(item.first);
		        ImageView displayIcon = (ImageView) convertView.findViewById(R.id.list_image);
		        displayIcon.setBackgroundResource(item.second);
		        //title.setCompoundDrawablesWithIntrinsicBounds(item.second, 0, 0, 0);
		        return convertView;
		    }
		 };//end getView
		 
		 this.setListAdapter(listadaptor);

	}//end onCreate()

	// add activity method onCreateOpetions menu to handle user definied XML
	// menu in menus folder
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		// craete the XML col_menu menu from RES/menu folder
		// delete the inhereited retun statemnt, eep the super const staement
		// craete a MenuInlfator and pass our collMenu to the inflate method of
		// MenuInflator object
		
		//need to implement thse claases for infaltanble options menu
		MenuInflater inflateMenu = this.getMenuInflater();
		//inflateMenu.inflate(R.menu.cool_menu, menu);
		// craete a emthdo to handle clickable events from menu items,
		// source-omplement methods from Activity-onOptionItemSeleced
		return true;

		// return super.onCreateOptionsMenu(menu);
	}// end onCtrateOptioneMenu

	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle when item selected from the cool_menu XML inflatable menu
		// delete retyrn supr refernecs
		// return super.onOptionsItemSelected(item);
		// item passed represents the clicked object
		switch (item.getItemId()) {
		case R.id.MenuItem_About_Us:
			//call the About_Us class
			Intent callClass = new Intent("com.mooneycallans.buckystutorials.ABOUT_US");
			startActivity(callClass);
			//the theme to this class is set in the manifast declaration - android:theme:"@android.theme/Theme.Dialog", @android as theme comes fofm the SDK

			break;
		case R.id.MenuItem_Prefernces:
			//call teh perferces class as a intent
			Intent runPrefernces = new Intent("com.mooneycallans.buckystutorials.PREFERNECMENU");
			startActivity(runPrefernces);

			break;
		case R.id.MenuItem_Exit:
			//quit the ap by calling the finish stage of activity cycle
			finish();
			break;

		}// end switch

		return false;// method reqs to return a boolean, so return false if
						// switch statement faills to return anythng
	}// end Infakable Menu event item handler
	*/

	// add activity method onCreateOpetions menu to handle user definied XML
	// menu in menus folder

}// end class Menu
