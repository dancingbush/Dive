package com.mooney.diveapp;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.view.View;


/*
 * Customized Activity class for the splash screen and media player for sound
 *open a new thread to allow flash to show for 5 seconds 
 *Followed by the ain activity definined in the activity manifast XML
*/


public class SplashScreen extends Activity{

	MediaPlayer ourSong=null;//plays the song in this activity
	Handler songThread;
	boolean musicOn_Off=true;
	//private String[] imageSelection;
	
	@Override
	protected void onCreate(Bundle diveApp) {
		
		super.onCreate(diveApp);
		
		int[] imageSelection= {R.drawable.jellyfish, R.drawable.splash3, R.drawable.splash5, R.drawable.splash6,
				R.drawable.splash7, R.drawable.splash8, R.drawable.splash13, R.drawable.splash14, R.drawable.splash15};
		Random whichImage = new Random();
		int theImage = whichImage.nextInt(imageSelection.length);
		
		
		//this.setContentView(R.layout.splashscreen);
		//View myView = findViewById(R.layout.splashscreen);
		//myView.setBackgroundDrawable(imageSelection[theImage]);
		this.setContentView(R.layout.splashscreen);
		//myView.setBackgroundResource(imageSelection[theImage]);
		//myView.setBackgroundResource(getResources().getDrawable(imageSelection[theImage]));
		this.getWindow().setBackgroundDrawableResource(imageSelection[theImage]);
		
		
		//first get the users prfernce wiethre music shoudl be played or not, from the res/menu/cool_menu linked to 
		//res/xml/pref.xml checkbox tag
		
		//SharedPreferences getMusicPrefernce = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
		
		//musicOn_Off = getMusicPrefernce.getBoolean("checkbox", true);//key=checkbox as defined in res/aml/prefs attribute for checkbox tag, return true if nothing selected (default value)
		
		
		Thread playSong = new Thread(new Runnable(){
			public void run(){
				if(musicOn_Off){
						//ourSong= MediaPlayer.create(SplashScreen.this, R.raw.sea);
						//ourSong.start();
				}//end if
				//run starts a new thread sep from main UI thread
			}//end outer run
			
		});//end playSong Thread
		playSong.start();
		
		//set up a new thread to allow splash screen to display for 5 seconds
		Thread timer = new Thread(new Runnable(){
			public void run(){
				try{
					//sleep for 5 seconds to let splash activity display
				Thread.sleep(5000);//milliseconds
					//ourSong.start();
					
					
				}catch(InterruptedException ex){
					//if interruoted thread
					ex.printStackTrace();
				}//end catch
				finally{
					//start the new activity after the splash, defined in the manifast file XML
				
					//Intent openStartingPoint= new Intent("com.mooney.diveapp.MAINMENU2");
					Intent openStartingPoint= new Intent(SplashScreen.this, MainMenu3.class);
					//the phone will implicity call the splash activity onPause() method here so the next activity can run
					startActivity(openStartingPoint);//from activity superclass
					overridePendingTransition(R.anim.fadein, R.anim.fadeout);
					
				}//end finally
			}//end run
			
		});//end thread
		//start the timer
		timer.start();
	}//end onCtrate

	//when phone calls the onPause() for splash activity (part of actvitity life cycle) we want to
	//over ride so we can quit the splash activity and free resources
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		/*
		if(musicOn_Off){
		
		float left=(float) 1.0;
		float right=(float) 1.0;
		//try to add fade out affect on sound
		while (left >0){
			ourSong.setVolume(left, right);
		left-= 0.2;
		right-=0.2;
		}
		*/
		//ourSong.release();//relase all media resources
		finish();
		System.gc();
		//turn teh mp3 off so does not play innto next activity
		//}//end if music switch n
	}

	
}//end splash

