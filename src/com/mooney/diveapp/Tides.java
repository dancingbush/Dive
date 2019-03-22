package com.mooney.diveapp;

import com.android.vending.billing.ConnectToGoogleBill;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Tides extends Activity implements OnClickListener{

	private WebView tidesWebSite;
	private Button back, forward, refresh, getURI, serach, saveSearch;
	private EditText url, townNameSearch;
	private InputMethodManager hideKeyboard;//hide keyboard 
	private String  townNameEntered;
	private SharedPreferences preferences;
	private String prefFilename = "MyPrefs";
	private String nameEntered;//web site enetee
	private ProgressDialog pb;
	private static String prefFileName="MyPref";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.tides);
		intilizeWidgets();
		
		
		/*
		 * Get shared pref boolean from ConnectToGoggeBIll activity
		 */
		
		SharedPreferences prefs =this.getSharedPreferences(prefFileName, MODE_PRIVATE);
		boolean purchased= prefs.getBoolean("purchased", false);
		//place add only if boolean flag purcahsed is false
				//ConnectToGoogleBill obj=new ConnectToGoogleBill();
				//obj.oncre
				if (purchased){
					//true, has purschased so hide button
					
					Log.d("TIDES ADDS: ", "PURCHASED FLAG SHOULD BE TRUE= :"+purchased);
					
				}else if(!purchased){
					//true, has purschased so hide button
					AdBuddiz.showAd(this);
					Log.d("TIDES: PURCHASE ", "ADDS IN TIDES SHOWN purcghase = " +purchased);
				}
		
		
		
		//get sahred pref for users preferable location of tide website
		// load the shared prefs object
				preferences = this.getSharedPreferences(prefFilename, MODE_PRIVATE);

				// load the shared pref values in the Edittexts
				townNameEntered = preferences.getString("savedWebsite", null);
		
		//set up our web view client so the Intenet brpwser intent is not called navigating away from app
		
		tidesWebSite.setWebViewClient(new OurWebViewClient());
		//tidesWebSite.setWebChromeClient(new MyWebChromeClient()); 
		tidesWebSite.getSettings().setJavaScriptEnabled(true);
		tidesWebSite.getSettings().setLoadWithOverviewMode(true);
		tidesWebSite.getSettings().setUseWideViewPort(true);
		tidesWebSite.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		tidesWebSite.getSettings().setBuiltInZoomControls(true);
		//use chrome not webKit engine as getting no display
		
		//start dialog progress as soon as page loads and stop on overridden method pageLoad(see below)
		pb = new ProgressDialog(this, pb.STYLE_SPINNER);
		pb.setMessage("Loading Tides....");
		pb.show();
		
		
		
		try{
		if(townNameEntered !=null && townNameEntered!= ""){
			tidesWebSite.loadUrl(townNameEntered);
			Toast.makeText(getApplicationContext(), "Your Saved website has loaded: "+townNameEntered, Toast.LENGTH_SHORT).show();
			
		}else if (townNameEntered == null || townNameEntered == ""){
		//load default website
		tidesWebSite.loadUrl("http://magicseaweed.com/Dublin-Area-Surf-Report/694/Tide/");
		Toast.makeText(getApplicationContext(), "Default website loaded... ", Toast.LENGTH_SHORT).show();
		}
		
		hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
		
		}catch(Exception e){
			e.printStackTrace();
			Dialog d = new Dialog(this);
			d.setTitle("Opening Browser Error");
			TextView tv = new TextView(this);
			tv.setText("Could not connect. Check your internet connection and try again! : Error:\n "+ e.getMessage());
			d.setContentView(tv);
			d.show();
		}
		
	}//end on create

	private void intilizeWidgets() {
		// TODO Auto-generated method stub
		tidesWebSite = (WebView) this.findViewById(R.id.webView1);
		this.townNameSearch = (EditText) this.findViewById(R.id.et_SearchTownName);
		back = (Button) this.findViewById(R.id.button_Back);
		back.setOnClickListener(this);
		forward = (Button) this.findViewById(R.id.button_Forward);
		forward.setOnClickListener(this);
		refresh = (Button) this.findViewById(R.id.button_RefreshPage);
		refresh.setOnClickListener(this);
		getURI = (Button) this.findViewById(R.id.button_Go);
		getURI.setOnClickListener(this);
		url = (EditText) this.findViewById(R.id.et_URL);
		hideKeyboard= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		serach = (Button) this.findViewById(R.id.button_SearchGoogle);
		serach.setOnClickListener(this);
		this.saveSearch = (Button) this.findViewById(R.id.button_SaveURL);
		saveSearch.setOnClickListener(this);
		
	}//end intilise widgets

	@Override
	public void onClick(View v) {
		// ahndle button events and hide keyboard when button clicked using InputMethodManager
		switch (v.getId()){
		
		
		case R.id.button_SearchGoogle:
			nameEntered = townNameSearch.getText().toString();
			if(nameEntered!=null){
				nameEntered = "http://google.ie/?gws_rd=cr&ei=tXXmUqnzIcnH7AbxzoDQDg#q="+nameEntered+"+Tides+Times"; 
			}
			try{
				pb.show();
				tidesWebSite.loadUrl(nameEntered);
				//endable javascript
				}catch(Exception e){
					e.printStackTrace();
					Dialog d = new Dialog(this);
					d.setTitle("Opening Browser Error");
					TextView tv = new TextView(this);
					tv.setText("Could not connect. Check your internet connection and try again! : Error:\n "+ e.getMessage());
					d.setContentView(tv);
					d.show();
				}
			hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
			break;
			
			
		case R.id.button_SaveURL:
			// When saved is clicked, Update shared pref, save data and write to MyPref
						// get the shared pref object and its editor to accept values
						
						preferences = this.getSharedPreferences(prefFilename, MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						// save the values obtained from respective widget inputs edittext
						editor.putString("savedWebsite", tidesWebSite.getUrl());
						editor.commit();
						Toast.makeText(getApplicationContext(), "Website Saved to default!!", Toast.LENGTH_SHORT).show();
						saveSearch.setTextColor(Color.GRAY);
						saveSearch.setEnabled(false);
						hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
			
			break;
			
		case R.id.button_Forward:
			if(tidesWebSite.canGoForward()){
				tidesWebSite.goForward();
				hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
			}
			
			break;
			
		case R.id.button_Back:
			if(tidesWebSite.canGoBack()){
				tidesWebSite.goBack();
			}
			
			break;
			
		case R.id.button_Go:
			//display website
			String getWebsite = url.getText().toString();
			if (getWebsite.contains("http://")){
				pb.show();
			this.tidesWebSite.loadUrl(getWebsite);
			}else{
				getWebsite = "http://"+getWebsite;
				tidesWebSite.loadUrl(getWebsite);
				
			}
			break;
			
		case R.id.button_RefreshPage:
			tidesWebSite.reload();
			break;
		
		}//end switch
		
	}

	
	private class OurWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// define which url we want to override
			view.loadUrl(url);
			/*show progress dialog if not already showing
			if(!pb.isShowing()){
				pb.show();
			}*/
			return true;
			//return super.shouldOverrideUrlLoading(view, url);
		}//end shoulDoVerride...

		@Override
		public void onPageFinished(WebView view, String theurl) {
			// close dialog progress when page loaded
			//super.onPageFinished(view, url);
			if(pb.isShowing()){
				pb.dismiss();
			}
			//hide keybord
			hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
		}//end onPage finished
		
		 @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
             // TODO Auto-generated method stub
             super.onPageStarted(view, url, favicon);
         }
		
		 public void onReceivedError(WebView view, int errorCode,
	                String description, String failingUrl) {
			 //handle conenction errors
	            AlertDialog alert = new AlertDialog.Builder(Tides.this)
	                    .create();
	            alert.setTitle("No connection");
	            alert.setIcon(R.drawable.ic_launcher);
	            alert.setCancelable(false);
	            alert.setMessage("No connection");
	            alert.setButton("OK", new DialogInterface.OnClickListener() {

	                @Override
	                public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();

	                }//end onClik
	            });
	            alert.show();
	        }//end OnReceived eror
	
		
	}//end inner class
	
	
	//web client chrome inner class
	public class MyWebChromeClient extends WebChromeClient {
	    //Handle javascript alerts:
	    @Override
	public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)  
	{
	  Log.d("alert", message);
	  Toast.makeText(getBaseContext(), message, 3000).show();
	  result.confirm();
	  return true;
	};
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}//edn class tides
