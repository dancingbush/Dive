package com.mooney.diveapp;

import android.app.Activity;


	import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

	import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

	public class MainMenu3 extends Activity {
		
		
		/*
		 * Menu 3: More Options navigates to MainMenu2 xml/java
		 * Two menu options on the one menu
		 * 
		 * 
		 */

		private  String TAG = "MAIN MENU 3";

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			Log.d(TAG, "ON CREATE()");
			
			// Set up the screen to be full screen with no title bar etc using
			// WindowsManager
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			
			setContentView(R.layout.mainmenu3);
			
			
			 // Profile click
			TextView menu_about= (TextView) findViewById(R.id.menu_about);
			menu_about.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent (MainMenu3.this, Profile.class);
	            	startActivity(intent);
	            	overridePendingTransition(R.anim.anim_slide_in_left,
	                        R.anim.anim_slide_out_left);
				}
			});
			
			 //Log a dive click
			TextView menu_portfolio= (TextView) findViewById(R.id.menu_portfolio);
			menu_portfolio.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent (MainMenu3.this, LogDive.class);
	            	startActivity(intent);
	            	overridePendingTransition(R.anim.anim_slide_in_left,
	                        R.anim.anim_slide_out_left);
				}
			});
			
			
			
			 // Map of All Dive Sites
			TextView menu_web= (TextView) findViewById(R.id.menu_web);
			menu_web.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.web_url)));
					
					Intent browserIntent = new Intent(MainMenu3.this, Map_DiveSites.class);
					startActivity(browserIntent);
					overridePendingTransition(R.anim.anim_slide_in_left,
	                        R.anim.anim_slide_out_left);
				}
			});
			
			
			
			 // view list of dives from DB
			TextView menu_media= (TextView) findViewById(R.id.menu_media);
			menu_media.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
	
					
					/*
					 * Depening on the OS of the device we will use either a listview with a serachview fucntion
					 * for ice creame sandwhoch and above (3.0) else use class without searchview xml
					 * 
					 * 
					 */
					

					if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
						Intent navigateIntent4 = new Intent(MainMenu3.this, ViewListOfDivesSearch.class);
						startActivity(navigateIntent4);
						overridePendingTransition(R.anim.cardin, R.anim.cardout);
					}else {
					
						Intent navigateIntent4 = new Intent(MainMenu3.this, ViewListOfDives.class);
						startActivity(navigateIntent4);
						overridePendingTransition(R.anim.cardin, R.anim.cardout);
					}
					
							
							

					
				}
			});
			
			
			
			
			 // Menu gallery click
			TextView menu_gallery= (TextView) findViewById(R.id.menu_gallery);
			menu_gallery.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent (MainMenu3.this, GalleryOfImages.class);
	            	startActivity(intent);
	            	overridePendingTransition(R.anim.anim_slide_in_left,
	                        R.anim.anim_slide_out_left);
				}
			});
			
			
			
			
			// More options click: go to main menu 2
			
			TextView menu_contact= (TextView) findViewById(R.id.menu_contact);
			menu_contact.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// getting a relative layout circular depncy
					Intent intent=new Intent (MainMenu3.this, MainMenu2.class);
	           	startActivity(intent);
	           	overridePendingTransition(R.anim.anim_slide_in_left,
	                    R.anim.anim_slide_out_left);
				}
			});
			
		}
		
		
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}



		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
		}



		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
		}



		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			Log.d(TAG, "ON PAUSE()");
		}



		@Override
		protected void onRestart() {
			// TODO Auto-generated method stub
			super.onRestart();
			Log.d(TAG, "ON RESTART()");
		}



		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			Log.d(TAG, "ON RESUME()");
		}



		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			Log.d(TAG, "ON STOP()");
			super.onStop();
		}
		
//		  @Override
//		  public boolean onOptionsItemSelected(MenuItem item) {
//		    switch (item.getItemId()) {
//		    case R.id.action_rate:
//		    	Intent i = new Intent(Intent.ACTION_VIEW);
//		    	i.setData(Uri.parse("market://details?id=" + getPackageName()));
//		    	startActivity(i);
//		      break;
//		    case R.id.action_exit:
//	        	Intent in = new Intent();
//	            in.setAction(Intent.ACTION_MAIN);
//	            in.addCategory(Intent.CATEGORY_HOME);
//	            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	            MainMenu3.this.startActivity(in);
//			       finish();
//	               android.os.Process.killProcess(android.os.Process.myPid());
//	               System.exit(0);
//	               getParent().finish();
//			      break;
//		    default:
//		      break;
//		    }
//
//		    return true;
//		  }

		
	}
