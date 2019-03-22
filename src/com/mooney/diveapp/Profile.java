package com.mooney.diveapp;

//control the shared preference data for the profile page

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	// instance varibles/widgets
	SharedPreferences prefs;
	String prefFilename = "MyPref";
	EditText firstName, lastName, insuranceNo, padiNumber, aboutMe, About_Me2;
	Spinner certLevel, yearsExperince;
	String certLevelChoice, yearsExperinceChoice;
	int certIndex, yearsExpIndex;
	ImageView profilePic;
	Button save;
	SeekBar seekBar;
	String[] spinnerCertifcationLevel;
	String[] spinnerYearsOfExp;
	CheckBox male, female;
	RadioGroup sexChoiceButtons;
	RadioButton isMaleButton, isFemaleButton;
	boolean isMale, isFemale;
	String sex;
	ImageButton takeAndSetPhoto;
	Bitmap photo, photoBitmap;
	Intent usePhoneCamera;
	final static int cameraData = 0;// get data from phone photo
	Handler handler;
	ImageView profPic;
	String encodedImage="";
	private Uri mCapturedImageURI;
	private Bitmap defaulPhoto;
	private Bitmap defaultPhoto;
	// for image resizing
	static int w = 250;
	static int h = 250;
	private static final String TAG = LogDive.class.getName();
	private CheckBox specialtyDeep, specialtyDrift,  specialtyNight, specialtyWreck, specialtyPeak, 
	specialtyPhotography, specialtyDrySuit, specialtyRebreather, specialtySideMount, specialtyEAN, specialtyNavigator,
	specialtyEquitment, specialtyDisciverTech, specialtyTec40, specialtyTec45, specialtyTec50;//n=16

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// intilize and create

		super.onCreate(savedInstanceState);
		
//		// Set up the screen to be full screen with no title bar etc using
//				// WindowsManager
//				this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//						WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
				
		this.setContentView(R.layout.profile);
		//this.getWindow().setBackgroundDrawableResource(R.drawable.plane);
		
		//set background image programmictally so image does not strech on smaller screens
		
		intilizeWidgets();

		// load the shared prefs object
		prefs = this.getSharedPreferences(prefFilename, MODE_PRIVATE);
		

		// load the shared pref values in the Edittexts
		firstName.setText(prefs.getString("firstName", null));
		lastName.setText(prefs.getString("lastName", null));
		insuranceNo.setText(prefs.getString("insuranceNumber", null));
		//padiNumber.setText(prefs.getString("padiNum", null));
		aboutMe.setText(prefs.getString("aboutMe", null));
		//aboutMe.setPaintFlags(aboutMe.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		About_Me2.setText(prefs.getString("aboutMe2", null));
		//About_Me2.setPaintFlags(About_Me2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		// underline the textviews
				TextView certifcation = (TextView) this.findViewById(R.id.textView1);
				certifcation.setPaintFlags(certifcation.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				
				TextView diveExperince = (TextView) this.findViewById(R.id.textView2);
				diveExperince.setPaintFlags(diveExperince.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				
				TextView insuranceNo = (TextView) this.findViewById(R.id.textView3);
				insuranceNo.setPaintFlags(insuranceNo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				
				TextView aboutMe = (TextView) this.findViewById(R.id.About_MeText);
				aboutMe.setPaintFlags(aboutMe.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
				
				TextView aboutMe2 = (TextView) this.findViewById(R.id.About_MeText2);
				aboutMe2.setPaintFlags(aboutMe.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		
		//load shared prefs in to checkboxes for sex and specialities (n=16)
		
		boolean maleBoo = prefs.getBoolean("maleValue", false);
		//specialtyDeep, specialtyDrift,  specialtyNight, specialtyWreck, specialtyPeak, 
		//specialtyPhotography, specialtyDrySuit, specialtyRebreather, specialtySideMount, specialtyEAN, specialtyNavigator,
		//specialtyEquitment, specialtyDisciverTech, specialtyTec40, specialtyTec45, specialtyTec50
		
		
		// specialDeep specialDrift specialNight specialWreck specialPeak specialPhotography specialDrySuit specialRebreather 
		// specialSideMount specialEAN  specialNavigator specialEquitment specialDiscoverTech specialTech40 specialTech45 specialTech50
		male.setChecked(prefs.getBoolean("maleValue", false));
		female.setChecked(prefs.getBoolean("femaleValue", false));
		boolean femaleBoo = prefs.getBoolean("femaleValue", false);
		specialtyDeep.setChecked(prefs.getBoolean("specialDeep", false));
		specialtyDrift.setChecked(prefs.getBoolean("specialDrift", false));
		specialtyNight.setChecked(prefs.getBoolean("specialNight", false));
		specialtyWreck.setChecked(prefs.getBoolean("specialWreck", false));
		specialtyPeak.setChecked(prefs.getBoolean("specialPeak", false));
		specialtyPhotography.setChecked(prefs.getBoolean("specialPhotography", false));
		specialtyDrySuit.setChecked(prefs.getBoolean("specialDrySuit", false));
		specialtyRebreather.setChecked(prefs.getBoolean("specialRebreather ", false));
		specialtySideMount.setChecked(prefs.getBoolean("specialSideMount", false));
		specialtyEAN.setChecked(prefs.getBoolean("specialEAN", false));
		specialtyNavigator.setChecked(prefs.getBoolean("specialNavigator", false));
		specialtyEquitment.setChecked(prefs.getBoolean("specialEquitment", false));
		specialtyDisciverTech.setChecked(prefs.getBoolean("specialDiscoverTech", false));
		specialtyTec40.setChecked(prefs.getBoolean("specialTech40", false));
		specialtyTec45.setChecked(prefs.getBoolean("specialTech45", false));
		specialtyTec50.setChecked(prefs.getBoolean("specialTech50", false));
		
		

		certLevel.setSelection(prefs.getInt("certLevel", 0));
		yearsExperince.setSelection(prefs.getInt("yearsExp", 0));

		
		/*
		 * Need to craete a defaul Bitmap base64 string to set profile pic or
		 * will crash with NulPoinetrException set the the default Bitmap photo
		 * object for Bitmap photo by wrapping a InputStrem from our resources
		 * files in a BitMapFactprty decoder
		 */
		try{
		InputStream photoBytes = this.getResources().openRawResource(
				R.drawable.logdive);
		defaultPhoto = BitmapFactory.decodeStream(photoBytes);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		defaultPhoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String defaultImage = Base64.encodeToString(b, Base64.DEFAULT);
		byte[] imageAsBytes = Base64
				.decode(prefs.getString("imageEncoded", null),
						Base64.DEFAULT);
		// now resize image as bytes
		String checkEndocedIMage = encodedImage;
		
		Bitmap image1=null;
		//Toast.makeText(getApplicationContext(), "imagedencode="+checkEndocedIMage , Toast.LENGTH_SHORT).show();
		if(encodedImage != null){
			try{
				
System.gc();
				//Uri imageBytes = encodedImage.
		image1 = BitmapFactory.decodeByteArray(imageAsBytes, 0,
				imageAsBytes.length);
		
		Bitmap imageResSized = reSizeImage(image1);
		
		takeAndSetPhoto.setImageBitmap(imageResSized);
		
			}catch(OutOfMemoryError ex){
				
				System.gc();
				// cathc image and try to resize 
				Log.d(TAG, "211: CATCHING OPUT OF MEMPRY IMAGE AND TRYOINGA AGINA");
				ex.printStackTrace();
				baos=new  ByteArrayOutputStream();
				image1.compress(Bitmap.CompressFormat.JPEG,50, baos);
	            b=baos.toByteArray();
	            Bitmap imageResSized = reSizeImage(image1);
	    		
	    		takeAndSetPhoto.setImageBitmap(imageResSized);
	            //temp=Base64.encodeToString(b, Base64.DEFAULT);
	            Log.e("EWN", "Out of memory error catched");
				
			}catch (NullPointerException e){
				//set defaul image
				takeAndSetPhoto.setImageResource(R.drawable.noimageselected);
			}
		}else{
			//set defaul image
			takeAndSetPhoto.setImageResource(R.drawable.noimageselected);
		}
		}catch(Exception ex){
			Log.d(TAG, "Exception loading shared prefs: "+ ex.getMessage());
			takeAndSetPhoto.setImageResource(R.drawable.noimageselected);
		}
	}// end onCreate
	
	

	private void intilizeWidgets() {
		// intilalise widgets
		
		handler = new Handler();
		takeAndSetPhoto = (ImageButton) this.findViewById(R.id.ib_profileImage);
		takeAndSetPhoto.setOnClickListener(this);
		firstName = (EditText) this.findViewById(R.id.et_firstName);
		lastName = (EditText) this.findViewById(R.id.et_lastName);
		insuranceNo = (EditText) this.findViewById(R.id.et_insuranceNumber);
		//padiNumber = (EditText) this.findViewById(R.id.et_padiNumber);
		aboutMe = (EditText) this.findViewById(R.id.About_Me);
		About_Me2 = (EditText) this.findViewById(R.id.About_Me2);
		yearsExperince = (Spinner) this.findViewById(R.id.spinner_dive_exp);
		seekBar = (SeekBar) this.seekBar;
		save = (Button) findViewById(R.id.button_SaveProfile);
		save.setOnClickListener(this);

		
	
		//table holding all the speciality checkboxes (16) and the onClickListeners
		
		 specialtyDeep = (CheckBox) this.findViewById(R.id.cb_specilalityCB_DeepDive);
		 specialtyDeep.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// when male is checked
					if (((CheckBox) v).isChecked()) {
						
						specialtyDeep.setChecked(true);
						
					}
				}
			});// end on click 
		 specialtyDrift= (CheckBox) this.findViewById(R.id.cb_specilalityCB_Drift);
		 specialtyDrift.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// when male is checked
					if (((CheckBox) v).isChecked()) {
						
						specialtyDrift.setChecked(true);
						
					}
				}
			});// end on click 
		 specialtyNight=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Night_Diver);
		 specialtyNight.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// when male is checked
					if (((CheckBox) v).isChecked()) {
						
						specialtyNight.setChecked(true);
						
					}
				}
			});// end on click 
		 specialtyWreck=(CheckBox) this.findViewById(R.id.cb_specilalityCB_WreckDive);
		 specialtyWreck.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// when male is checked
					if (((CheckBox) v).isChecked()) {
						
						specialtyWreck.setChecked(true);
						
					}
				}
			});// end on click 
		 specialtyPeak=(CheckBox) this.findViewById(R.id.cb_specilalityCB_PeakBouyancy);
		 specialtyPeak.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// when male is checked
					if (((CheckBox) v).isChecked()) {
						
						specialtyPeak.setChecked(true);
						
					}
				}
			});// end on click 
		specialtyPhotography=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Photograpgy);
		specialtyPhotography.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyPhotography.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyDrySuit=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Dry_Suit);
		specialtyDrySuit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyDrySuit.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyRebreather=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Rebreather);
		specialtyRebreather.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyRebreather.setChecked(true);
					
				}
			}
		});// end on click 
		specialtySideMount=(CheckBox) this.findViewById(R.id.cb_specilalityCB_SideMount);
		specialtySideMount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtySideMount.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyEAN=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Enriched_Air);
		specialtyEAN.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyEAN.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyNavigator=(CheckBox) this.findViewById(R.id.cb_specilalityCB_UnderWater_Navigator);
		specialtyNavigator.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyDeep.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyEquitment=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Equitment);
		specialtyEquitment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyEquitment.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyDisciverTech=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Discover_Tech);
		specialtyDisciverTech.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyDisciverTech.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyTec40=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Tec40);
		specialtyTec40.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyTec40.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyTec45=(CheckBox) this.findViewById(R.id.cb_specilalityCB_Tec45);
		specialtyTec45.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyTec45.setChecked(true);
					
				}
			}
		});// end on click 
		specialtyTec50=(CheckBox) this.findViewById(R.id.cb_specilalityCB_tec50);
		specialtyTec50.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					
					specialtyTec50.setChecked(true);
					
				}
			}
		});// end on click 
		
		
		
		
		
		
		// spinners CertLevel or drop downs
		this.spinnerCertifcationLevel = this.getResources().getStringArray(
				R.array.certifcation_arrays);
		certLevel = (Spinner) this.findViewById(R.id.spinner_certifcation);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item,
				spinnerCertifcationLevel);
		certLevel.setAdapter(adapter);
		
		certLevel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				// add colour with textbox, add tv dynamiccaly as a child to spinner
				
				//((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				
				certIndex = parent.getSelectedItemPosition();
				certLevelChoice = spinnerCertifcationLevel[certIndex];
				
				// add colour with textbox
				//((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				
				

			}// end onitemselected

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// this must be implement in case nothings selected, add textbox dynamically with text color
				
				((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
				
			}// end onNOthingSeltedd

		});// end new onItemSelceted

		// spinners or drop downs for years exp
		this.spinnerYearsOfExp = this.getResources().getStringArray(
				R.array.exp_arrays);
		this.yearsExperince = (Spinner) this
				.findViewById(R.id.spinner_dive_exp);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item,
				spinnerYearsOfExp);
		yearsExperince.setAdapter(adapter2);
		yearsExperince.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View arg1,
					int arg2, long arg3) {
				// add tv dynamically so can color spinner text
				
				//((TextView)parentView.getChildAt(0)).setTextColor(Color.BLACK);
				
				yearsExpIndex = parentView.getSelectedItemPosition();
				yearsExperinceChoice = spinnerYearsOfExp[yearsExpIndex];
				

			}// end onitemselected

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// this must be implementd in case nothingis selec ted
				Toast.makeText(getBaseContext(), "Nothing Selcetd",
						Toast.LENGTH_SHORT);

				((TextView)parentView.getChildAt(0)).setTextColor(Color.BLACK);
			}// end onNOthingSeltedd
//no code req
		});// end new onItemSelceted
		
		
		
		

		// check box male and feamle
		male = (CheckBox) this.findViewById(R.id.cb_MalePrfofile);
		male.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					sex = "Male";
					// malePref=true;
					// femalePref=false;
					female.setChecked(false);
					
				}
			}
		});// end on click fro male

		// if female check box is checked
		female = (CheckBox) this.findViewById(R.id.cb_FemalePrfofile);
		female.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// when male is checked
				if (((CheckBox) v).isChecked()) {
					sex = "Female";
					// femalePref=true;
					// malePref=false;
					male.setChecked(false);
					
				}
			}// end onClick/if
		});// end on click call for male
	}// end intilialise widgets

	
	
	
	// file
	@Override
	public void onClick(View buttonClicked) {
		
		// sort whether ImageButton or Save button clicked

		switch (buttonClicked.getId()) {
		case R.id.button_SaveProfile:
			// When saved is clicked, Update shared pref, save data and write to MyPref
			// get the shared pref object and its editor to accept values
			
			this.prefs = this.getSharedPreferences(prefFilename, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			// save the values obtained from respective widget inputs
			// Edittexts
			editor.putString("firstName", this.firstName.getText().toString());
			editor.putString("lastName", this.lastName.getText().toString());
			editor.putString("insuranceNumber", this.insuranceNo.getText()
					.toString());
			//editor.putString("padiNum", this.padiNumber.getText().toString());
			editor.putString("aboutMe", this.aboutMe.getText().toString());
			editor.putString("aboutMe2", this.About_Me2.getText().toString());
//About_Me2.setText(prefs.getString("aboutMe2", null));
			
			
			// All specilaity and sex Checkboxes
						if (male.isChecked()) {
							// boolean maleValue=true;
							editor.putBoolean("maleValue", true);
							
							editor.commit();
						}
							else if (male.isChecked()==false){
								editor.putBoolean("maleValue", false);
								editor.commit();
							}
					
					
				if (female.isChecked()) {
							boolean femaleValue = true;
							editor.putBoolean("femaleValue", true);
							editor.commit();
						}else if (female.isChecked()==false){
							boolean femaleValue = false;
							editor.putBoolean("femaleValue", false);
							editor.commit();
						}// end esle

				if (specialtyDeep.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialDeep", true);
					
					editor.commit();
				}
					else if (specialtyDeep.isChecked()==false){
						editor.putBoolean("specialDeep", false);
						editor.commit();
					}
			
				
				// specialDeep specialDrift specialNight specialWreck specialPeak specialPhotography specialDrySuit specialRebreather 
				// specialSideMount specialEAN  specialNavigator specialEquitment specialDiscoverTech specialTech40 specialTech45 specialTech50
				
				if (specialtyDrift.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialDrift", true);
					
					editor.commit();
				}
					else if (specialtyDrift.isChecked()==false){
						editor.putBoolean("specialDrift", false);
						editor.commit();
					}
				
				
				 if ( specialtyNight.isChecked()) {
						// boolean maleValue=true;
						editor.putBoolean("specialNight", true);
						
						editor.commit();
					}
						else if ( specialtyNight.isChecked()==false){
							editor.putBoolean("specialNight", false);
							editor.commit();
						}
				 
				 
				 //specialWreck 
				 
				 
				 if ( specialtyWreck.isChecked()) {
						// boolean maleValue=true;
						editor.putBoolean("specialWreck", true);
						
						editor.commit();
					}
						else if ( specialtyWreck.isChecked()==false){
							editor.putBoolean("specialWreck", false);
							editor.commit();
						}
				 
				 
				 if ( specialtyPeak.isChecked()) {
						// boolean maleValue=true;
						editor.putBoolean("specialPeak", true);
						
						editor.commit();
					}
						else if ( specialtyPeak.isChecked()==false){
							editor.putBoolean("specialPeak", false);
							editor.commit();
						}
				 
				
				 if ( specialtyPhotography.isChecked()) {
						// boolean maleValue=true;
						editor.putBoolean("specialPhotography", true);
						
						editor.commit();
					}
						else if ( specialtyPhotography.isChecked()==false){
							editor.putBoolean("specialPhotography", false);
							editor.commit();
						}
				
				
				 if ( specialtyDrySuit.isChecked()) {
						// boolean maleValue=true;
						editor.putBoolean("specialDrySuit", true);
						
						editor.commit();
					}
						else if ( specialtyDrySuit.isChecked()==false){
							editor.putBoolean("specialDrySuit", false);
							editor.commit();
						}
				
				 if ( specialtyRebreather.isChecked()) {
						// boolean maleValue=true;
						editor.putBoolean("specialRebreather", true);
						
						editor.commit();
					}
						else if ( specialtyRebreather.isChecked()==false){
							editor.putBoolean("specialRebreather", false);
							editor.commit();
						}
				
				if ( specialtySideMount.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialSideMount", true);
					
					editor.commit();
				}
					else if ( specialtySideMount.isChecked()==false){
						editor.putBoolean("specialSideMount", false);
						editor.commit();
					}
				
				
				
				if ( specialtyEAN.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialEAN", true);
					
					editor.commit();
				}
					else if ( specialtyEAN.isChecked()==false){
						editor.putBoolean("specialEAN", false);
						editor.commit();
					}
				
				
				
				if ( specialtyNavigator.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialNavigator", true);
					
					editor.commit();
				}
					else if ( specialtyNavigator.isChecked()==false){
						editor.putBoolean("specialNavigator", false);
						editor.commit();
					}
				
				
				

				if ( specialtyEquitment.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialEquitment", true);
					
					editor.commit();
				}
					else if ( specialtyEquitment.isChecked()==false){
						editor.putBoolean("specialEquitment", false);
						editor.commit();
					}
				
				

				if ( specialtyDisciverTech.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialDiscoverTech", true);
					
					editor.commit();
				}
					else if ( specialtyDisciverTech.isChecked()==false){
						editor.putBoolean("specialDiscoverTech", false);
						editor.commit();
					}
				
				
				
				
				

				if ( specialtyTec40.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialTech40", true);
					
					editor.commit();
				}
					else if ( specialtyTec40.isChecked()==false){
						editor.putBoolean("specialTech40", false);
						editor.commit();
					}
				
				
				
				
				if ( specialtyTec45.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialTech45", true);
					
					editor.commit();
				}
					else if ( specialtyTec45.isChecked()==false){
						editor.putBoolean("specialTech45", false);
						editor.commit();
					}
				
				
				
				if ( specialtyTec50.isChecked()) {
					// boolean maleValue=true;
					editor.putBoolean("specialTech50", true);
					
					editor.commit();
				}
					else if ( specialtyTec50.isChecked()==false){
						editor.putBoolean("specialTech50", false);
						editor.commit();
					}
				
				//end checkboxes commits
				
				

			// spinners
			editor.putInt("certLevel", certIndex);
			editor.putInt("yearsExp", yearsExpIndex);
			editor.commit();

			

			// save the camera image in its base64 string form taken from camear
			//if imagEncoded = null or "" then donet save!!
			
			if(encodedImage =="" || encodedImage ==null){
				//do nothing
			}else{
				//save what photo we have to shared pref 
			editor.putString("imageEncoded", encodedImage);

			editor.commit();
			
			}
			
			
			Toast.makeText(this.getBaseContext(), "Saved", Toast.LENGTH_SHORT)
			.show();
	this.finish();
	
	
	//memory mop up
	if(encodedImage!=null ){
		encodedImage=null;
	}
			if(photo!=null){
				photo.recycle();
				
			}
			if(photoBitmap!=null){
				photoBitmap.recycle();
				
			}
			if(defaultPhoto!=null){
				defaultPhoto.recycle();
				
			}
			System.gc();
			finish();
			
			break;
			

		// take photo
		case R.id.ib_profileImage:
			// access the phones camera with a new Intent get data returned from photo
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

						}// end onclick
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

	}// end onClick

	// override Activity method onActivityresut to check data returned form
	// camera, then get the photo and store in instance variable
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {
			
			
			// choice is get photo from sd card, note this Intent only returns a
			// URi and not a bitmap so must convert the Uri onto Bitmap
			if (resultCode == Activity.RESULT_OK) {
				
				Uri selectedImage = data.getData();
				if (selectedImage != null) {
					try {
						
						// rediuec image size before it gets to RAM
//						photo = android.provider.MediaStore.Images.Media
//								.getBitmap(this.getContentResolver(),
//										selectedImage);
						Bitmap bm = null; 
						BitmapFactory.Options options = new BitmapFactory.Options(); 
						options.inSampleSize = 6; 
						AssetFileDescriptor fileDescriptor =null; 
						
						
							
							//Uri selectedImage = data.getData();
						fileDescriptor = getContentResolver()
								.openAssetFileDescriptor(selectedImage,"r"); 
						
						photo = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options); 
						
						fileDescriptor.close(); 
						
						
					} catch (FileNotFoundException e) {
						Toast.makeText(getBaseContext(), "Bitmap not found!!",
								Toast.LENGTH_SHORT).show();
					} catch (IOException ex) {
						Toast.makeText(getBaseContext(),
								"IO Exception retriving Image!!",
								Toast.LENGTH_SHORT).show();
					}// end catch
				}// if selected image

				// resize photo
				photoBitmap = reSizeImage(photo);
				photoBitmap = photo;
				takeAndSetPhoto.setImageBitmap(photoBitmap);
				takeAndSetPhoto.setScaleType(ScaleType.FIT_XY);
				
				
				// convert the bitmap to binary array and base64 string
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] b = baos.toByteArray();
				encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
			
			} // if result is get from sd card
			
			
		} else if (requestCode == cameraData) {
			// photo taken from camera
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(getBaseContext(), "resultCode=0",
						Toast.LENGTH_SHORT).show();
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						handler.post(new Runnable() {

							@Override
							public void run() {
								// use a Bundle object to get data from the Intent,
								// Bundle holed the value data in key:value pairs 
								Bundle extras = data.getExtras();

								
								//if data.getData does not return a Uri, then the photo data is in extras
								if(data.getData()==null){//no uri
								    photo = (Bitmap)data.getExtras().get("data");
								}else{
								    try {
								    	Uri selectedImage = data.getData();
								    	Bitmap bm = null; 
										BitmapFactory.Options options = new BitmapFactory.Options(); 
										options.inSampleSize = 6; 
										AssetFileDescriptor fileDescriptor =null; 
										
										
											
											//Uri selectedImage = data.getData();
										fileDescriptor = getContentResolver()
												.openAssetFileDescriptor(selectedImage,"r"); 
										
										photo = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options); 
										
										fileDescriptor.close(); 
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
								
								//photo = (Bitmap) extras.get("data");
								
								photoBitmap = reSizeImage(photo);
								//photoBitmap = photo;
								//Uri photoShot = data.getData();
								// now pass the Bitmap image to the Image button
								
								takeAndSetPhoto.setImageBitmap(photoBitmap);
								takeAndSetPhoto.setScaleType(ScaleType.FIT_XY);
								// convert the bitmap to binary array then base64 string
								ByteArrayOutputStream baos = new ByteArrayOutputStream();

								photoBitmap.compress(
										Bitmap.CompressFormat.JPEG, 100, baos);
								byte[] b = baos.toByteArray(); 
								encodedImage = Base64.encodeToString(b,
										Base64.DEFAULT);
								Toast.makeText(getBaseContext(),
										"Image set to profile!",
										Toast.LENGTH_SHORT).show();
							}// end inner run

						});// end new runnable

					}

				};
				new Thread(runnable).start();

			}// end if result OK
		}// end else resultCode ==2
	}// end onActivity

	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedID) {
		// // RadioButtons male or female
		if (isMaleButton.isChecked()) {
			isMale = true;
			
		} else {
			isFemale = true;
		}

	}// end onCheckChanged for radioButtons

	public Bitmap reSizeImage(Bitmap bitmapImage) {
		// resize bitmap image passed and rerun new one
		Bitmap resizedImage = null;
		float factorH = h / (float) bitmapImage.getHeight();
		float factorW = w / (float) bitmapImage.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		
		//photo before resizing
		Log.d(TAG, "PHTO BEFORE RESIZING, HEIGHT: " + bitmapImage.getHeight() + " WIDTH: " +bitmapImage.getWidth());
		
		try {
			resizedImage = Bitmap.createScaledBitmap(bitmapImage,
					(int) (bitmapImage.getWidth() * factorToUse),
					(int) (bitmapImage.getHeight() * factorToUse), false);
			
			//photo before resizing
			Log.d(TAG, "PHTO AFTER RESIZING, HEIGHT: " + resizedImage.getHeight() + " WIDTH: " +resizedImage.getWidth());
			
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "Problem resizing Image @Line 510+");
			e.printStackTrace();
		}
		Log.d(TAG,
				"in resixed, value of resized image: "
						+ resizedImage.toString());
		return resizedImage;
	}// end reSize

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		//photo, photoBitmap
//		if(encodedImage!=null ){
//			encodedImage=null;
//		}
		if(photo!=null){
			photo=null;
			
		}
		if(photoBitmap!=null){
			photoBitmap=null;
			
		}
		if(defaultPhoto!=null){
			defaultPhoto=null;
			
		}
		System.gc();
		finish();
	}
}// end class

