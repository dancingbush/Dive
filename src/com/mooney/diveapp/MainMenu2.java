package com.mooney.diveapp;


import com.android.vending.billing.ConnectToGoogleBill;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainMenu2 extends Activity implements OnClickListener{

	
	private Button allTests, buttonTwo, buttonThree, buttonFour, buttonFive, buttonSix,
	buttonSeven, buttonEight, buttonNine, buttonTen, buttonFourandHalf, buttonEleven, buttonTwelve;
	private static String prefFileName="MyPref";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Set up the screen to be full screen with no title bar etc using
				// WindowsManager
				this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
				
				this.setContentView(R.layout.mainmenu);
				 intilizeWidgets();
				 
				 
				 /*cache ads as soon actibvity starts, 
				  * publisher key is from 
				  * https://publishers.adbuddiz.com/portal/support/java login for dive app
				 * whenever we want to place an 
				 * add AdBuddiz.showAd(this); // this = current Activity
				 * For now place in email textfile and webviews
				 */
				 
				 
				 AdBuddiz.setPublisherKey("e1e16bb4-5435-43f3-9d47-2d79facb97a5");
			      AdBuddiz.cacheAds(this); // this = current Activity
		
	}//onCreate

	//second updated main menu
	
	private void intilizeWidgets() {
		// intilize all widgets and set listeners to buttons
		
		allTests = (Button) this.findViewById(R.id.buttonAllTests);
		allTests.setOnClickListener(this);
		buttonTwo = (Button) this.findViewById(R.id.buttonTwo);
		buttonTwo.setOnClickListener(this);
		buttonThree = (Button) this.findViewById(R.id.buttonThree);
		buttonThree.setOnClickListener(this);
		buttonFour = (Button) this.findViewById(R.id.buttonFour);
		buttonFour.setOnClickListener(this);
		buttonFive = (Button) this.findViewById(R.id.buttonFive);
		buttonFive.setOnClickListener(this);
		buttonSix = (Button) this.findViewById(R.id.buttonSix);
		buttonSix.setOnClickListener(this);
		buttonSeven = (Button) this.findViewById(R.id.buttonSeven);
		buttonSeven.setOnClickListener(this);
		buttonEight = (Button) this.findViewById(R.id.buttonEight);
		buttonEight.setOnClickListener(this);
		buttonNine = (Button) this.findViewById(R.id.buttonNine);
		buttonNine.setOnClickListener(this);
		buttonTen = (Button) this.findViewById(R.id.buttonTen);
		buttonTen.setOnClickListener(this);
		buttonEleven = (Button) this.findViewById(R.id.buttonEleven);
		buttonEleven.setOnClickListener(this);
		buttonTwelve = (Button) this.findViewById(R.id.buttonTwelve);
		buttonTwelve.setOnClickListener(this);
		
		
		//remove adds button, if purcaghsed hide button
		
		buttonFourandHalf= (Button) this.findViewById(R.id.buttonFourAndHalf);
		buttonFourandHalf.setOnClickListener(this);
		//boolean purchased=false;
		
		
		/*
		 * Get sharde pref data from GoogleToBill activity
		 * 
		 * 
		 */
		
		SharedPreferences prefs =this.getSharedPreferences(prefFileName, MODE_PRIVATE);
		boolean purchased= prefs.getBoolean("purchased", false);
		//place add only if boolean flag purcahsed is false
				//ConnectToGoogleBill obj=new ConnectToGoogleBill();
				//obj.oncre
				if (purchased){
					//true, has purschased so hide button
					buttonFourandHalf.setVisibility(View.GONE);
					Log.d("MAIN MENU: ", "PURCHASED FLAG SHOULD BE TRUE= :"+purchased);
					
				}else if(!purchased){
					Log.d("MAIN ME PURCHASE BUTTON", "PURCAHSE BUTTON ON: purcghase = " +purchased);
				}
		
		
		
	}//end intilizeWidgets

	
	
	
	
	@Override
	public void onClick(View button) {
		
		// TODO Auto-generated method stub
		
switch (button.getId()){
		
		case R.id.buttonAllTests:
			
			
			Intent callClass = new Intent(MainMenu2.this, Profile.class);
			
			
			startActivity(callClass);
			//overridePendingTransition(R.anim.slidein, R.anim.slideout);
			overridePendingTransition(R.anim.cardin, R.anim.cardout);
			//Intent callClass = new Intent("com.mooneycallans.buckystutorials.ABOUT_US");
			//startActivity(callClass);
			break;
			
		case R.id.buttonTwo:
			
			/*
			 * As website from diff package must call like pacakgename.classname.lass
			 * call webview displaying labtests on line info
			 */
			
			Intent navigateIntentWebview = new Intent(MainMenu2.this, LogDive.class);
			startActivity(navigateIntentWebview);
			//overridePendingTransition(R.anim.slidein, R.anim.slideout);
			overridePendingTransition(R.anim.cardin, R.anim.cardout);
			
			
			
			
			break;
			
		case R.id.buttonThree:
			//dive history
			
			
			Animation animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidedown);
			final Intent navigateIntent = new Intent(MainMenu2.this, DiveHistory.class);
			//button.setAnimation(animate);
			startActivity(navigateIntent);
			//overridePendingTransition(R.anim.slidein, R.anim.slideout);
			overridePendingTransition(R.anim.cardin, R.anim.cardout);
			
			
			break;
			
case R.id.buttonFour:
			
	
	/*
	 * Depening on the OS of the device we will use either a listview with a serachview fucnyion
	 * for ice creame sandwhoch and above (3.0) else use class wthout searchview xml
	 * 
	 * 
	 */
	

	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
		Intent navigateIntent4 = new Intent(MainMenu2.this, ViewListOfDivesSearch.class);
		startActivity(navigateIntent4);
	}else {
	
		Intent navigateIntent4 = new Intent(MainMenu2.this, ViewListOfDives.class);
		startActivity(navigateIntent4);
	}
	
			
			//overridePendingTransition(R.anim.slidein, R.anim.slideout);
			overridePendingTransition(R.anim.cardin, R.anim.cardout);
			break;
			
case R.id.buttonFive:
	
	/*
	 *view gallery
	 */
	
	Intent navigateIntent5 = new Intent(MainMenu2.this, GalleryOfImages.class);
	navigateIntent5.putExtra("key", "http://www.rotunda.ie");
	
	
	startActivity(navigateIntent5);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	break;
	
case R.id.buttonSix:
	
	/*
	 * Emsil dives
	 */
	
	Intent navigateIntent6 = new Intent(MainMenu2.this, EmailDives.class);
	
	
	
	startActivity(navigateIntent6);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	break;
	
case R.id.buttonSeven:
	
Intent navigateIntent7 = new Intent(MainMenu2.this, WindDirection_Surf.class);
	
	
	
	startActivity(navigateIntent7);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	
	break;
	
	
case R.id.buttonEight:
	
Intent navigateIntent8 = new Intent(MainMenu2.this, Tides.class);
	
	
	
	startActivity(navigateIntent8);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	
	break;
	
case R.id.buttonNine:
	
Intent navigateIntent9 = new Intent(MainMenu2.this, About.class);
	
	
	
	startActivity(navigateIntent9);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	
	break;
	//7 wind spedds 8 todes 9 contacts
	
case R.id.buttonTen:
	
Intent navigateIntent10 = new Intent(MainMenu2.this, Checklist.class);
	
	
	
	startActivity(navigateIntent10);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	
	break;
	//7 wind spedds 8 todes 9 contacts
	
case R.id.buttonFourAndHalf:
	
Intent navigateIntent11 = new Intent(MainMenu2.this, ConnectToGoogleBill.class);
	
	//must also get callback from this actovty by pverriding onActiviyFroResult()
	
	startActivity(navigateIntent11);
	//overridePendingTransition(R.anim.slidein, R.anim.slideout);
	overridePendingTransition(R.anim.cardin, R.anim.cardout);
	
	break;
	
	
case R.id.buttonEleven:
	
	// pres dive sites displayed in google map fragment
	
	Intent diveSites = new Intent(MainMenu2.this, Map_DiveSites.class);
	startActivity(diveSites);
	this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	
	break;
	
	
case R.id.buttonTwelve:
	
	Intent diveNews = new Intent(MainMenu2.this, News.class);
	startActivity(diveNews);
	this.overridePendingTransition(R.anim.cardin, R.anim.fadeout);
	
	
		}//end switch
		
		
	}//onClick

	
	
	
	
	
}//end mm2
