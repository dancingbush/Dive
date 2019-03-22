package com.mooney.diveapp;

//this is a List, using the customized CursorAdtper from ItemAdapter
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

public class ViewListOfDives extends ListActivity {

	private static diveDataBase data;
	private Cursor cursor;
	private ItemAdapter adapter;
	private ProgressDialog pd;
	private getCursor getCursorAysnch;
	private boolean preiwImagesInListView=false;
	private int value=1;

	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Send the row number of dive selected to the ViewSelectedDive class
		
		super.onListItemClick(l, v, position, id);
		String row = cursor.getString(cursor.getColumnIndexOrThrow(diveDataBase.KEY_ROWID));
		Intent i = new Intent("android.intent.action.VIEWSELECTEDDIVE");
		Bundle extras = new Bundle();
		extras.putString("row", row);
		i.putExtras(extras);
		startActivityForResult(i, 0);
		//Toast.makeText(getBaseContext(), "Row selected: "+ row, Toast.LENGTH_LONG).show();
		finish();//free resources

	}// end on list item clicked

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		previewDives();
		
		
		

		
		
		// Set up the screen to be full screen with no title bar etc using
		// WindowsManager
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	
		//call the asynch task to get cusror and create the adpter and set the list adpter
		//getCursorAysnch = (getCursor) new getCursor().execute();
		//new getCursor().execute();
		
	}// end on create




	private void previewDives() {
		//ask user if they wish to preview images in ist view as will take more time
				AlertDialog.Builder build = null;
				AlertDialog dialog = null;
				build = new AlertDialog.Builder(this);
				build.setCancelable(true);
				build.setMessage("Do you want to preview your Dive Site images? \nThis may take more time if you have a large number of dives");
				build.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// preview dives
								preiwImagesInListView= true;
								//call the asynch task to get cusror and create the adpter and set the list adpter
								getCursorAysnch = (getCursor) new getCursor().execute();
								value++;
								//return;//exit

							}// end on click
						});// end pos button take photo

				// get photo from SD card option
				build.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								preiwImagesInListView=false;
								//call the asynch task to get cusror and create the adpter and set the list adpter
								getCursorAysnch = (getCursor) new getCursor().execute();
								value++;
								//return;//exit to oncreate

							}// end onclick
						});// end pos button take photo
				dialog = build.create();
				dialog.setTitle("Preview Dive Images");
				dialog.show();
				
				//return preiwImagesInListView;
				
		
	}




	private void displayDialog() {
		//display dialog of there are no entries in the database and finish the activity
		
		AlertDialog.Builder build = null;
		AlertDialog dialog = null;
		build = new AlertDialog.Builder(this);
		build.setCancelable(true);
		build.setMessage("You have no dives saved to view!!");
		build.setPositiveButton("Log A Dive",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// go to log a dive
						Intent i = new Intent(
								"android.intent.action.LOGDIVE");
						startActivity(i);
						finish();

					}// end on click
				});// end pos button take photo

		// get photo from SD card option
		build.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();

					}// end onclick
				});// end pos button take photo
		dialog = build.create();
		dialog.setTitle("No Logged Dives");
		dialog.show();
		
		
	}//end display dalog





	
	private final class getCursor extends AsyncTask<Void, Void, Cursor>{

		protected void onPreExecute(){
			//show dialog while loading data
			pd = new ProgressDialog( ViewListOfDives.this, ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("Loading your dives....");
			pd.show();
			
		}
		
		

		@Override
		protected void onPostExecute(Cursor cursor) {
			/*
			if (value==1){
				previewDives();
				value++;
				Log.d("ViewListDives", "value after question: "+ value);
				
			}//else if (value>1){
				Log.d("ViewListDives", "value after if:else: "+ value);
			*/
						//check if data available
						if(cursor!=null && cursor.getCount()>0){
						// get customised array adoater list
							//previewDives();
							Log.d("View List", "Value of preview dives is :" +preiwImagesInListView);
						adapter = new ItemAdapter(ViewListOfDives.this, cursor, preiwImagesInListView);
							//adapter = new ItemAdapter(ViewListOfDives.this, cursor);
						}else{
							
								//display o dives in data base message and finish this activity
								displayDialog();
							
						}
						ViewListOfDives.this.setListAdapter(adapter);
						ViewListOfDives.data.close();
			super.onPostExecute(cursor);
			// dispose dialog
						if(pd.isShowing()){
									pd.dismiss();
						}
			//}//end if value
		}



		
		@Override
		protected Cursor doInBackground(Void... params) {
			// get the cursor from database 
			
			
			
			if(!isCancelled()){
						ViewListOfDives.data = new diveDataBase(ViewListOfDives.this);
						ViewListOfDives.data.open();
						// get cursor object holding all data, use a asynch inner class to load 
						cursor = data.getCursorData();

						
						//ViewListOfDives.data.close();
			}//edn if no cancelled by back button pressed
						
			return cursor;
		}
		
		
	}//end getCursor asynch






	@Override
	protected void onPause() {
		// try to quit cursoradpter from reriving and upload data when user clicks back button
		ViewListOfDives.data.close();
		cursor=null;
		//adapter=null;
		getCursorAysnch.cancel(true);
		//now cancel backgound process of Itemadatpetr class to free memory
				//adapter.getImageAsynch.cancel(true);
		Log.d("View Dives", "On pause back button clicked");
		
		super.onPause();
	}




	@Override
	public void onBackPressed() {
		// try to quit cursoradpter from reriving and upload data when user clicks back button
		super.onBackPressed();
		//cancel teh background process of asycn task
		getCursorAysnch.cancel(true);
		
		//now cancel backgound process of Itemadatpetr class to free memory
		adapter.getImageAsynch.cancel(true);
		Log.d("Vuiew List Dives:", "Back button pressed");
		
		
		//((adapter)ItemAdapter.).finish();
		ViewListOfDives.data.close();
		cursor=null;
		adapter=null;
		//finish();
		
		
	}
	
	

}// end class
