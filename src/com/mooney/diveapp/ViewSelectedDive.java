package com.mooney.diveapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

/*View the dive chosen from the View all dives list activity 
 * and update or delete. Get data from Lost activity via Bundled 
 * Intent carrying the Row the dive corresponds to.
 * 
 * Add aboilty to use a gestire so user can swipe back and forward to doives
 * Override onTouchEvent and dispatchEvent ( last  when using Scollview in xml only)
 */

public class ViewSelectedDive extends Activity implements OnClickListener,
		OnRatingBarChangeListener {

	private boolean editPressed = false;
	private static final int DATE_DIALOG_ID = 1;
	private static final String TAG = LogDive.class.getName();
	private EditText diveDate2, diveNumber2, diveLocation2, diveSite2,
			waterTemp2, vizibilty2, startBar2, endBar2, diveBuddy2,
			diveCenter2, comments2, bottomTime2, condtionsOfDive,
			diveDepthEditText2;
	private String retrunedPicPath="";
	private String diveDateString, diveLoctionString, diveSiteString,
			diveBuddyString, diveCenetrString, comment, conditionChoice;
	int bottomDiveTime, diveNum, waterTemperature, viz, startBars, endBars,
			condtionIndex, diveDepth2;
	private RatingBar diveRating2;
	private ImageButton diveSitePicture2, activateMap2;
	int yr, month, day;
	float diveRate2;
	protected Intent usePhoneCamera;
	protected int cameraData = 0;
	private Bitmap photo, photoBitmap;
	private String encodedImage;// string base64 rep of image
	private Uri mCapturedImageURI;
	private Bitmap defaulPhoto;
	private Handler handler;
	private Spinner conditions;
	private String[] diveConditions;
	private Button editLog, deleteLog;
	// for image resizing
	private static int w = 250;
	private static int h = 250;
	private String dataBaseRowNumber;
	private String data = "";
	private String diveAlbum = "dive_photos";// album name in ext directory
	private String savedImagePath2 = "";// file path to image on SD card
	private Bitmap resizedImage = null;
	
	private  SharedPreferences sharedpreferences;
	 public static final String MyPREFERENCES = "MyPrefFile" ;
	 private int viewNumber;
	
	public GestureDetectorCompat gestureDetectorObject;
	private boolean imageHasBeenEnlarged;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.viewloggeddive2);
		
		
		
		// instantiate gesture detector
		gestureDetectorObject = new GestureDetectorCompat(this, new GestureListener());
		
		intililizeWidgets();
		// set the values for the dive form the saved dive
		setDiveLoggedValues();
		
		
		if(savedInstanceState!=null){
			
			//make sure the Update button is on and ready to save to database when user navigates away fomr
			//activity by gettong a image and make sure image is loaded if already chosen
			// override OnSavedInstance class method first to save varibles when activity is pout to background
			
			setEditable();
			this.diveRating2.setNumStars(5);// reset dive rating bar
			this.editLog.setText("Update");
			editPressed = true;
			
			// reload image if saved prio to luanching maps 
			
			//now we just get the bitmap form the image path and return to IMageView
			Log.d("MYTAG", "on returning the image path is : " + retrunedPicPath);
			
			if(!savedImagePath2.isEmpty())
			setImageViewFromSavedInstance(savedImagePath2);
			Log.d("MYTAG", "on returning the image path not found:==  " + retrunedPicPath);
			//now we must ressign savedIMagePathway if we nagate from activit agian ie caling maps then facebook
			//savedImagePath2 = savedImagePath2;
			Log.d("MYTAG", "on returning the image path set back to saveIMagePath:==  " + savedImagePath2);
		}
		
		
		
		// display every 10 views of dive
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		viewNumber = sharedpreferences.getInt("viewnumber", 0);
		
		if(viewNumber == 0|| viewNumber == 10 || viewNumber == 20 || viewNumber == 30 || viewNumber == 45
				||viewNumber == 60 ||viewNumber == 75 ||viewNumber == 90 ||viewNumber == 100 ||viewNumber == 130
				||viewNumber == 160 ||viewNumber == 190||viewNumber == 230 ||viewNumber == 280 ){
		displayCustomToast(); // inof anout swipe
		
		
		}
		Editor editor = sharedpreferences.edit();
		viewNumber++;
		//viewNumber = sharedpreferences.getInt("viewnumber", 0);
		editor.putInt("viewnumber", viewNumber);
		editor.commit();
		
	}// end onCreate

	
	
	
@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save data to instcance bundle here when loading maps and putting current activity to background
	// get bundle data back in onCreate
	// saved image path is assigned asynvh task after photo uplodaed from gallery by user
	
	outState.putString("savedImapthPathwayOnSaveInstance", savedImagePath2);//the key string, followed by ots value
	   Log.d("MYTAG", "onSaveInstanceState the image path is  : " +savedImagePath2);
		super.onSaveInstanceState(outState);
	
}




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
				
				diveSitePicture2.setImageBitmap(b);
				
			} else {
				// diveSitePicture2.setBackgroundResource(R.drawable.camera);
//				

				// diveSitePicture2.setBackgroundDrawable(R.drawable.camera);
				Log.d(TAG, "247 No image saved from returning form Maps of FB");
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


	
	private void displayCustomToast() {
		// dsipaly custome toast using custom xml to give info about swu
		// swiping
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE); // 0 - for private mode
		
		
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Swipe To View Dives");
		
		
		

//		TextView text = (TextView) dialog.findViewById(R.id.text);
//		//text.setText("Your Text");
//		ImageView image = (ImageView) dialog.findViewById(R.id.imageForCustomDialogAlert);
//		//image.setImageResource(R.drawable.ic_launcher);

		Button dialogButton = (Button) dialog.findViewById(R.id.customDialogButtonOK);
		dialogButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        dialog.dismiss();
		    }
		});

		dialog.show();
		
	}




	private void setDiveLoggedValues() {
		/*
		 * get data from bundle intent in the form of the row of thedive
		 * selected from the list view menu (ViewListOfDives class).First get
		 * the bundle data containg the row number and open databaseand get
		 * cursor according to the row number
		 */

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			dataBaseRowNumber = extras.getString("row");
		}
		Log.d(TAG, "Row number from Lsit menu Intent is : " + dataBaseRowNumber);
		// Toast.makeText(getApplicationContext(), "Row from List Menu is: "+
		// dataBaseRowNumber, Toast.LENGTH_SHORT).show();

		// get cursor object rep of the row
		// get data fro database

		diveDataBase db = new diveDataBase(this);
		db.open();
		long l = Long.parseLong(dataBaseRowNumber);// convert to long

		this.diveSite2.setText(db.getSite(l));
		String divSite = db.getSite(l);

		this.diveLocation2.setText(db.getLocation(l));

		String loc = db.getLocation(l);
		String divedate = db.getDiveDate(l);
		this.diveDate2.setText(db.getDiveDate(l));

		this.condtionsOfDive.setText(db.getDiveConditions(l));
		this.diveNumber2.setText(db.getDiveNumber(l));
		// diveNumber2.setEnabled(false);
		this.diveRating2.setRating(Float.parseFloat(db.getDiveRate(l)));
		int diveRate = (int) Float.parseFloat(db.getDiveRate(l));
		// Toast.makeText(getApplicationContext(), "Dive Rating: "+ diveRate,
		// Toast.LENGTH_SHORT).show();
		this.bottomTime2.setText(db.getDiveBottomTime(l));
		this.diveBuddy2.setText(db.getDiveBuddy(l));
		this.diveCenter2.setText(db.getDiveCenter(l));
		this.endBar2.setText(db.getDiveEndBar(l));
		this.startBar2.setText(db.getDiveStartBar(l));
		this.vizibilty2.setText(db.getDiveViz(l));
		this.waterTemp2.setText(db.getDiveTemp(l));
		this.comments2.setText(db.getDiveComments(l));
		diveDepthEditText2.setText(db.getDiveDepth(l));

		// String retrunedConditions = db.getDiveComments(l);
		/*
		 * fade image in and out animation code
		 */
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
		fadeIn.setDuration(3000);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
		fadeOut.setStartOffset(1000);
		fadeOut.setDuration(1000);

		AnimationSet animation = new AnimationSet(false); // change to false
		animation.addAnimation(fadeIn);
		// animation.addAnimation(fadeOut);
		// get base 64 string from DB
		retrunedPicPath = db.getDiveImage_Path(l);

		// Toast.makeText(getApplicationContext(), "String path for image is: "+
		// retrunedPicPath, Toast.LENGTH_SHORT).show();
		File imagePathFile = new File(retrunedPicPath);
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

			//by setting m.pow(scale, 3.85) we get a org image of 2500:1400 beofre loaded and when loaded we get 426:240,
			//no need fro resize method call here
			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 4.2)) > IMAGE_MAX_SIZE) {
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
				// Bitmap resizedImage = reSizeImage(bitmap);
				// displayImage.setAnimation(animation);
				diveSitePicture2.setAnimation(fadeIn);
				// set the scale type so image fits correctly
				diveSitePicture2.setScaleType(ScaleType.FIT_XY);
				diveSitePicture2.setImageBitmap(b);
				//diveSitePicture2.setImageBitmap(resizedImage);
				// Toast.makeText(getApplicationContext(),
				// "Image set from DB Image", Toast.LENGTH_SHORT).show();

				// displayImage.setImageBitmap(resizedImage);
				// return resizedImage;

			} else {
				// diveSitePicture2.setBackgroundResource(R.drawable.camera);
				Toast.makeText(getApplicationContext(),
						"Image not set, no Image form DB...",
						Toast.LENGTH_SHORT).show();
				// diveSitePicture2.setBackgroundDrawable(R.drawable.camera);
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

		db.close();
		// this.comments.setText(returnedDiveSite);

		// now set all fields to uneditable until 'Edit' Button clicked
		setUnEditable();
	}// end setDivedLoggedValues
	
	
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// tell teh activity tto use gerture listener when touch event is detected
		
		this.gestureDetectorObject.onTouchEvent(event);
		
		Log.d(TAG, "312 ON TOUCH EVENT");
		return super.onTouchEvent(event);
	}




	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// must use this to pass event on to Gesture object from scrollvoew n xml
		
		super.dispatchTouchEvent(ev);
		
		return this.gestureDetectorObject.onTouchEvent(ev);
	}




	public void setUnEditable() {
		// set all fields to uneditable until edit button clicked
		diveSite2.setEnabled(true);
		this.diveRating2.setEnabled(true);
		diveLocation2.setEnabled(true);
		diveDate2.setEnabled(true);
		bottomTime2.setEnabled(false);
		diveBuddy2.setEnabled(false);
		diveCenter2.setEnabled(false);
		endBar2.setEnabled(false);
		startBar2.setEnabled(false);
		vizibilty2.setEnabled(false);
		waterTemp2.setEnabled(false);
		comments2.setEnabled(false);
		this.condtionsOfDive.setEnabled(false);
		this.diveNumber2.setEnabled(false);
		//this.diveSitePicture2.setEnabled(false);
		this.activateMap2.setEnabled(false);
		diveDepthEditText2.setEnabled(false);

	}// end set uneditable

	public void getUpdatedValues() {
		
		// get any changed values to be sent back to databse when Update Button
		// clicked

		this.diveDateString = this.diveDate2.getText().toString();
		this.diveLoctionString = this.diveLocation2.getText().toString();
		this.diveSiteString = this.diveSite2.getText().toString();
		this.diveBuddyString = this.diveBuddy2.getText().toString();
		this.diveCenetrString = this.diveCenter2.getText().toString();
		this.comment = this.comments2.getText().toString();
		bottomDiveTime = Integer
				.parseInt(this.bottomTime2.getText().toString());
		diveNum = Integer.parseInt(this.diveNumber2.getText().toString());
		this.waterTemperature = Integer.parseInt(this.waterTemp2.getText()
				.toString());
		viz = Integer.parseInt(this.vizibilty2.getText().toString());
		startBars = Integer.parseInt(this.startBar2.getText().toString());
		endBars = Integer.parseInt(this.endBar2.getText().toString());
		this.diveRate2 = this.diveRating2.getNumStars();
		this.conditionChoice = this.condtionsOfDive.getText().toString();
		diveRate2 = diveRating2.getRating();
		
		
		// if no image is taken we must saved the current image in DB back again, 
		// returnedPciPath is from database image loaded when 
		// activity starts, savedImagePath2 is new image uploaded and saved to file
		
		if (!savedImagePath2.isEmpty()){
		this.retrunedPicPath = this.savedImagePath2;// get the string image path
													// for saving the new pic taken
		
		Log.d("MYTAG", "Saving the new changed image from here  :" + savedImagePath2);
		Log.d("MYTAG", "Saving the new changed image at :" + retrunedPicPath);
		}
		else if ( savedImagePath2.isEmpty()){
			//get current image from DB abd save it back to retrunedPicPath
			diveDataBase db = new diveDataBase(this);
			db.open();
			long l = Long.parseLong(dataBaseRowNumber);// convert to long
			retrunedPicPath = db.getDiveImage_Path(l);
			Log.d("MYTAG", "Saving the not changed image at :" + retrunedPicPath);
			
			db.close();
		}
		
		
		
		diveDepth2 = Integer.parseInt(diveDepthEditText2.getText().toString());

	}// end getUpdated Values

	public void setEditable() {
		// set all widgets to editable once the 'Edit' button is clicked
		diveSite2.setEnabled(true);
		diveLocation2.setEnabled(true);
		diveDate2.setEnabled(true);
		bottomTime2.setEnabled(true);
		diveBuddy2.setEnabled(true);
		diveCenter2.setEnabled(true);
		endBar2.setEnabled(true);
		startBar2.setEnabled(true);
		vizibilty2.setEnabled(true);
		waterTemp2.setEnabled(true);
		comments2.setEnabled(true);
		this.condtionsOfDive.setEnabled(true);
		this.diveSitePicture2.setEnabled(true);
		this.activateMap2.setEnabled(true);
		this.diveRating2.setEnabled(true);
		diveDepthEditText2.setEnabled(true);
		this.diveNumber2.setEnabled(true);

	}// end setEditable

	public Bitmap reSizeImage(Bitmap bitmapImage) {
		// resize bitmap image passed and rerun new one, this always called from
		// with a asynvh task
		Bitmap resizedImage = null;
		float factorH = h / (float) bitmapImage.getHeight();
		float factorW = w / (float) bitmapImage.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		Log.d(TAG, "Image size & width before resize: " + bitmapImage.getWidth() + " image heoght: "+ bitmapImage.getHeight());
		//makeToast("Image size & width before resize: " + bitmapImage.getWidth() + " image heoght: "+ bitmapImage.getHeight());
		try {
			resizedImage = Bitmap.createScaledBitmap(bitmapImage,
					(int) (bitmapImage.getWidth() * factorToUse),
					(int) (bitmapImage.getHeight() * factorToUse), false);
			Log.d(TAG, "Image size & width AFTER resize: " + (bitmapImage.getWidth() * factorToUse) + " Height: "+ (bitmapImage.getHeight() * factorToUse));
		 //makeToast("Image size & width AFTER resize: " + (bitmapImage.getWidth() * factorToUse) + " Height: "+ (bitmapImage.getHeight() * factorToUse));
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "Problem resizing Image @Line 510+");
			e.printStackTrace();
		}
		Log.d(TAG,
				"in resixed, value of resized image: "
						+ resizedImage.toString());
		return resizedImage;
	}// end reSize

	private void intililizeWidgets() {
		// intilize all wdigets
		// intilize all widgets form XML viewloggeddive2 and add listeners

		handler = new Handler();
		diveDepthEditText2 = (EditText) findViewById(R.id.et_diveDepth2);
		diveDate2 = (EditText) this.findViewById(R.id.dp_diveDate2);
		deleteLog = (Button) this.findViewById(R.id.button_deleteDive);
		deleteLog.setOnClickListener(this);

		editLog = (Button) this.findViewById(R.id.button_EditDive);
		editLog.setOnClickListener(this);

		diveRating2 = (RatingBar) this.findViewById(R.id.rb_diveRating);

		diveRating2.setOnRatingBarChangeListener(this);

		bottomTime2 = (EditText) this.findViewById(R.id.et_bottomTime2);
		diveBuddy2 = (EditText) this.findViewById(R.id.et_diveBuddy2);
		diveCenter2 = (EditText) this.findViewById(R.id.et_diveCenter2);

		diveLocation2 = (EditText) this.findViewById(R.id.et_diveLocation);
		diveNumber2 = (EditText) this.findViewById(R.id.et_DiveNumber2);
		diveSite2 = (EditText) this.findViewById(R.id.et_diveSiteLogDive2);

		endBar2 = (EditText) this.findViewById(R.id.et_endPressure2);
		startBar2 = (EditText) this.findViewById(R.id.et_startPressure2);
		vizibilty2 = (EditText) this.findViewById(R.id.et_visibilty2);

		waterTemp2 = (EditText) this.findViewById(R.id.et_waterTemp2);
		diveSitePicture2 = (ImageButton) this.findViewById(R.id.im_divePic);
		diveSitePicture2.setOnClickListener(this);

		this.comments2 = (EditText) this.findViewById(R.id.et_DiveComments2);
		this.activateMap2 = (ImageButton) this
				.findViewById(R.id.ib_findLocationOnMap2);
		activateMap2.setOnClickListener(this);
		this.condtionsOfDive = (EditText) this
				.findViewById(R.id.et_tv_diveConditions2);

	}// end intilize widgets

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		// get the new dive rating if changed
		diveRate2 = diveRating2.getRating();

	}

	@Override
	public void onClick(View v) {
		// handle delete and update buttons
		switch (v.getId()) {

		case R.id.ib_findLocationOnMap2:
			// get map location as string
			startActivityForResult(new Intent(
					"android.intent.action.MAPDIVELOCATION"), 3);
			// 3 as 1 and 2 already used

			break;

		case R.id.im_divePic:
			
			
			// get picture if image buttn selected
			// take photo
			// access the phones camera with a new Intent get data ret from the
			// photo
			// use alert dialog to ask to take a photo or get form SD card
			
			if(editPressed){
				
				// if edot btton press
			AlertDialog.Builder build = null;
			AlertDialog dialog = null;
			build = new AlertDialog.Builder(this);
			build.setCancelable(true);
			build.setMessage("Take a photo or choose from SD card kind sir.");
			build.setPositiveButton("Cancel",
					new DialogInterface.OnClickListener() {

						// this giving a error so reove option for now
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// take a photo with a new camera intent
							// usePhoneCamera = new Intent(
							// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							// startActivityForResult(usePhoneCamera,
							// cameraData);
							return;
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
			
			}else if(editPressed==false){
				
				if(!imageHasBeenEnlarged){
				// e;arge image
				diveSitePicture2.requestLayout(); //call if layout already set in xmls
				
				this.diveSitePicture2.getLayoutParams().height=550;
				this.diveSitePicture2.getLayoutParams().width=550;
				this.imageHasBeenEnlarged=true;
				diveSitePicture2.setScaleType(ImageView.ScaleType.FIT_XY);//fill the gallery container with image
				
				}else if(imageHasBeenEnlarged){
					
					// reduce size back
					diveSitePicture2.requestLayout(); //call if layout already set in xmls
					
					this.diveSitePicture2.getLayoutParams().height=320;
					this.diveSitePicture2.getLayoutParams().width=270;
					this.imageHasBeenEnlarged=true;
					diveSitePicture2.setScaleType(ImageView.ScaleType.FIT_XY);//fill the gallery container with image
					imageHasBeenEnlarged=false;
				}
				}
			break;

		case R.id.button_deleteDive:
			try {
				// display dialog
				AlertDialog.Builder build1 = null;
				AlertDialog dialog1 = null;
				build1 = new AlertDialog.Builder(this);
				build1.setCancelable(true);
				build1.setMessage("Are you sure u want to delete this Dive?");
				build1.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String rowDelete = dataBaseRowNumber;
								long lDeleteRow = Long.parseLong(rowDelete);
								diveDataBase dbase1 = new diveDataBase(
										getBaseContext());
								dbase1.open();
								dbase1.deleteEntry(lDeleteRow);
								dbase1.close();

								finish();

							}// end on click
						});// end pos button take photo

				// do npot delete the dive, just continue
				build1.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// no code req

							}// end onclick
						});// end pos button take photo
				dialog1 = build1.create();
				dialog1.setTitle("Delete Confirmation");
				dialog1.show();

			} catch (Exception e) {
				// display failed dialog message
				// didItWork=false;
				e.printStackTrace();
				String error = e.getMessage();
				Dialog d = new Dialog(this);
				d.setTitle("Saving to DB");
				TextView tv = new TextView(this);
				tv.setText("Dive did not save, somethong went wrong writong to DB!\nError: "
						+ error);
				d.setContentView(tv);
				d.show();

			}
			break;
		case R.id.button_EditDive:
			if (editPressed == false) {
				setEditable();
				this.diveRating2.setNumStars(5);// reset dive rating bar
				this.editLog.setText("Update");
				this.imageHasBeenEnlarged=true;
				editPressed=true;
				
			}// make evrything editable

			
			else if (editPressed == true) {// if already pressed
				try {
					// String row = rowEntered.getText().toString();
					// long lRow = Long.parseLong(row);
					
					// save to database
					
					long lRow = Long.parseLong(dataBaseRowNumber);// convert to
																	// long
					// then
					this.getUpdatedValues();
					diveDataBase dbase = new diveDataBase(this);
					dbase.open();
					dbase.updateEntry(lRow, String.valueOf(diveRate2),
							String.valueOf(bottomDiveTime), diveBuddyString,
							diveCenetrString, diveLoctionString,
							String.valueOf(diveNum), diveSiteString,
							String.valueOf(endBars), String.valueOf(startBars),
							String.valueOf(viz),
							String.valueOf(waterTemperature), diveDateString,
							comment, conditionChoice, retrunedPicPath,
							String.valueOf(diveDepth2));
					dbase.close();
					Log.d(TAG, "Saving imag to DB : "+retrunedPicPath);
					Toast.makeText(getApplicationContext(), "Dive Updated!",
							Toast.LENGTH_SHORT).show();
					this.setUnEditable();
					this.deleteLog.setEnabled(false);

					this.editLog.setEnabled(false);
					// editLog.setPressed(true);
					editLog.setTextColor(Color.GRAY);
					deleteLog.setTextColor(Color.GRAY);
					this.displayDialog();
				} catch (Exception e) {
					// display failed dialog message
					// didItWork=false;
					e.printStackTrace();
					String error = e.getMessage();
					Dialog d = new Dialog(this);
					d.setTitle("Saving to DB");
					TextView tv = new TextView(this);
					tv.setText("Dive did not save, somethong went wrong writong to DB!\nError: "
							+ error);
					d.setContentView(tv);
					d.show();

				}

			}// end else
			editPressed = true;


			break;

		}// end switch

	}// end onClick

	// get photos from SD card or take on from button selection startActivity
	// for intents
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		
		
		// method checks data returned form camera via startActivityForResult
		
		super.onActivityResult(requestCode, resultCode, data);
		// check data integrity against static int from activity class
		

		// get string location from Intent data in Map activity
		
		if (requestCode == 3) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getBaseContext(),
						"Location: " + data.getData().toString(),
						Toast.LENGTH_SHORT).show();
				// set the edit text to location form map
				diveLocation2.setText(data.getData().toString());
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


				// save image to Sd card with asynch thread inner class
				(new saveImageExtenalDriveAsynch2(this)).execute(photo);
				
				// resize photo
				photoBitmap = reSizeImage(photo);
				diveSitePicture2.setImageBitmap(photoBitmap);
				diveSitePicture2.setScaleType(ScaleType.FIT_XY);
				
			}
			
			//switch off currentlty
			
		} else if (requestCode == cameraData) {

			// if photo is taken from the camera, store in external storage
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getBaseContext(), "resultCode=0",
						Toast.LENGTH_SHORT).show();
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
								// if data.getData does not return a Uri, then
								// the photo data is in extras
								if (data.getData() == null) {// no uri
									photo = (Bitmap) data.getExtras().get(
											"data");
								} else {
									try {
										// get bitmap form uri source
										photo = MediaStore.Images.Media
												.getBitmap(
														getContentResolver(),
														data.getData());
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

								photo = (Bitmap) extras.get("data");
								// save image to SD card or external storage
								(new saveImageExtenalDriveAsynch2(
										getApplicationContext()))
										.execute(photo);
								photoBitmap = reSizeImage(photo);
								diveSitePicture2.setScaleType(ScaleType.FIT_XY);
								diveSitePicture2.setImageBitmap(photoBitmap);

								//Uri photoShot = data.getData();

							}// end inner run

						});// end new runnable

					}

				};
				new Thread(runnable).start();

			}// end if result OK for camera shot
		}// end else resultCode ==2
	}// end onActivity

	
	
	
	
	private class saveImageExtenalDriveAsynch2 extends
			AsyncTask<Bitmap, Void, String> {
		
		// private inner aycnh class resizes images and saves to SD card, called when user clicks getphoto and intent is activated
		// savedImagePath is assigned here
		
		
		String resultMessage = "";
		Context mContext;
		ProgressDialog pb;

		// constructor takes 'this' context so can be called form anywhere
		// within this class
		public saveImageExtenalDriveAsynch2(Context context) {
			super();
			mContext = context;
		}

		
		
		@Override
		protected void onPreExecute() {
			/* deactive the update and save to DataBase button until 
			*the image is saved to a file path in SD card (done in do in background)
			*enable again in onPOstExecute() of this aysnch class
			*/
			
			editLog.setEnabled(false);
			
			//set dialog while image been saved and turn off in postEcecute
			//show dialog while loading data
			pb = new ProgressDialog( ViewSelectedDive.this, ProgressDialog.STYLE_HORIZONTAL);
			pb.setMessage("Updating Dive Site Image....");
			pb.show();
			
			super.onPreExecute();
		}



		@Override
		protected String doInBackground(Bitmap... params) {
			// save images from phone or gallery to SD card asynchronously
			// check if external storage is available to write
			Bitmap imageToSave = params[0];// get image

			final String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// media available
				// canSaveToExternalSD = true;
				// use a random number to append to image file name until use
				// diveNumber according to log entry on
				Random diveNum = new Random();
				File sdCard = Environment.getExternalStorageDirectory();
				final File directory = new File(sdCard.getAbsolutePath()
						+ File.separator + diveAlbum);
				final File theFile = new File(directory, "image"
						+ diveNum.nextInt(5000) + ".png");
				directory.getParentFile().mkdirs();
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
					
					savedImagePath2 = theFile.getAbsolutePath();

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
							+ savedImagePath2;

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

			return resultMessage;// return to onPostExcute

		}// end do in background thread

		@Override
		protected void onPostExecute(String result) {
			
			/*send success message to makeToast and reenable the Udate Log button so we can save the 
			* image o the DataBase
			*/
			makeToast(result);
			editLog.setEnabled(true);
			
			//turn of dialog if showing
			if(pb.isShowing()){
				pb.dismiss();
			}
		}// End on post execute

	};// end aysnch save image

	public void makeToast(String message) {
		//enable only for de bugging
		
		//Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
	}// end make toast
	
	
	
	// inner class to handle guerter detection
	public class GestureListener extends GestureDetector.SimpleOnGestureListener{

		private float flingMin=0;
		private float velocityMin = 0;
		
		
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			
			
			Log.d(TAG, "ON FLING INNER CLASS 951");
			// user move to next dive in db
			boolean moveToNextDive=false;
			boolean moveToPreviousDive=false;
			
			// caulate the change in x pos within fling ges
			float horizontalDiff = e2.getX()-e1.getX();
			float verticalDiff = e2.getY() - e1.getY();
			
			// calulate the abs values
			float absHDiff = Math.abs(horizontalDiff);
			float absVDiff = Math.abs(verticalDiff);
			float absVelocityX = Math.abs(velocityX);
			float absVelocityY = Math.abs(velocityY);
					
			/*
			 * NOTE: the DB will start at row 0 but nothing is stired here
			 * so if total rows=9, then we have 8 entries starting row 1, row 2....row8
			 * First entry is row 1, not 0
			 */
					// now of hor distance > vertival distance , move back or forward
					if(absHDiff>absVDiff && absHDiff>flingMin && absVelocityX> velocityMin){
						
						// swiping backward
						if(horizontalDiff>0) {
							moveToPreviousDive=true;
							
							//Toast.makeText(getApplicationContext(), "BACKWARD GESTURE DETECTED", Toast.LENGTH_LONG).show();
							Log.d(TAG, "SWIPE HORIZONTAL DETECTED BACKWARD");
							
							// move to last dive in db
							diveDataBase db = new diveDataBase(ViewSelectedDive.this);
							db.open();
							
							
							String currentDiveNumberString = diveNumber2.getText().toString();
							int currentDiveNumber = Integer.parseInt(currentDiveNumberString);
							
							// get ro ids fro the dive number
							int currentDiveRowID = db.getRowNumber(currentDiveNumber);
							int lastDiveRowID_totalRowNumbers = (db.getNumberOfRows()-1);;
							int firstDiveRowID =0;
							
							int lastDiveInDB = db.getLastDiveNumber();
							int diveDBEntries = db.getNumberOfRows();
							
							//String dive = db.getDiveNumber(lastDiveInDB);
							
							
							
							int nextDive=0;
							String goToDive="";
							
							boolean curentDiveNumberHasBeenCahmged=false;
							
							int currentRow = db.getRowNumber((currentDiveNumber-1));
							
							
							if(currentDiveRowID >1){
								
								// we can move down a row
								
								nextDive = currentDiveRowID-1;
								// next dive should be the corrsepomdng row of dive number
								//int nextDiveRow =db.getRowNumber(nextDive);
								
								goToDive = String.valueOf(nextDive);
							}
							
							
if(currentDiveRowID<=1){
	
	// can only move back to end of the dives
								
								nextDive = (lastDiveRowID_totalRowNumbers);
								// next dive should be the corrsepomdng row of dive number
								//int nextDiveRow =db.getRowNumber(nextDive);
								
								goToDive = String.valueOf(nextDive);
							}
							
							
							if(diveDBEntries<=1){
								
								// only one entry socant go back or froward, load current dive
								nextDive = currentDiveNumber;
								goToDive = String.valueOf(nextDive);
								Toast.makeText(getApplicationContext(), "Only one Entry!", Toast.LENGTH_LONG).show();
								
							}else{
								
							Intent i = new Intent("android.intent.action.VIEWSELECTEDDIVE");
							Bundle extras = new Bundle();
							extras.putString("row", goToDive);
							i.putExtras(extras);
							startActivityForResult(i, 0);
							Log.d("GESTIRE BACKWARD TO DVE", "SENDING ROW TO SELECTED TEST: " + goToDive );
							overridePendingTransition(R.anim.anim_slide_in_right,
			                        R.anim.anim_slide_out_right);
							//Toast.makeText(getBaseContext(), "Row selected: "+ row, Toast.LENGTH_LONG).show();
							
							
							// free memory and GC
							
							if(photo!=null){
								photo.recycle();
							}
							if(photoBitmap!=null){
								photoBitmap.recycle();
							}
							if(encodedImage!=null){
								encodedImage=null;
							}
							
							finish();//free resources and when backbutton pressed in selected test activity it will go back 
							
							
							
							System.gc();
							
							}
							
							
							
						}else{
							
							
							moveToNextDive=true;
							
								//Toast.makeText(getApplicationContext(), "FORWARD GESTURE DETECTED", Toast.LENGTH_LONG).show();
								Log.d(TAG, "SWIPE HORIZONTAL FORWAD DETECTED BACKWARD");
								
								// move tpo last dive in db
								diveDataBase db = new diveDataBase(ViewSelectedDive.this);
								db.open();
								
								String currentDiveNumberString = diveNumber2.getText().toString();
								int currentDiveNumber = Integer.parseInt(currentDiveNumberString);
								
								int currentDiveRowID = db.getRowNumber(currentDiveNumber);
								int lastDiveRowID_totalRowNumbers = (db.getNumberOfRows()-1); // -1 as pos 0 does not hold a value
								int firstDiveRowID =0;
								
								int diveDBEntries = db.getNumberOfRows();
								int lastDiveInDB = db.getLastDiveNumber();
								
								
								int nextDive=0;
								String goToDive="";
								
								// make sure not at last dive or first div so cant go anywhere
								if(currentDiveRowID ==lastDiveRowID_totalRowNumbers){
									
									// move to pos 1 of database if last dive
									
									nextDive = 1;// pos row 1=first entry in Db
									
									goToDive = String.valueOf(nextDive);
									
									// send intent and leave
									
								}
								
//								
								if(currentDiveRowID<lastDiveRowID_totalRowNumbers){
									
								// we can move up a row
									
								 nextDive = currentDiveRowID+1;
								
								// next dive should be the corrsepomdng row of dive number
								//int nextDiveRow =db.getRowNumber(nextDive);
								
								 goToDive = String.valueOf(nextDive);
								
								}
								
//								if(currentDiveNumber==lastDiveInDB){
//									nextDive = currentDiveNumber;
//									
//									// next dive should be the corrsepomdng row of dive number
////									int nextDiveRow =db.getRowNumber(nextDive);
//									// got to first dive in db
//									int nextDiveRow=1;
//									
//									 goToDive = String.valueOf(nextDiveRow);
//									
//								}
								
								//}// outer end if nexyDive!=1
								// if first emtry is a cimulative dve ie 230 and not 1, account for it here
								if(diveDBEntries<=1){
									
									// only one entry socant go back or froward, load current dive
									nextDive = currentDiveNumber;
									goToDive = String.valueOf(nextDive);
									Toast.makeText(getApplicationContext(), "Only one Entry!", Toast.LENGTH_LONG).show();
									
								}
								
								
								else{
								Intent i = new Intent("android.intent.action.VIEWSELECTEDDIVE");
								Bundle extras = new Bundle();
								extras.putString("row", goToDive);
								i.putExtras(extras);
								startActivityForResult(i, 0);
								Log.d("GESTIRE FORWAD TO DVE", "SENDING ROW TO SELECTED TEST: " + goToDive );
								overridePendingTransition(R.anim.anim_slide_in_left,
				                        R.anim.anim_slide_out_left);
								//Toast.makeText(getBaseContext(), "Row selected: "+ row, Toast.LENGTH_LONG).show();
								// free memory and GC
								
								if(photo!=null){
									photo.recycle();
								}
								if(photoBitmap!=null){
									photoBitmap.recycle();
								}
								if(encodedImage!=null){
									encodedImage=null;
								}
								
								finish();//free resources and when backbutton pressed in selected test activity it will go back 
								
								
								
								System.gc();
								
								}	
						}
						
					}// outer ifelse if(absVDiff>flingMin && absVelocityY>velocityMin){
					
					else if(absVDiff>flingMin && absVelocityY>velocityMin){
						
						// vertical swipe detected
						
						  if(verticalDiff>0) {
							  moveToPreviousDive=true;
							  
							 // Toast.makeText(getApplicationContext(), "VERTICAL BACKWARD GESTURE DETECTED", Toast.LENGTH_LONG).show();
							  Log.d(TAG, "SWIPE VERTICAL DETECTED BACKWARD");
						  }
						  
						  
						  else{
							  moveToNextDive=true;
							  
							  Log.d(TAG, "SWIPE VERTICAL FORWARD DETECTED BACKWARD");
							  //Toast.makeText(getApplicationContext(), "VERTICAL FORWARD GESTURE DETECTED", Toast.LENGTH_LONG).show();
						  }
						}

			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			
//			Returning true tells the operating system that your code
//			is interested in the remaining gesture events. 
			Log.d(TAG, "ON DOWN INNER CLASS 1010");
			return true;
		}
		
		
		
		// convense inner class as we only want some gueters deteced,
		// http://code.tutsplus.com/tutorials/android-sdk-detecting-gestures--mobile-21161
		
		
		
		
	} // inner class

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		// free memory and GC
		
		if(photo!=null){
			photo.recycle();
		}
		if(photoBitmap!=null){
			photoBitmap.recycle();
		}
		if(encodedImage!=null){
			encodedImage=null;
		}
		
		finish();//free resources and when backbutton pressed in selected test activity it will go back 
		
		
		
		System.gc();
		
	}

	
	
private void displayDialog() {
		
		// called form on`Clock afer dive is Updated 
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
							
							Intent intt = new Intent(ViewSelectedDive.this, ViewListOfDivesSearch.class );
							startActivity(intt);
							overridePendingTransition(R.anim.cardin, R.anim.cardout);
							
							
							
							// free up meemory
							if(photo!=null){
								photo.recycle();
							}
							if(photoBitmap!=null){
								photoBitmap.recycle();
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
		
	
	
	
}// end class ViewSelectedDive
