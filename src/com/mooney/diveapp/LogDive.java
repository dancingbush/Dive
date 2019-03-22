package com.mooney.diveapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class LogDive extends Activity implements OnClickListener,
		OnRatingBarChangeListener ,StatusCallback{

	private static final int DATE_DIALOG_ID = 1;
	private static final String TAG = LogDive.class.getName();
	private EditText diveDate, diveNumber,   waterTemp,
			vizibilty, startBar, endBar,  comments,
			bottomTime, diveDepthEditText;
	private AutoCompleteTextView diveLocation, diveSite,diveBuddy, diveCenter;
	private String diveDateString, diveLoctionString, diveSiteString, diveBuddyString,
			diveCenetrString, comment, conditionChoice;
	private int bottomDiveTime, diveNum, waterTemperature, viz, startBars, endBars,
			condtionIndex, diveDepth;
	private RatingBar diveRating;
	private ImageButton diveSitePicture, activateMap;
	int yr, month, day;
	float diveRate;
	protected Intent usePhoneCamera;
	protected int cameraData = 0;
	Bitmap photo, photoBitmap;
	String encodedImage;// string base64 rep of image
	private Uri mCapturedImageURI;
	private Bitmap defaulPhoto;
	private Handler handler;
	private Spinner conditions;
	private String[] diveConditions;
	private Button saveLog, viewLog;
	// for image resizing
	static int w = 150;
	static int h = 150;
	private boolean canSaveToExternalSD = false;// if ext CD card avail
	private String diveAlbum = "dive_photos";// album name in ext directory
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	private ProgressDialog mProgressDialog;
	private String savedImagePath = "";// file path to image on SD card
	// store last dive number
	private SharedPreferences prefs;
	private String prefFilename = "MyPref";
	
	private UiLifecycleHelper uiHelper;//FB call back class instance
	private StatusCallback callback;
	public String APP_ID;
	private Facebook fb;
	private boolean isInterNetConnected=false;
	//save global varibles straningPath and Bitmap when Maps or FB clicked
	private Bitmap savedBitmapOnPause;
	private String savedImapthPathwayOnPause;
	private int nextDive;
	private URL userImageURL= null;
    private String userImageURLString = "";
    private String fbPhotoURL = null;//URL of dive site pic uploaded to FB
    boolean loadMyPicture=false;
    private ProgressDialog uploadUserPicToFacebookDialog;
    private int executeUploadOnce=0;//a flag to stop inifite uploads as ipload
    private Cursor cursor;
    private saveImageExtenalDriveAsynch saveIMageToPhoneAsynch;
    private ArrayList<String> diveLocations=new ArrayList<String>();
	private ArrayList <String> diveBuddies=new ArrayList<String>();
	private ArrayList <String> diveCenters=new ArrayList<String>();
	private ArrayList <String>diveSites= new ArrayList<String>();

    /*
     * Use aitocomplete textview for Log dive, dive center, dive buddy and dive loacton/site.
     * Build array by iterating DB with cursor object in background, diseable autocomplete textviews
     * in post execute when String arrays are set to Autocompete etextview adpters
     * 
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// create XML and initialize widgets
		super.onCreate(savedInstanceState);
		
		
		checkIfFirstEntry();
		
		//get FB app ID
		APP_ID = getString(R.string.app_id);
		
		fb = new Facebook(APP_ID);
		
		 
			    //}
		
		
				
		this.setContentView(R.layout.logdive);
		intilizeWidgets();
		
		//get string arraylists fro Autocompletetextviews and set adapter in postexceute
		
		new getAutoCompleteTexViewArrays().execute();
		
		//Toast.makeText(getApplicationContext(), "Complete all fields before saving or posting to FB!!", Toast.LENGTH_LONG).show();
		
		//onCreate - FB object to handle dialog callbacks
				uiHelper = new UiLifecycleHelper(this, callback);
				uiHelper.onCreate(savedInstanceState);
				

		// get current date
		Calendar today = Calendar.getInstance();
		yr = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH);
		day = today.get(Calendar.DAY_OF_MONTH);

		diveDate = (EditText) this.findViewById(R.id.dp_diveDate);
		diveDate.setOnClickListener(this);

		//get the last entered dive number from DB as string, add one and set to current dive
		diveDataBase db = new diveDataBase(this);
		db.open();
		
		
		
		
		nextDive = db.getLastDiveNumber()+1;
		db.close();
		if( nextDive != 0){
		diveNumber.setText(String.valueOf(nextDive));
		}
		else{
			diveNumber.setText("0");
		}
		
		
		/*if user nagigates from avtivity with maps or favebook, all veiw data will be saved automatically
		*but global varibles saveImagePath and PhotoBitmap will not, mu
		*st save these in overRde onSaveInstance and call back in OnCReate
		*/
		
		
		if (savedInstanceState != null) {
			
			
	        // Restore value of members from saved state
	       // mCurrentScore = savedInstanceState.getInt(STATE_SCORE); 
			this.savedImapthPathwayOnPause = savedInstanceState.getString("savedImapthPathwayOnSaveInstance");
			//Toast.makeText(getApplicationContext(), "on returning the image path is : " + savedImapthPathwayOnPause, Toast.LENGTH_SHORT).show();
			
			//now we just get the bitmap form the image path and return to IMageView
			Log.d("MYTAG", "on returning the image path is : " + savedImapthPathwayOnPause);
			
			if(!savedImapthPathwayOnPause.isEmpty())
			setImageViewFromSavedInstance(savedImapthPathwayOnPause);
			Log.d("MYTAG", "on returning the image path not found:==  " + savedImapthPathwayOnPause);
			//now we must ressign savedIMagePathway if we nagate from activit agian ie caling maps then facebook
			savedImagePath = savedImapthPathwayOnPause;
			Log.d("MYTAG", "on returning the image path set back to saveIMagePath:==  " + savedImagePath);
	    }
		
		
		
	}// end onCreate

	
	private class getAutoCompleteTexViewArrays extends AsyncTask <Void, Void, ArrayList>{
		
		 
		private ProgressDialog pd=new ProgressDialog(LogDive.this);;

		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
			diveSites= new ArrayList<String>();
			 diveLocations=new ArrayList<String>();
			 diveBuddies=new ArrayList<String>();
			 diveCenters=new ArrayList<String>();
			super.onPreExecute();
			
			
			pd.setMessage("Preparing Databse.....");
			pd.show();
			
			
		}

		@Override
		protected ArrayList<ArrayList> doInBackground(Void... params) {
			// iterate cursor object db and get list of strings
			
			ArrayList<ArrayList> lists= new ArrayList<ArrayList>();
			
			diveDataBase db = new diveDataBase(LogDive.this);
			db.open();
			
			
			//diveDataBase db = new diveDataBase(this);
			//db.open();
			cursor = db.getCursorData();
			if(cursor!=null&&cursor.getCount()>0){
				
				Log.d(TAG, "CURSOR SIZE IN GET AUTOTEXTVIEW: "+cursor.getCount());
				
				for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
				
					//for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
				
				String diveSite = cursor.getString(cursor.getColumnIndexOrThrow(diveDataBase.KEY_DIVESITE));
				if (diveSite==null||diveSite.isEmpty()){
					diveSite="No Entry";
					
				}
				diveSites.add(diveSite);
				
				String diveLocation = cursor.getString(cursor.getColumnIndexOrThrow(diveDataBase.KEY_DIVELOCATION));
				if (diveLocation==null||diveLocation.isEmpty()){
					diveLocation="No Entry";
					
				}
				diveLocations.add(diveLocation);
				
				
				String diveBuddie = cursor.getString(cursor.getColumnIndexOrThrow(diveDataBase.KEY_DIVEBUDDY));
				
				if (diveBuddie==null||diveBuddie.isEmpty()){
					diveBuddie="No Entry";
					
				}
				diveBuddies.add(diveBuddie);
				
				String diveCenter = cursor.getString(cursor.getColumnIndexOrThrow(diveDataBase.KEY__DIVECENTER));
				if (diveCenter==null||diveCenter.isEmpty()){
					diveCenter="No Entry";
					
				}
				diveCenters.add(diveCenter);
				
				
				
				
				}//for
			}else{
				
				Log.d(TAG, "DB EMPTY cursor: " +cursor.getCount() );
			}
				
			//add all lists to Arraylist 
			lists.add(diveSites);
			lists.add(diveLocations);
			lists.add(diveBuddies);
			lists.add(diveCenters);
			
		
			
			
			
			return lists;
		}//doinBgrd

		@Override
		protected void onPostExecute(ArrayList lists) {
			// set AutoViewTV adapters with araryList strings and enable the views
			
			super.onPostExecute(lists);
			
			
			//lists 0=diveSites lists 1=diveLocations lists 2=divebuddies lists 3=diveCenters
			ArrayAdapter<String> sitesAdapter = new ArrayAdapter(LogDive.this, android.R.layout.simple_list_item_1, diveSites );
			ArrayAdapter<String> locationsAdapter = new ArrayAdapter(LogDive.this, android.R.layout.simple_list_item_1, diveLocations );
			ArrayAdapter<String> buddyAdapter = new ArrayAdapter(LogDive.this, android.R.layout.simple_list_item_1, diveBuddies);
			ArrayAdapter<String> centersAdapter = new ArrayAdapter(LogDive.this, android.R.layout.simple_list_item_1, diveCenters);
			
			
			//set autocompleteViews adapters , setThreshold activates autocomple after 1st charcgter
			LogDive.this.diveBuddy.setAdapter(buddyAdapter);
			LogDive.this.diveLocation.setAdapter(locationsAdapter);
			diveLocation.requestFocus();
			LogDive.this.diveSite.setAdapter(sitesAdapter);
			LogDive.this.diveCenter.setAdapter(centersAdapter);
			
			//enable the autocomplete views fro user interaction
			LogDive.this.diveCenter.setEnabled(true);
			LogDive.this.diveCenter.setThreshold(1);
			
			LogDive.this.diveSite.setEnabled(true);
			LogDive.this.diveSite.setThreshold(1);
			
			LogDive.this.diveLocation.setEnabled(true);
			LogDive.this.diveLocation.setThreshold(1);
			
			LogDive.this.diveBuddy.setEnabled(true);
			LogDive.this.diveBuddy.setThreshold(1);
			
		
			if(pd.isShowing()){
				pd.dismiss();
			}
			
		}
		
		//dismiss dialog
		
		
		
		
		
		
		
		
		
	}//getAutoComplTV
	
	
	private void checkIfFirstEntry() {
		
		// if first entry present dialog that says to add here current cummaltive bottom time to bootom time and current dive no
		diveDataBase db = new diveDataBase(this);
		db.open();
		Cursor cursor = db.getCursorData();
		if(cursor==null || cursor.getCount()==0){
			// get customised array adoater list
			Dialog d = new Dialog(this);
			d.setTitle("Your First Entry");
			TextView tv = new TextView(this);
			tv.setText("If this is not your first dive make sure you enter \n your current dive number and cummulative \n " +
					" dive time (in minutes) to date in the Bottom Time field!! \n" +
					"(Press back button to continue) ");
			d.setContentView(tv);
			d.show();
			}
		db.close();
		
	}//end check for first dive





	private void setImageViewFromSavedInstance(String imagePathway) {
		
		/*
		 * Called form onCreate() savedInstanceState if statement
		 * 
		 * if Maps or FB called we save the image in overRide onSaveInstanceState, get back in onCreate
		 *and set the image back to ImageView so it can be saved
		*/
		
		String savedImagePathWay = imagePathway;
		File imagePathFile = new File(savedImagePathWay);
		try {

			final int IMAGE_MAX_SIZE = 3000;
			FileInputStream streamIn = new FileInputStream(imagePathFile);

			// important to reduce size of image before it is loaded into memory
			// and then resized, otherwise will
			// get out of memory error

			// Decode image size and setInJBounds = true to avoid auto memory
			// allocation for large image
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(streamIn, null, o);
			streamIn.close();

			//by setting m.pow(scale, 3.85) we get a org image of 2500:1400 beofre loaded and when loaded we get 426:240, no need fro resize method call here
			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 3.9)) > IMAGE_MAX_SIZE) {
				scale++;
			}

			// get orginal width of image before loaded into memory
			Log.d("VIEW DIVE", "scale = " + scale + ", orig-width: " + o.outWidth
					+ "  orig-height: " + o.outHeight);
			//Toast.makeText(getApplicationContext(), "Orginal Image size from DB: width: "+ o.outWidth + "/height: " + o.outHeight , Toast.LENGTH_LONG).show();

			Bitmap b = null;
			streamIn = new FileInputStream(imagePathFile);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target, inSampleSize loads the image into memor
				// by a factor of its integer value
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				// Decode bitmap with inSampleSize set
				o.inJustDecodeBounds = false;

				b = BitmapFactory.decodeStream(streamIn, null, o);
				//makeToast("Image after sacling and now loading: width: "+ o.outWidth + "/height: " + o.outHeight );
				//resizedImage = reSizeImage(b); this is blowing uo and alreadt samll image from
				//org size 2500:1400 to 73:44, then blown back up in resize does not look good

				streamIn.close();
				//b.recycle();
				System.gc();
			}// end if scale
			// Bitmap bitmap = BitmapFactory.decodeStream(streamIn);// retrun
			// null
			// if
			// cant
			// convert

			// streamIn.close();
			if (b != null) {
				
				diveSitePicture.setImageBitmap(b);
				
			} else {
				// diveSitePicture2.setBackgroundResource(R.drawable.camera);
//				

				// diveSitePicture2.setBackgroundDrawable(R.drawable.camera);
				Log.d(TAG, "494 No image saved from returning form Maps of FB");
			}

		} // end try
		catch (IOException exe) {
			exe.printStackTrace();

		} catch (OutOfMemoryError exc) {
			exc.printStackTrace();
			// Toast.makeText(this, "Something went wrong! Try again...",
			// Toast.LENGTH_SHORT).show();
		} catch (NullPointerException nullpoint) {
			nullpoint.printStackTrace();

		}// end try catch

		
		
	}//end setImageViewFromSavedInastance





	// date picker dialog
	protected Dialog onCreateDialog(int id) {

		return new DatePickerDialog(this, mDateSetListener, yr, month, day);
	}// end onCreateDialog

	private DatePickerDialog.OnDateSetListener mDateSetListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			yr = year;
			month = monthOfYear;
			day = dayOfMonth;
			diveDate.setText((month + 1) + "/" + day + "/" + year);

		}// end onDateSet
	};// end datepicker listener

	
	private void intilizeWidgets() {
		// intilize all widgets form XML logdive and add listeners

		/*AutoCompleteTextView diveLocation, diveSite,diveBuddy, diveCenter
		 * Disable these views until the arrayadpter is set to AutoTextView in aynsch task
		 * 
		 */
		
		handler = new Handler();
		diveDepthEditText = (EditText)findViewById(R.id.et_diveDepth);
		viewLog = (Button) this.findViewById(R.id.button_ViewSavedDive);
		viewLog.setOnClickListener(this);
		saveLog = (Button) this.findViewById(R.id.button_SaveDiveLogEntry);
		saveLog.setOnClickListener(this);
		diveRating = (RatingBar) this.findViewById(R.id.diveratingBar1);
		diveRating.setOnRatingBarChangeListener(this);
		
		this.bottomTime = (EditText) this.findViewById(R.id.et_bottomTime);
		
		this.diveBuddy = (AutoCompleteTextView ) this.findViewById(R.id.et_diveBuddy);
		diveBuddy.setEnabled(false);
		
		this.diveCenter = (AutoCompleteTextView ) this.findViewById(R.id.et_diveCenter);
		diveCenter.setEnabled(false);
		
		this.diveLocation = (AutoCompleteTextView ) this
				.findViewById(R.id.et_locationLogDive);
		diveLocation.setEnabled(false);
		
		this.diveNumber = (EditText) this.findViewById(R.id.et_diveNumber);
		
		
		this.diveSite = (AutoCompleteTextView ) this.findViewById(R.id.et_diveSiteLogDive);
		diveSite.setEnabled(false);
		
		
		
		this.endBar = (EditText) this.findViewById(R.id.et_endPressure);
		this.startBar = (EditText) this.findViewById(R.id.et_startPressure);
		this.vizibilty = (EditText) this.findViewById(R.id.et_visibilty);
		this.waterTemp = (EditText) this.findViewById(R.id.et_waterTemp);
		this.diveSitePicture = (ImageButton) this
				.findViewById(R.id.ib_logDiveImage);
		diveSitePicture.setOnClickListener(this);
		this.comments = (EditText) this.findViewById(R.id.et_DiveComments);
		this.activateMap = (ImageButton) this
				.findViewById(R.id.ib_findLocationOnMap);
		activateMap.setOnClickListener(this);

		// spinners CertLevel or drop downs
		this.diveConditions = this.getResources().getStringArray(
				R.array.diveConditions_arrays);
		conditions = (Spinner) this.findViewById(R.id.spinner_diveConditons);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, diveConditions);
		conditions.setAdapter(adapter);
		conditions.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// get conditions form drop box spinner
				condtionIndex = arg0.getSelectedItemPosition();
				conditionChoice = diveConditions[condtionIndex];
				/*Toast.makeText(getBaseContext(),
						"" + diveConditions[condtionIndex] + "",
						Toast.LENGTH_SHORT).show();*/

			}// end onitemselected for conditions spinner

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// this must be implementd in case nothingis selec ted
				Toast.makeText(getBaseContext(), "Nothing Selcetd",
						Toast.LENGTH_SHORT);

			}// end onNOthingSeltedd

		});// end new onItemSelceted

	}// end initialize widgets

	@Override
	public void onClick(View arg0) {
		// // handle all clickable events
		switch (arg0.getId()) {

		case R.id.ib_findLocationOnMap:
			// get map location as string
			startActivityForResult(new Intent(
					"android.intent.action.MAPDIVELOCATION"), 3);// 3 as 1 and 2
																	// already
				
			//saveDataWhileUsingMap();// saave data first
			break;

		case R.id.button_SaveDiveLogEntry:
			try {
				
				
				checkForNullData();
				diveNum = Integer.valueOf(diveNumber.getText().toString());
				
				this.diveBuddyString = diveBuddy.getText().toString();
				this.diveCenetrString = diveCenter.getText().toString();
				this.diveDateString = diveDate.getText().toString();
				this.diveLoctionString = diveLocation.getText().toString();
				
				this.diveSiteString = diveSite.getText().toString();
				this.endBars = Integer.parseInt(endBar.getText().toString());
				this.startBars = Integer
						.parseInt(startBar.getText().toString());
				this.viz = Integer.parseInt(vizibilty.getText().toString());
				this.waterTemperature = Integer.parseInt(waterTemp.getText()
						.toString());
				comment = comments.getText().toString();
				diveDepth = Integer.parseInt(diveDepthEditText.getText().toString());
				
				String BT = bottomTime.getText()
						.toString();
				bottomDiveTime = Integer.valueOf(bottomTime.getText()
						.toString());
				
			} catch (NumberFormatException ne) {
				makeToast(ne.getMessage());
				Log.d(TAG, "656: Error SAVING DATA : "+ ne.getMessage().toString());
			} catch (Exception e) {
				makeToast(e.getMessage());
			}

			Log.d(TAG, "String path in Save Button: "+ savedImagePath);
			// check first if photo taken
			if (savedImagePath.isEmpty()) {
				savedImagePath = "No Image Selected";
				makeToast("No picture selected for this dive!");
			}

			// save image
			
			boolean didItWork = true;
			try {

				// db instance divedateBase and write to it
				diveDataBase entry = new diveDataBase(this);
				entry.open();
				entry.createEntry( diveRate, bottomDiveTime,
						diveBuddyString, diveCenetrString, diveLoctionString,
						diveNum, diveSiteString, endBars, startBars, viz,
						waterTemperature, diveDateString, comment,
						conditionChoice, savedImagePath, diveDepth);
entry.close();
this.saveLog.setTextColor(Color.GRAY);
saveLog.setEnabled(false);

			} catch (Exception e) {
				// display failed dialog message
				didItWork = false;
				e.printStackTrace();
				String error = e.getMessage();
				Dialog d = new Dialog(this);
				d.setTitle("Saving to DB");
				TextView tv = new TextView(this);
				tv.setText("Dive did not save, somethong went wrong writong to DB!\nError: "
						+ error);
				d.setContentView(tv);
				d.show();

			} finally {
				// display success message dialog
				if (didItWork) {
//					Dialog d = new Dialog(this);
//					d.setTitle("Saving to DB");
//					TextView tv = new TextView(this);
//					tv.setText("\nDive Succesfully Logged!!\nPress back to continue.");
//					
//					d.setContentView(tv);
//					d.show();
					displayDialog();
					

				}// end finnally
			}// end didItWork
				// log the values of all variables before been sent to SQL
			Log.d(TAG, "All data for SQL: Dive Rating: " + this.diveRate
					+ " Bottom Time: " + bottomDiveTime + "Buddy: "
					+ diveBuddyString + "\n" + "Dive Center: "
					+ diveCenetrString + "Dive Loaction: " + diveLoctionString
					+ " Dive Number: " + diveNum + "\n" + "Dive Site: "
					+ diveSiteString + "End Bar: " + endBars + " Start Bar: "
					+ startBars + "\n" + "Vizibilty: " + viz + "Water temp: "
					+ waterTemperature + " Dive Date: " + this.diveDateString
					+ "\n" + "Comments: " + comment + "Conditions: "
					+ this.conditionChoice + " Condtions Index: "
					+ this.condtionIndex + "Image saved to SD path: "
					+ this.savedImagePath+ "dive Depth: "+diveDepth);

			break;

		case R.id.button_ViewSavedDive:
			//call face book dialog to share data from dive
			//check first if user has android FB app, if not cant use dialog andmust use feed instead
			
			
			
			AlertDialog.Builder build1 = null;
			AlertDialog dialog1 = null;
			build1 = new AlertDialog.Builder(this);
			build1.setCancelable(true);
			build1.setMessage("Do you want to upload your dive site picture \n" +
					" or a pictiure from the Gallery on your Facebook post?");
			build1.setPositiveButton("My Picture",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// call facebook method and pass flag to use method to post image from SD card
							loadMyPicture=true;
							shareDialogFacebook(loadMyPicture);
							
							
						}// end on click
					});// end pos button take photo

			// get photo from SD card option
			build1.setNegativeButton("Gallery Picture",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// call facebook method and pass flag to use method to post image from URLs provided in method
							loadMyPicture=false;
							shareDialogFacebook(loadMyPicture);

						}// end onclick
					});// end pos button take photo
			dialog1 = build1.create();
			dialog1.setTitle("No Logged Dives");
			dialog1.show();
			
			
			break;

		case R.id.dp_diveDate:
			// activate dialog to get date of dive
			showDialog(DATE_DIALOG_ID);
			break;

		case R.id.ib_logDiveImage:
			// take photo
			// access the phones camera with a new Intent get data ret from the
			// photo
			// use alert dialog to ask to take a photo or get form SD card
			AlertDialog.Builder build = null;
			AlertDialog dialog = null;
			build = new AlertDialog.Builder(this);
			build.setCancelable(true);
			build.setMessage("Take a photo or choose from SD card kind sir.");
			build.setPositiveButton("Take Photo",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// take a photo with a new camera intent
							usePhoneCamera = new Intent(
									android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(usePhoneCamera, cameraData);

						}// end on click
					});// end pos button take photo

			// get photo from SD card option
			build.setNegativeButton("Get Photo",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent getPhotoFromGallery = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(getPhotoFromGallery, 2);

						}// end onclick
					});// end pos button take photo
			dialog = build.create();
			dialog.setTitle("Photo Type");
			dialog.show();
			break;

		}// end switch

	}// end onClick for clickable events
	
	
	
	private void displayDialog() {
		
		// called form on`Clock saved finally()
		//  when all data saved correctly display and navigate back to main menu or 
		// list of dives
	


			AlertDialog.Builder build = null;
			AlertDialog dialog = null;
			build = new AlertDialog.Builder(this);
			build.setCancelable(true);
			build.setMessage("Dive Saved Succesfully!!");
			build.setPositiveButton("Main Menu",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// go to log a dive
							Intent i = new Intent("android.intent.action.MAINMENU3");
							startActivity(i);
							overridePendingTransition(R.anim.cardin, R.anim.cardout);
							
							// free up meemory
							if(photo!=null){
								photo.recycle();
							}
							if(photoBitmap!=null){
								photoBitmap.recycle();
							}
							if(diveSites!=null){
								diveSites=null;
							}
							if(diveLocations!=null){
								diveLocations=null;
							}
							if(diveBuddies!=null){
								diveBuddies=null;
							}
							if(diveCenters!=null){
								diveCenters=null;
							}
							
							System.gc();
							finish();
							
							
							//System.gc();

						}// end on click
					});// end pos button take photo

			// get photo from SD card option
			build.setNegativeButton("Review Dives",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Intent intt = new Intent(LogDive.this, ViewListOfDivesSearch.class );
							startActivity(intt);
							overridePendingTransition(R.anim.cardin, R.anim.cardout);
							
							
							
							// free up meemory
							if(photo!=null){
								photo.recycle();
							}
							if(photoBitmap!=null){
								photoBitmap.recycle();
							}
							if(diveSites!=null){
								diveSites=null;
							}
							if(diveLocations!=null){
								diveLocations=null;
							}
							if(diveBuddies!=null){
								diveBuddies=null;
							}
							if(diveCenters!=null){
								diveCenters=null;
							}
							
							System.gc();
							finish();
							
							
							
							
							//finish();
							
							
							System.gc();

						}// end onclick
					});// end pos button take photo
			dialog = build.create();
			dialog.setTitle("Log Dive");
			dialog.show();

		}// end display dalog
		
	





	//handle state changes of Session for facebook
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i(TAG, "Logged in...");
	    } else if (state.isClosed()) {
	        Log.i(TAG, "Logged out...");
	    }
	}
	
	
	
	private void shareDialogFacebook(final boolean useDiveSiteImage){
		
		//post to facebook using either Request upload photo or ShareDialog FB object
		//depenidng on users choice from btton click and dialog 
		
		
		//check for internet connectivty
		isInterNetConnected=checkInternetConnection();
		if(!isInterNetConnected){
			alertDialog();
		}else if(isInterNetConnected){
			Log.d("LogDive Facebook Class", "Internet connection established");
		}
		
		
		
		//get data first
		checkForNullData();
		try{
		diveNum = Integer.parseInt(diveNumber.getText().toString());
		bottomDiveTime = Integer.parseInt(bottomTime.getText()
				.toString());
		if( bottomDiveTime >=0){
			//bottomDiveTime=0;
		}else{
			bottomDiveTime=0;
		}
		
		diveLoctionString = diveLocation.getText().toString();
		
		diveSiteString = diveSite.getText().toString();
		
		viz = Integer.parseInt(vizibilty.getText().toString());
		if(viz >=0){
			//bottomDiveTime=0;
		}else{
			viz=0;
		}
		waterTemperature = Integer.parseInt(waterTemp.getText()
				.toString());
		if( waterTemperature >=0){
			//bottomDiveTime=0;
		}else{
			waterTemperature=0;
		}
		
		diveDepth = Integer.parseInt(diveDepthEditText.getText().toString());
		if( diveDepth >=0){
			//bottomDiveTime=0;
		}else{
			diveDepth=0;
		}
		diveRate = this.diveRating.getRating();
		Log.d(TAG, "FB varicbles : Dive No: "+ diveNum +" Dive Site "+ diveSiteString);
		}catch(Exception e){
			Log.d(TAG, "Exception on fb details: " + e.getMessage() + " trace: " );
			e.printStackTrace();
			Log.d(TAG, "FB varicbles : Dive No: "+ diveNum +" Dive Site "+ diveSiteString +"" +
					" Depth "+ diveDepth + " rate " +diveRate+" temp "+waterTemperature+" viz "+viz);
			//Toast.makeText(getApplicationContext(), "Please compelete all details and save first!!", Toast.LENGTH_LONG);
			//return;
		}
		
	
		
		
			   
		Session.openActiveSession(this, true, new Session.StatusCallback() {
		//Session.StatusCallback callback = new  StatusCallback(){
			@Override
			public void call(final Session session, SessionState state, Exception exception) {
				//callback when session changes state
				
				
				
				
				
				//link for this http://stackoverflow.com/questions/15143951/get-read-and-publish-permissions-in-one-request
				
				
				//get a string array of URL for photos from moonetcallans.com
				//get a random drawable
					String[] imageSelection= {"http://i.stack.imgur.com/yJm5R.jpg?s=128&g=1",
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image42Thump.jpg",
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image40Thump.jpg",
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image50Thump.jpg", 
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image51Thump.jpg",
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image44Thump.jpg",
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image1Thump.jpg",
							"http://www.mooneycallans.com/images/Gallery/Images%20Converted/image43Thump.jpg",
							"http://www.mooneycallans.com/images/Gallery/image38.jpg",
							"http://www.mooneycallans.com/images/Gallery/image51.jpg",
							"http://www.mooneycallans.com/images/Gallery/OzTrip/OzPicsThump46.jpg"};
					
					 					Random whichImage = new Random();
					//int theImage = whichImage.nextInt(imageSelection.length);
					final String theImageURL = imageSelection[whichImage.nextInt(imageSelection.length)];
					
					
					
					//if user chooses to post site picture form dive log
					//try to get a file:: protocoll URL from the savediamged pathway to pass to set pic on FB
					File imagePathFile = new File(savedImagePath);
					//final URL userImageURL;
					
					
					 
					
					
					//determone which image to post according to boolean flag passed to methdo
					if(useDiveSiteImage){
					
						Log.d(TAG, "FB booean useDiveSiteImage is: " +useDiveSiteImage);
					 /*Upload the users Dive Site Image by using the Request FB class
					 *we must first upload the image (File) to users album using a Request FB SDK object, 
					 *must also request more permissions to publish 
					 *The image will auto save to Dive App alvum on FB and publih pic plus dive details to wall
					 */
					 
					 
					if(session.isOpened() ){
						   
						
						//is called after the callback method has been intilised
						
	 		          Log.d(TAG, "Session permissions are: " +session.getPermissions().toString());
					//Toast.makeText(getApplicationContext(), "Session is open FOR UPLOAD PHOTO", Toast.LENGTH_SHORT).show();
					
					Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
						//callback handles response from Request photo upload
						Object graphResponse=null;
					    @Override
					    public void onCompleted(Response response) {
					    
					    	
					    	//turn of dialog if showing
					    	if(uploadUserPicToFacebookDialog.isShowing()){
					    		uploadUserPicToFacebookDialog.dismiss();
					    	}
					    	
					    	//request addtional permissions to upload photos
							 Session.NewPermissionsRequest newPermissionsRequest = new Session
								      .NewPermissionsRequest(LogDive.this, Arrays.asList("publish_actions", "publish_stream"));
								    session.requestNewPublishPermissions(newPermissionsRequest);
								    
					    	//create callback to catch errors and the url of image via graphResponse object
					    	//sfatey check
								    executeUploadOnce=1;//when not euql to zero the upload call will only occur once
								    
								    Log.d(TAG, "FLAG executePhotoUploadOnce should be= 1 is :" +executeUploadOnce);
					    	if(isFinishing()){
					    		return;
					    	}
					        if (response.getError() != null) { 
					            //if failed posting post error
					        	Log.d(TAG, "FB upload image error: " + response.getError() );
					        	
					        } 
					        try{
					        //get id of photo and concentate to string URL
					        graphResponse = response.getGraphObject().getProperty("id");
					        
					        Log.d(TAG, "FB Graph Response object ID of photo: " + graphResponse);
					        
					        }catch(Exception ex){
					        	ex.printStackTrace();
					        }
					        if(graphResponse == null || !(graphResponse instanceof String) ||
					        	TextUtils.isEmpty((String) graphResponse)){
					    		Log.d(TAG, "FB imageFile failed photo upload/no response");
					    		Toast.makeText(getApplicationContext(), "Not Posted!! Try again later.. ", Toast.LENGTH_SHORT).show();
					        }//end if grpahRes ==null
					    	else{
					    		//if load succesfull, fbPhotoURL vcan now be passed to share dialog object setPicture() to load dive site image
					    		fbPhotoURL = "https://www.facebook.com/photo.php?fbid=" +graphResponse;
					    		Log.d(TAG, "SUCCES: FB URL of uploaded IMAGE IS : " + fbPhotoURL);
					    		Log.d(TAG, "FLAG executePhotoUploadOnce should be= 1 is :" +executeUploadOnce);
					    		
					    		
									Log.d(TAG, "Session permissions are: " +session.getPermissions().toString());
									Toast.makeText(getApplicationContext(), "Posted to Facebook!! ", Toast.LENGTH_SHORT).show();
								
									return;
					    	}//end else grphrespoinse not null
							
					    } //end onComplete  
					};//end callback method  from uplOadPhotoRequest


					//first try to publish photo and message to wall, thencallback method above will be called
					//image path file a file rep of stroed image user took
					
					
					if(executeUploadOnce==0){
						
						Bitmap facebookBitmapImage=null;
						//when using loading by file use paramter imagePathFile
						
						//get resized bitmap image from SD card
						try{
						facebookBitmapImage = resizedImageForFacebookPost();
						Log.d(TAG, "FCEBOOK resized photo is: " + facebookBitmapImage );
						}catch(Exception e){
							e.printStackTrace();
							
						}
						
						
						
				try {
					Log.d(TAG, "FLAG executePhotoUploadOnce should be= 0 is :" +executeUploadOnce);
					Request request = Request.newUploadPhotoRequest(session, facebookBitmapImage, uploadPhotoRequestCallback);
					Bundle parameters = request.getParameters(); // <-- THIS IS IMPORTANT
 					//add data image path file to paramters in method, and execute
 					// add more params here
					String message= "Dive No: " + diveNum + "- at....." +diveSiteString +".\n\n"+bottomDiveTime+ " minute dive at "
 					+ waterTemperature +" degrees celcuis, vizibilty " 
							+viz+ " meters. Depth:  "+diveDepth+ " meters.\n Dive Rating: " +diveRate+ "/5!";
		 
 					parameters.putString("name", message);//also try key message
 					
 					request.setParameters(parameters);
					Log.d(TAG, "FB IMagePathe FILE TO REQUEST UPLOADPHOTO IS: " + imagePathFile);
 					
 					//execute photo upload by calling Request aycnh task so not freeze up GUI
 					request.executeAsync();
 					
 					//start dialog here and end in onComplete() of callback method above uploadPhotoRequestCallback
 					//show dialog while loading data
 					
 					uploadUserPicToFacebookDialog = new ProgressDialog( LogDive.this, ProgressDialog.STYLE_HORIZONTAL);
 					uploadUserPicToFacebookDialog.setMessage("Uploading your Pic to Facebook....");
 					uploadUserPicToFacebookDialog.show();
				
				
				} 
				/*
				catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					//add alert dialog here
					// get customised array adoater list
					Dialog d = new Dialog(LogDive.this);
					d.setTitle("No Site Picture Found!\n Go take one..");
					TextView tv = new TextView(LogDive.this);
					tv.setText("No dive site picture found!!");
					d.setContentView(tv);
					d.show();
					//}
					
					e1.printStackTrace();
				}*/
				//end try catch upload photo call
				catch(Exception e){
					e.printStackTrace();
					Dialog d = new Dialog(LogDive.this);
					d.setTitle("No Site Picture Found or error loadng!\n Go take one..");
					TextView tv = new TextView(LogDive.this);
					tv.setText("No dive site picture found!!");
					d.setContentView(tv);
					d.show();
				}
					
					}//end if executeUploadOnce=0
					
					}//end is session open
					 
					}//end if boolean uploadUserImage
					
					
					if(!useDiveSiteImage){
					
						//use share diaog box to upload a URL pic via setPicture()
					Log.d(TAG, "FB booean useDiveSiteImage is: " +useDiveSiteImage);
					 
					

					 
				if(session.isOpened() ){
					
					//Toast.makeText(getApplicationContext(), "Session is open", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "FACEBOOK SESSION IS OPEN");
					//make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, Response response) {
							// callback after Grap API response with user object
							if(user != null){
								
								
								//test links to images on fb
								
								
								//try to publish to wall via FB app, if app not installed via webview
								if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
					                    FacebookDialog.ShareDialogFeature.SHARE_DIALOG)){
									Toast.makeText(getApplicationContext(), "Publishing via app", Toast.LENGTH_SHORT).show();
								FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(LogDive.this)
								.setLink("https://play.google.com/store/apps/details?id=com.mooney.diveapp&hl=en")//developers.facebook.com/android
								.setRequestCode(4)
								.setApplicationName("Dive App")
								.setName("Dive No: " + diveNum + "- at....."+diveSiteString)
								.setDescription(bottomDiveTime+ " minute dive at "+ waterTemperature +
										" degrees celcuis, vizibilty "+viz+ " meters. Depth:  "
										+diveDepth+" meters. Dive Rating: "+diveRate+"/5!")
										.setPlace(diveLoctionString)
										.setPicture(theImageURL)
										
								.build();
							
								//URL to google play dive apps https://play.google.com/store/search?q=dive&c=apps&hl=en
								//Toast.makeText(getApplicationContext(), "Saved Image Path: "+ savedImagePath, Toast.LENGTH_SHORT).show();
								//picture is url (image address) from avatar on my stack overflow accout 
								
								uiHelper.trackPendingDialogCall(shareDialog.present());
								}else{
									//publish the post using the feed dialog
									Toast.makeText(getApplicationContext(), "Publish via FeddDialog", Toast.LENGTH_SHORT).show();
									publishFeedDialog();
									
								}//end else
								
								
							}//edn if user!=null
							
						}//edn onCmpleted
					});//end Request.execute
				}//edn is session is open
				else if(!session.isOpened()){
					//if session nt open we must login to FB and get authorisation
					//Toast.makeText(getApplicationContext(), "FB not open!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "FACEBOOK SESSION NOT OPEN");
					//session.openActiveSession(getParent(), true, callback)
					fb.authorize( LogDive.this, new DialogListener(){

						@Override
						public void onComplete(Bundle values) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onComplete in FB Authorise", Toast.LENGTH_SHORT).show();
							
						}

						@Override
						public void onFacebookError(FacebookError e) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onFaceBookError in FB Authorise", Toast.LENGTH_SHORT).show();
							
						}

						@Override
						public void onError(DialogError e) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onError in FB Auhorise", Toast.LENGTH_SHORT).show();
							
						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onCancel in FB Authorise", Toast.LENGTH_SHORT).show();
							
						}
						
					});
				}
				
					}//edn if !useDiveSiteImage and upload a URL link in shareDialog
					
			}//end call
			
		 });//end Session.openActiveSession
			
		
	}//end share dialog on facebook
	
	
	
	private void publishFeedDialog() {
		/* if cant use share dialog as user does not haveFB android app
		*use a web dialog to post the feed
		*/
		
		//check for internet connectivty
		isInterNetConnected=checkInternetConnection();
		if(!isInterNetConnected){
			alertDialog();
		}else if(isInterNetConnected){
			Log.d("LogDive Facebook Class", "Internet connection established");
		}
		
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle("Facebook APp Required");
		alert.setMessage("You need to insatll FB app to your phone!");
		alert.setButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// quit this activity and return to logdve to avoid null pointer http exceptions
				//finish();
				
			}
		});
		
		alert.show();
		//get data first
		checkForNullData();
		try{
		diveNum = Integer.parseInt(diveNumber.getText().toString());
		bottomDiveTime = Integer.parseInt(bottomTime.getText()
				.toString());
		
		diveLoctionString = diveLocation.getText().toString();
		
		diveSiteString = diveSite.getText().toString();
		
		viz = Integer.parseInt(vizibilty.getText().toString());
		waterTemperature = Integer.parseInt(waterTemp.getText()
				.toString());
		
		diveDepth = Integer.parseInt(diveDepthEditText.getText().toString());
		diveRate = this.diveRating.getRating();
		}catch(Exception e){
			Log.d(TAG, "Exception on fb details: " + e.getMessage());
			
			
		}
		
		
		Toast.makeText(getApplicationContext(), "You need to download the FB app on your mobile!", Toast.LENGTH_LONG).show();
		//start a session
		 Session.openActiveSession(LogDive.this, true, new Session.StatusCallback() {
			
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				//callback when session changes state
				
				
				/*
				//get access token
				String token = fb.getAccessToken();
				long token_expires = fb.getAccessExpires();
				
				if (token != null && token_expires != -1)
                {
                    fb.setAccessToken(token);
                    fb.setAccessExpires(token_expires);
                }
				*/
				if(session.isOpened() ){
					Toast.makeText(getApplicationContext(), "Session is open. Publishing on web dialog....", Toast.LENGTH_SHORT).show();
					//make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						
						


						@Override
						public void onCompleted(GraphUser user, Response response) {
							// callback after Grap API response with user object
							if(user != null){
								
								//try to publish to wall via FB app, if app not installed via webview
								Log.e("FACEBOOK", "post link on the wall ...");
								Bundle params = new Bundle();
							    params.putString("name", "Dive App");
							    params.putString("caption", "Dive site "+ diveSiteString+" at "+ diveLoctionString);
							    params.putString("description",  bottomDiveTime+" minute dive at "+ waterTemperature +
											"degrees celcuis, vizibilty "+ viz+ "depth:  "
											+ diveDepth+". I rate this dive a "+ diveRate+"/5!");
							    params.putString("link", "https://developers.facebook.com/android");
							    params.putString("picture", "http://i.stack.imgur.com/yJm5R.jpg?s=128&g=1");
							    
							    //image is image address from my avatar profile on stack overflow
							    
							    WebDialog feedDialog = (
							        new WebDialog.FeedDialogBuilder(LogDive.this,
							            Session.getActiveSession(),
							            params))
							        .setOnCompleteListener(new OnCompleteListener() {

							            @Override
							            public void onComplete(Bundle values,
							                FacebookException error) {
							                if (error == null) {
							                    // When the story is posted, echo the success
							                    // and the post Id.
							                    final String postId = values.getString("post_id");
							                    if (postId != null) {
							                        Toast.makeText(LogDive.this,
							                            "Succes! Posted story, id: "+postId,
							                            Toast.LENGTH_SHORT).show();
							                    } else {
							                        // User clicked the Cancel button
							                        Toast.makeText(LogDive.this, 
							                            "Publish cancelled", 
							                            Toast.LENGTH_SHORT).show();
							                    }
							                } else if (error instanceof FacebookOperationCanceledException) {
							                    // User clicked the "x" button
							                    Toast.makeText(LogDive.this, 
							                        "Publish cancelled! Download Facebook App and try again!!", 
							                        Toast.LENGTH_LONG).show();
							                } else {
							                    // Generic, ex: network error
							                    Toast.makeText(LogDive.this, 
							                        "Error posting story", 
							                        Toast.LENGTH_SHORT).show();
							                }
							            }

										

							        })
							        .build();
							    try{
							    feedDialog.show();
							    }catch(Exception exe){
							    	exe.printStackTrace();
							    }
								
							}//edn if user!=null
							
						}//edn onCmpleted
					});//end Request.execute
				}//edn is session is open
				else if(!session.isOpened()){
					//if session nt open we must login to FB and get authorisation
					Toast.makeText(getApplicationContext(), "FB not open!", Toast.LENGTH_SHORT).show();
					//session.openActiveSession(getParent(), true, callback)
					fb.authorize( LogDive.this, new DialogListener(){

						@Override
						public void onComplete(Bundle values) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onComplete in FB Authorise", Toast.LENGTH_SHORT).show();
							
						}

						@Override
						public void onFacebookError(FacebookError e) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onFaceBookError in FB Authorise", Toast.LENGTH_SHORT).show();
							
						}

						@Override
						public void onError(DialogError e) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onError in FB Auhorise", Toast.LENGTH_SHORT).show();
							
						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "onCancel in FB Authorise", Toast.LENGTH_SHORT).show();
							
						}
						
					});
				}
			}//end call
		 });//end Session.openActiveSession
		 
		
	}//end publishFeedDialog


	private void checkForNullData() {
		// if no data entered assign some so saves corretly to DB
		
		if (bottomTime.getText().toString()==null|| bottomTime.getText().toString().isEmpty()){
			bottomTime.setText("0");
		}
		if (diveBuddy.getText().toString()==null || diveBuddy.getText().toString().isEmpty()){
			diveBuddy.setText("No Entry");
		}
		if (diveCenter.getText().toString()==null || diveCenter.getText().toString().isEmpty()){
			diveCenter.setText("No Entry");
		}
		if (diveDate.getText().toString()==null || diveDate.getText().toString().isEmpty()){
			diveDate.setText("No Entry");
		}
		if (diveLocation.getText().toString()==null || diveLocation.getText().toString().isEmpty()){
			diveLocation.setText("No Entry");
		}
		if (diveSite.getText().toString()==null || diveSite.getText().toString().isEmpty()){
			diveSite.setText("No Entry");
		}
		if (endBar.getText().toString()==null || endBar.getText().toString().isEmpty()){
			endBar.setText("0");
		}
		if (startBar.getText().toString()==null || startBar.getText().toString().isEmpty()){
			startBar.setText("0");
		}
		if (vizibilty.getText().toString()==null || vizibilty.getText().toString().isEmpty()){
			vizibilty.setText("0");
		}
		if (waterTemp.getText().toString()==null || waterTemp.getText().toString().isEmpty()){
			waterTemp.setText("0");
		}
		if (comments.getText().toString()==null || comments.getText().toString().isEmpty()){
			comments.setText("No Entry");
		}
		if (diveDepthEditText.getText().toString()==null || diveDepthEditText.getText().toString().isEmpty()){
			diveDepthEditText.setText("0");
		}
		
	}//end checkForNullData

	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		
		
		// method checks data returned form camera via startActivityForResult
		super.onActivityResult(requestCode, resultCode, data);
		
		
		// check data integrity against static int from activity class
		fb.authorizeCallback(requestCode, resultCode, data);
			
			Session.getActiveSession().onActivityResult(this, requestCode,
	                resultCode, data);
			
			uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
			        @Override
			        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
			            Log.e("Activity", String.format("Error: %s", error.toString()));
			            Toast.makeText(getApplicationContext(), "Ergg. Something went wrong. Check your interet connection and" +
			            		" make sure you have FB app installed!", Toast.LENGTH_LONG).show();
			        }

			        @Override
			        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
			            Log.i("Activity", "Success!");
			            Toast.makeText(getApplicationContext(), "Posted on Facebook!", Toast.LENGTH_SHORT).show();
			        }
			    });
			
		
			
		// get string location from Intent data in Map activity
		if (requestCode == 3) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getBaseContext(),
						"Location: " + data.getData().toString(),
						Toast.LENGTH_SHORT).show();
				diveLocation.setText(data.getData().toString());
				
				
			}

		}// end 3

		if (requestCode == 2) {
			/*
			 * choice is get photo from sd card, note this Intent only returns a
			 * URi and not a bitmap so must convert the Uri onto Bitmap
			 */
			
			
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				if (selectedImage != null) {
					 Bitmap bm = null; 
						BitmapFactory.Options options = new BitmapFactory.Options(); 
						options.inSampleSize = 2; 
						AssetFileDescriptor fileDescriptor =null; 
						
						try {
							
							//Uri selectedImage = data.getData();
						fileDescriptor = getContentResolver()
								.openAssetFileDescriptor(selectedImage,"r"); 
						
						photo = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options); 
						
						fileDescriptor.close(); 
						
						} catch (FileNotFoundException e) { 
						e.printStackTrace(); 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
				}// end try

				// resize photo
				photoBitmap =photo;
				//photoBitmap = reSizeImage(photo);
				
				// save image to Sd card with asynch thread inner class
				saveIMageToPhoneAsynch =(saveImageExtenalDriveAsynch) new saveImageExtenalDriveAsynch(
						getApplicationContext())
						.execute(photo);
				
				diveSitePicture.setImageBitmap(photoBitmap);
				diveSitePicture.setScaleType(ScaleType.FIT_XY);
			}
			
			
		} else if (requestCode == cameraData) {

			// if photo is taken from the camera, store in external storage
			if (resultCode == Activity.RESULT_OK) {
				//Toast.makeText(getBaseContext(), "resultCode=0",
					//	Toast.LENGTH_SHORT).show();
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								// use a Bundle object to get data from the
								// Intent,
								// Bundle holed the value data in key:value
								// pairs, like a Java Map
								// data structure
								Bundle extras = data.getExtras();// get map of
																	// all data
								// get the image from the Bundle data using the
								// key string as 'data'
								// to get the bitmap value

								//if data.getData does not return a Uri, then the data is in extras
								
//								The magic fairy dust in this function that allows you to trim down 
//								large bitmaps into digestible sizes is the options.inSampleSize property.
//								inSampleSize – in this instance – takes a bitmap and reduces its height 
//								and width to 20% (1/5) of its original size.
//								The larger the value of inSampleSize N (where N=5 in our example), the more the bitmap is reduced in size. 
//
//								There’s also another coding practice that one s
//								hould always use when dealing with Bitmaps in Android,
//								and that is the use of the Bitmap recycle() method. 
//								The recycle() method frees up the memory associated with a 
//								bitmap’s pixels, and marks the bitmap as “dead”,
//								meaning it will throw an exception if getPixels() or 
//								setPixels() is called, and will draw nothing. 
								
								// ref http://tutorials-android.blogspot.co.il/2011/11/outofmemory-exception-when-decoding.html
								
								
								if(data.getData()==null){//no uri
								    photo = (Bitmap)data.getExtras().get("data");
								    
								}else{
									
								    Bitmap bm = null; 
									BitmapFactory.Options options = new BitmapFactory.Options(); 
									options.inSampleSize = 2;  // 2=size =720 x 1280
									AssetFileDescriptor fileDescriptor =null; 
									
									try {
										
										Uri selectedImage = data.getData();
									fileDescriptor = getContentResolver()
											.openAssetFileDescriptor(selectedImage,"r"); 
									} catch (FileNotFoundException e) { 
									e.printStackTrace(); 
									} 
									finally{ 
									try { 
										photo = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options); 
									
									fileDescriptor.close(); 
									
									photoBitmap =photo;
									//photoBitmap = reSizeImage(photo);
									// save image to SD card or external storage
									saveIMageToPhoneAsynch =(saveImageExtenalDriveAsynch) new saveImageExtenalDriveAsynch(
											getApplicationContext())
											.execute(photo);
									
									//diveSitePicture.setScaleType(ScaleType.FIT_XY);
									diveSitePicture.setImageBitmap(photoBitmap);
									diveSitePicture.setScaleType(ScaleType.FIT_XY);

									
									
									//Uri photoShot = data.getData();
									} catch (IOException e) { 
									e.printStackTrace(); 
									} 
									}
								}
								

							}// end inner run

						});// end new runnable

					}

				};
				new Thread(runnable).start();

			}// end if result OK for camera shot
		}// end else resultCode ==2
	}// end onActivity

	
	@Override
	public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
		// get the rating,  returns a  float
		diveRate = this.diveRating.getRating();
		//makeToast("Rating " + diveRate + "!");

	}// end onRatelistener

	public void makeToast(String message) {
		//Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
	}// end make toast

	public Bitmap reSizeImage(Bitmap bitmapImage) {
		// resize bitmap image passed and rerun new one
		Bitmap resizedImage = null;
		float factorH = h / (float) bitmapImage.getHeight();
		float factorW = w / (float) bitmapImage.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		try {
			resizedImage = Bitmap.createScaledBitmap(bitmapImage,
					(int) (bitmapImage.getWidth() * factorToUse),
					(int) (bitmapImage.getHeight() * factorToUse), false);
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "Problem resizing Image @Line 510+");
			e.printStackTrace();
		}
		Log.d(TAG,
				"in resixed, value of resized image: "
						+ resizedImage.toString());
		return resizedImage;
	}// end reSize

	private class saveImageExtenalDriveAsynch extends
			AsyncTask<Bitmap, Void, String> {
		
		String resultMessage = "";
		Context mContext;
		ProgressDialog pb;

		
		// constructor takes 'this' context so can be called form anywhere
		// within this class
		public saveImageExtenalDriveAsynch(Context context) {
			super();
			mContext = context;
		}

		
		@Override
		protected void onPreExecute() {
			
			// make sure we disable the Facebook button and the Map FOnd Location button until the savedIMagePathWay is set
			//or it will not save when theeese activities ae launched
			activateMap.setEnabled(false);
			viewLog.setEnabled(false);//facebook post
			saveLog.setEnabled(false);//cannot save data until we save imageto SD card!
			
			//set dialog while image been saved and turn off in postEcecute
			//show dialog while loading data
			pb = new ProgressDialog( LogDive.this, ProgressDialog.STYLE_SPINNER);
			pb.setMessage("Saving dive site Image....");
			pb.show();
			
			super.onPreExecute();
		}


		@Override
		protected String doInBackground(Bitmap... params) {
			// save images from phone or gallery to SD card asynchronously
			// check if external storage is available to write
			
			
			
			// check if backButton pressed mid and cancel task if so
			if(this.isCancelled()==true){
				
				return null;
			}
			Bitmap imageToSave = params[0];// get image
			
			Log.d(TAG, "1812 Image to be saved size, height "+ imageToSave.getHeight() +" and width: " +imageToSave.getWidth());

			final String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// media available
				canSaveToExternalSD = true;
				// use a random number to append to image file name until use
				// diveNumber according to log entry on
				Random diveNum = new Random();
				File sdCard = Environment.getExternalStorageDirectory();
				final File directory = new File(sdCard.getAbsolutePath()
						+ File.separator + diveAlbum);
				final File theFile = new File(directory, "image"
						+ diveNum.nextInt(5000) + ".png");
				directory.getParentFile().mkdirs();
				//directory.mkdirs();   was this getting errors
				//f.getParentFile().mkdirs()
				if (!directory.mkdirs()) {
					Log.d(TAG, "Directory not created!!!!");
				}

				/*
				 * dont try to udate UI form here as will get looper errors if
				 * (theFile.exists()) { theFile.delete(); makeToast("Name " +
				 * theFile.getAbsolutePath() + " already exists!"); }
				 */
				try {
					FileOutputStream out = new FileOutputStream(theFile);
					imageToSave.compress(Bitmap.CompressFormat.PNG, 90, out);// compress
					Log.d(TAG, "Image to be saved size AFTER COMPRESS TO PNG, height "+ imageToSave.getHeight() +" and width: " +imageToSave.getWidth());
					// image fro
					// output
					out.flush();
					out.close();

					// check if external storage is available at least to read
					if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
							|| Environment.MEDIA_REMOVED.equals(state)
							|| Environment.MEDIA_REMOVED.equals(state)
							|| Environment.MEDIA_SHARED.equals(state)) {
						resultMessage = "Cannot save to your SD card! \n"
								+ "It may be read only, missing, removed, or connected to USB. \n"
								+ "Check your settings\nAnd try again.";
					}// end if
					savedImagePath = theFile.getAbsolutePath();
					Log.d(TAG, "Image saved to path: "+ savedImagePath);
					

					// Tell the media scanner about the new file so that it is
					// immediately available to the user.
					MediaScannerConnection
							.scanFile(
									getApplicationContext(),
									new String[] { theFile.toString() },
									null,
									new MediaScannerConnection.OnScanCompletedListener() {
										public void onScanCompleted(
												String path, Uri uri) {
											Log.i("ExternalStorage", "Scanned "
													+ path + ":");
											Log.i("ExternalStorage", "-> uri="
													+ uri);
										}
									});// end media scanner

					// view the saved image in the phones gallery
					try {
						sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
								Uri.parse("file://"
										+ Environment
												.getExternalStorageDirectory())));
					} catch (Exception ex) {
						// Security exception
						Log.d(TAG, "Cant call Intent.ACTION>MEDIA.MOUNTED");
					}// end

					return resultMessage = "Picture succesfully saved in "
							+ savedImagePath;
					

				} catch (IOException e) {
					// Unable to create file, likely because external storage is
					// not currently mounted or permissions from manifast not
					// granted by user.
					resultMessage = "Cannot save to your SD card! \n"
							+ "It may be read only, missing, removed, or connected to USB. \n"
							+ "Check your settings\nAnd try again.";

					e.printStackTrace();
				}// end if media mounted or available
			}

			// garbage collect. once saved to sd card
			//imageToSave.recycle();
			System.gc();
			
			
			return resultMessage;// return to onPostExcute

		}// end do in background thread

		@Override
		protected void onPostExecute(String result) {
			// send success message to makeToast
			//makeToast(result);
			//Toast.makeText(getApplicationContext(), "IMage saved to path in asynch class savedIMagePath=" + savedImagePath, Toast.LENGTH_SHORT).show();
			Log.d(TAG, "IMage saved to path in asynch class savedIMagePath=" + savedImagePath);
			//enable facebook and map buttons once we have pur image pathway form do in background
			//otherwise we will not be able to save the image in onSavedInstance when these buttons are clicked
			activateMap.setEnabled(true);
			viewLog.setEnabled(true);//facebook post
			saveLog.setEnabled(true);
			
			//turn of progress dialog
			if(pb.isShowing()){
				pb.dismiss();
			}
		}// End on post execute

	};// end aysnch save image

	public void saveDataWhileUsingMap(){
		//if map activoity is clicked we need to save the data first
		try {
			
			// get values for all log dive parameters as Strings and
			// Integers for Data base
			this.bottomDiveTime = Integer.parseInt(bottomTime.getText()
					.toString());
			this.diveBuddyString = diveBuddy.getText().toString();
			this.diveCenetrString = diveCenter.getText().toString();
			this.diveDateString = diveDate.getText().toString();
			this.diveLoctionString = diveLocation.getText().toString();
			
			this.diveSiteString = diveSite.getText().toString();
			this.endBars = Integer.parseInt(endBar.getText().toString());
			this.startBars = Integer
					.parseInt(startBar.getText().toString());
			this.viz = Integer.parseInt(vizibilty.getText().toString());
			this.waterTemperature = Integer.parseInt(waterTemp.getText()
					.toString());
			comment = comments.getText().toString();
		} catch (NumberFormatException ne) {
			makeToast(ne.getMessage());
		} catch (Exception e) {
			makeToast(e.getMessage());
		}

		// check first if photo taken
		if (savedImagePath.isEmpty()) {
			savedImagePath = "No Image Selected";
			//makeToast("No picture selected for this dive!");
		}

		boolean didItWork = true;
		try {

			// db instance divedateBase and write to it
			diveDataBase entry = new diveDataBase(this);
			entry.open();
			entry.createEntry((int) diveRate, bottomDiveTime,
					diveBuddyString, diveCenetrString, diveLoctionString,
					diveNum, diveSiteString, endBars, startBars, viz,
					waterTemperature, diveDateString, comment,
					conditionChoice, savedImagePath, diveDepth);
entry.close();
		} catch (Exception e) {
			// display failed dialog message
			didItWork = false;
			e.printStackTrace();
			String error = e.getMessage();
			Dialog d = new Dialog(this);
			d.setTitle("Saving to DB");
			TextView tv = new TextView(this);
			tv.setText("Dive did not save, somethong went wrong writong to DB!\nError: "
					+ error);
			d.setContentView(tv);
			//d.show();

		} finally {
			// display success message dialog
			if (didItWork) {
				Dialog d = new Dialog(this);
				d.setTitle("Saving to DB");
				TextView tv = new TextView(this);
				//tv.setText("\nDive Succesfully Logged!!\nPress back to continue.");
				//d.setContentView(tv);
				//d.show();
				

			}// end finnally
		}// end didItWork
			// log the values of all variables before been sent to SQL
		Log.d(TAG, "All data for SQL: Dive Rating: " + this.diveRate
				+ " Bottom Time: " + bottomDiveTime + "Buddy: "
				+ diveBuddyString + "\n" + "Dive Center: "
				+ diveCenetrString + "Dive Loaction: " + diveLoctionString
				+ " Dive Number: " + diveNum + "\n" + "Dive Site: "
				+ diveSiteString + "End Bar: " + endBars + " Start Bar: "
				+ startBars + "\n" + "Vizibilty: " + viz + "Water temp: "
				+ waterTemperature + " Dive Date: " + this.diveDateString
				+ "\n" + "Comments: " + comment + "Conditions: "
				+ this.conditionChoice + " Condtions Index: "
				+ this.condtionIndex + "Image saved to SD path: "
				+ this.savedImagePath);

		
		
		
		
	}//end saveDataWhileUsingMap
	
	
	//configure other methods on ui helper to handle activity life cycle call backs correctly from FB
		@Override
		protected void onResume() {
		    super.onResume();
		    uiHelper.onResume();
		    
		    
		}

		@Override
		protected void onSaveInstanceState(Bundle outState) {
			
			//if onPause called on activity when user selects maps or FB, we lose value of global varibles like 
			//saveIMageBPath and photoBitMap but keep all view values (Edittexts)
			//so we must save our global values here and call them back on onRestoreInstance state and assign to IMageButton
			
		    super.onSaveInstanceState(outState);
		    uiHelper.onSaveInstanceState(outState);
		    
		    
		   outState.putString("savedImapthPathwayOnSaveInstance", savedImagePath);//the key string, followed by ots value
		   Log.d("MYTAG", "onSaveInstanceState the image path is  : " +savedImagePath);
		  
		}
		
		
		

		@Override
		protected void onRestoreInstanceState(Bundle savedInstanceState) {
			// get Strin savdeiMagepath and set ImageView to Bitmap when activity restored from Maps/Facebook activities
			
			super.onRestoreInstanceState(savedInstanceState);
			
		}





		@Override
		public void onPause() {
		    super.onPause();
		    uiHelper.onPause();
		}

		@Override
		public void onDestroy() {
		    super.onDestroy();
		    uiHelper.onDestroy();
		}
		
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





		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// TODO Auto-generated method stub
			
		}
		
		public Bitmap resizedImageForFacebookPost(){
			
			//get image from SD card file, convert to bitmap and resize BEFORE
			//image is allcated to memory, then pass back to FB uplOad{hotoRequest
			//tab oage 3.5MB to 4MB in size, S4/S5 2-2.5
			//min size we want is 0.5MB, this will take approx 10 sesinds to upload to FB on 3G or 
			//wi -fi as oth hvae same upload spped of 0.07MB
			//do not want to go below 500*500
			
			
				 try {
					 File imagePathFile = new File(this.savedImagePath); 
					final int IMAGE_MAX_SIZE = 3000;
						FileInputStream streamIn = new FileInputStream(imagePathFile);
						
					// Decode image size and setInJBounds = true to avoid auto memory allocation for large image
					    BitmapFactory.Options o = new BitmapFactory.Options();
					    o.inJustDecodeBounds = true;
					   BitmapFactory.decodeStream(streamIn, null, o);
						 streamIn.close();
						 
						int scale = 1;
						
						//adjust soze here with Math.Pow(scale, 3); adjust with 4 as goes up image size goes up
						//3 will give a 3MB image approx 0.5mb or 600*600 width:height
						//if scale == 4, image width and height is reduced by 1/4 and pixels by 1/16th
						//value of 4 will convert a 2000*1400 3.5MB photo to 512*288
						
						//FOR IMAGE OF 5ooKB 0.5MB 600by600, looks ok
						//FB IMAGE DIMENSIONS BEFORE RESIZING: scale = 4, orig-width: 640  orig-height: 638
						// FB IMAGE DIMENSIONS AFTER RESIZING: scale = 3, orig-width: 213  orig-height: 212ACTUAL SIZE MBYTES: 38
						//FB IMAGE DIMENSIONS BEFORE RESIZING: scale = 4 power = 5, orig-width: 640  orig-height: 638
						// FB IMAGE DIMENSIONS AFTER RESIZING: scale = 3, orig-width: 213  orig-height: 212ACTUAL SIZE MBYTES: 38

						//power size 5 converts 2000*1400 3.5MB to 640*360, takes 7seconds
						//power 5.9 takes 3.MB 2000by1440 to 900by500 and takes 6 sesonds to upload and looks fine
						
					    while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 5.0)) > 
					   IMAGE_MAX_SIZE) {
					       scale++;
					    }
					    
					   //get orginal width of image before loaded into memory 
					   Log.d(TAG, "FB IMAGE DIMENSIONS BEFORE RESIZING: " +
					   		"scale = " + scale + ", orig-width: " + o.outWidth + "  orig-height: " + o.outHeight);
					    
					   
					   Bitmap b = null;
					   streamIn = new FileInputStream(imagePathFile);
					    if (scale > 1) {
					        scale--;
					        // scale to max possible inSampleSize that still yields an image
					        // larger than target, inSampleSize loads the image into memor by a factor of its integer value
					        o = new BitmapFactory.Options();
					        o.inSampleSize = scale;
					    // Decode bitmap with inSampleSize set
					       o.inJustDecodeBounds = false;
					       
					       b = BitmapFactory.decodeStream(streamIn, null, o);
					       //savedImagePath = reSizeImage(b);
					      
					     //noOfImagesloaded++;
					       //get orginal width of image before loaded into memory 
						   Log.d(TAG, "FB IMAGE DIMENSIONS AFTER RESIZING: " +
						   		"scale = " + scale + ", orig-width: " + o.outWidth + "  orig-height: " + o.outHeight
						   		 + "ACTUAL SIZE MBYTES: " + (b.getByteCount()/1000000));
						    //no such method error +"SIZE MEGA BYTES" + (b.getAllocationByteCount()/1000000)
						   
					     streamIn.close();
					     //b.recycle();
					     System.gc();
					     
					     return b;//retrun btmap to calling method
					    
				} else {
					System.gc();
			       return null;
			       
			    }
					   
						//resizedImage = reSizeImage(bitmap);
					   
				
				 }catch(IOException exe){
					 exe.printStackTrace();
				 
				 
				 
			}catch(OutOfMemoryError exc){
				exc.printStackTrace();
				//Toast.makeText(this, "Something went wrong! Try again...", Toast.LENGTH_SHORT).show();
			}catch(NullPointerException nullpoint){
				nullpoint.printStackTrace();
				//end try catch
			}
			return null;
			
			
		}//end resized image for FB post

		
		
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			// free resources and detroy activity
			if(saveIMageToPhoneAsynch!=null){
				saveIMageToPhoneAsynch.cancel(true);
			}
			
			// free up meemory
			if(photo!=null){
				photo.recycle();
			}
			if(photoBitmap!=null){
				photoBitmap.recycle();
			}
			if(diveSites!=null){
				diveSites=null;
			}
			if(diveLocations!=null){
				diveLocations=null;
			}
			if(diveBuddies!=null){
				diveBuddies=null;
			}
			if(diveCenters!=null){
				diveCenters=null;
			}
			
			System.gc();
			finish();
		}
}// end class LogDive

