package com.mooney.diveapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Checklist extends ListActivity {

	
private static final String TAG = LogDive.class.getName();
	
	private TextView title;
	private ImageView displayIcon;
	private boolean clicked=false;
	private ImageView equitmentCrossed;
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//  when a list item is clicked, first remove the XML set image  cross to a replace with tick on image view

	
		equitmentCrossed = (ImageView) v.findViewById(R.id.iv_equitmentCheckList_Cross);
		//if already clicked once allow item to be deselected
		/*
		if(clicked){
			equitmentCrossed.setBackground(null);
			clicked=false;
			return;//leave this method
		}
		//must use view.findId.... so we are selecting only one list otem, not all of them
		clicked=true;
		*/
		//equitmentCrossed.setBackgroundResource(0);
		//equitmentCrossed.setBackground(null);
		equitmentCrossed.setBackgroundResource(R.drawable.tick);
	 //title.setText("CLICKED");
		
		//set checked item against selected item
		l.setItemChecked(position, l.isItemChecked(position));

	}//end onListItemClicked

	// ListActivity subclass of activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		

		super.onCreate(savedInstanceState);
		
		//get a ref to the list view object and set t allow mulitple item selections
		ListView lstView = this.getListView();
		lstView.setChoiceMode(2);
		lstView.setTextFilterEnabled(true);//allow search by keypad input
		
		/*Set up the screen to be full screen with no title bar etc using WindowsManager
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		*/
		
		/*craete a unique arraylist and populate with string:images data 
		sets and override Adapter getView() to populate the 
		customised list with and strings with pair arraylist
	   images
	    */
		ArrayList<Pair<String, Integer>> equitmentAndImagesArray = new ArrayList<Pair<String, Integer>>();
		equitmentAndImagesArray.add(Pair.create("BCD", R.drawable.bcd));
		equitmentAndImagesArray.add(Pair.create("Tank (with air!) & Regulator", R.drawable.tankandreg));
		equitmentAndImagesArray.add(Pair.create("Fins (Flippers)", R.drawable.fins));
		equitmentAndImagesArray.add(Pair.create("Suit", R.drawable.drysuit));
		equitmentAndImagesArray.add(Pair.create("Gloves, hood, boots", R.drawable.hoodsglovesandboots));
		equitmentAndImagesArray.add(Pair.create("Weights +/- Belt", R.drawable.weightsandbelt));
		equitmentAndImagesArray.add(Pair.create("Mask", R.drawable.mask));
		equitmentAndImagesArray.add(Pair.create("Computer", R.drawable.divecomputer));
		equitmentAndImagesArray.add(Pair.create("Torch + Backup", R.drawable.torch));
		equitmentAndImagesArray.add(Pair.create("Camera", R.drawable.divecamera));
		equitmentAndImagesArray.add(Pair.create("Buddy Check: \nB-Buoyancy-BCD Inflate\n" +
				"W-weights R-releases A-air Fi-final", R.drawable.buddycheck));
		
		/*
		equitmentAndImagesArray.add(Pair.create("Buddy Check 2: W - Weight (Cx Quick Release System", R.drawable.buddycheck));
		equitmentAndImagesArray.add(Pair.create("Buddy Check 3: R - Releases (CxEquipment Releases)", R.drawable.buddycheck));
		equitmentAndImagesArray.add(Pair.create("Buddy Check 4: A - Air (Confirm Air Is Switched On & Sufficient Supply", R.drawable.buddycheck));
		equitmentAndImagesArray.add(Pair.create("Buddy Check 5: F - Final Check", R.drawable.buddycheck));
		*/
		
		// add all items
		ArrayAdapter checklistadaptor = new ArrayAdapter<Pair<String, Integer>>(this,
		            R.layout.checklist_row, R.id.tv_equimentChecklistName, equitmentAndImagesArray) {
			
			//over ride getView
		    public View getView(int position, View convertView, ViewGroup container) {
		        if(convertView == null) {
		            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checklist_row, container, false);
		        }
		        
		        title = (TextView) convertView.findViewById(R.id.tv_equimentChecklistName);
		        Pair<String, Integer> item = getItem(position);
		        title.setText(item.first);
		        displayIcon = (ImageView) convertView.findViewById(R.id.iv_equimentChecklist);
		        displayIcon.setBackgroundResource(item.second);
		        //title.setCompoundDrawablesWithIntrinsicBounds(item.second, 0, 0, 0);
		        /*
		        if(clicked){
		        	//do not draw a background default resource
		        	equitmentCrossed.setBackground(null);
		    		equitmentCrossed.setBackgroundResource(R.drawable.tick2);
		    		//title.setText("CLCIKED");
		        }else{
		        	equitmentCrossed.setBackgroundResource(R.drawable.cross);
		        }
		        */
		        return convertView;
		    }
		 };//end getView
		 
		 this.setListAdapter(checklistadaptor);
		 

	}//end onCreate()
	
	
	
	
	
}//end class

