package com.mooney.diveapp;



import com.android.vending.billing.ConnectToGoogleBill;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WindDirection_Surf extends Activity implements OnClickListener{

	
	private WebView windWebSite;
	private Button back2, forward2, refresh2, getURI2, serach2, saveSearch2;
	private EditText url2, townNameSearch2;
	private InputMethodManager hideKeyboard2;//hide keyboard 
	private String  townNameEntered2;
	SharedPreferences preferences2;
	String prefFilename = "MyPrefs";
	String nameEntered;//web site enetee
	private ProgressDialog pd;
	private static String prefFileName="MyPref";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.winddirection_surf);
		intilizeWidgets();
		
		//get sahred pref for users preferable location of tide website
		// load the shared prefs object
				this.preferences2 = this.getSharedPreferences(prefFilename, MODE_PRIVATE);

				// load the shared pref values in the Edittexts
				//townNameSearch.setText(preferences.getString("savedWebsite", null));
				townNameEntered2 = preferences2.getString("savedWebsite2", null);
				//firstName.setText(prefs.getString("firstName", null));
		
		//set up our web view client so the Intenet brpwser intent is not called navigating away from app
				
				
				/*
				 * get sharde pref from GoogleIll activoty. Arg is sharde pref xml file name
				 */
				
				
				SharedPreferences prefs =this.getSharedPreferences(prefFileName, MODE_PRIVATE);
				boolean purchased= prefs.getBoolean("purchased", false);
				//place add only if boolean flag purcahsed is false
						//ConnectToGoogleBill obj=new ConnectToGoogleBill();
						//obj.oncre
						if (purchased){
							//true, has purschased so hide button
							
							Log.d("WIND ADDS: ", "PURCHASED FLAG SHOULD BE TRUE= :"+purchased);
							
						}else if(!purchased){
							//true, has purschased so hide button
							AdBuddiz.showAd(this);
							Log.d("WIND: PURCHASE ", "ADDS IN TIDES SHOWN purcghase = " +purchased);
						}
						
						
				//place add only if boolean flag purcahsed is false
						/*
				ConnectToGoogleBill obj= new ConnectToGoogleBill();
				if (obj.isAppPurchased()==false){
					//true, has purschased so hide button
					AdBuddiz.showAd(this);
					Log.d("WINDS ADDS: ", "PURCHASED FLAG SHOULD BE FALSE= :"+obj.isAppPurchased());
				}
				
				*/
				
		windWebSite.getSettings().setJavaScriptEnabled(true);
		windWebSite.getSettings().setLoadWithOverviewMode(true);
		windWebSite.getSettings().setUseWideViewPort(true);
		windWebSite.getSettings().setBuiltInZoomControls(true);
		
		
		windWebSite.setWebViewClient(new OurWebViewClient());
		pd = new ProgressDialog(this, pd.STYLE_HORIZONTAL);
		pd.setMessage("Loading Winds and Surf Conditions....");
		pd.show();
		
		
		
		try{
		if(townNameEntered2 !=null && townNameEntered2 != ""){
			//tidesWebSite.loadUrl(townNameEntered);
			windWebSite.loadUrl(townNameEntered2);
			url2.setText(townNameEntered2);
			Toast.makeText(getApplicationContext(), "Your Saved website has loaded: "+townNameEntered2, Toast.LENGTH_SHORT).show();
			
		}else if(townNameEntered2 ==null || townNameEntered2 == "") {
		//http://magicseaweed.com/World-Surf-Chart/64/
		windWebSite.loadUrl("http://en.windfinder.com/forecast/dublin");
		Toast.makeText(getApplicationContext(), "Default website loaded... ", Toast.LENGTH_SHORT).show();
		//"http://magicseaweed.com/UK-Ireland-Surf-Chart/1/"
		//endable javascript
		}
		
		hideKeyboard2.hideSoftInputFromWindow(url2.getWindowToken(), 0);
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
		windWebSite = (WebView) this.findViewById(R.id.webView2);
		this.townNameSearch2 = (EditText) this.findViewById(R.id.et_SearchTownName2);
		back2 = (Button) this.findViewById(R.id.button_Back2);
		back2.setOnClickListener(this);
		forward2 = (Button) this.findViewById(R.id.button_Forward2);
		forward2.setOnClickListener(this);
		refresh2 = (Button) this.findViewById(R.id.button_RefreshPage2);
		refresh2.setOnClickListener(this);
		getURI2 = (Button) this.findViewById(R.id.button_Go2);
		getURI2.setOnClickListener(this);
		url2 = (EditText) this.findViewById(R.id.et_URL2);
		hideKeyboard2= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		serach2 = (Button) this.findViewById(R.id.button_SearchGoogle2);
		serach2.setOnClickListener(this);
		this.saveSearch2 = (Button) this.findViewById(R.id.button_SaveURL2);
		saveSearch2.setOnClickListener(this);
		
	}//end intilise widgets

	@Override
	public void onClick(View v) {
		// ahndle button events and hide keyboard when button clicked using InputMethodManager
		switch (v.getId()){
		
		
		case R.id.button_SearchGoogle2:
			nameEntered = townNameSearch2.getText().toString();
			if(nameEntered!=null){
				nameEntered = "http://en.windfinder.com/forecast/"+nameEntered; 
			}
			try{
				pd.show();
				windWebSite.loadUrl(nameEntered);
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
			hideKeyboard2.hideSoftInputFromWindow(url2.getWindowToken(), 0);
			break;
			
			
		case R.id.button_SaveURL2:
			// When saved is clicked, Update shared pref, save data and write to MyPref
						// get the shared pref object and its editor to accept values
						
						preferences2 = this.getSharedPreferences(prefFilename, MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences2.edit();
						// save the values obtained from respective widget inputs
						// Edittexts
						editor.putString("savedWebsite2", windWebSite.getUrl());
						//editor.putString("savedWebsite", tidesWebSite.getUrl());
						editor.commit();
						//Log.d("Tides", "savedWebsite: "+savedWebsite);
						Toast.makeText(getApplicationContext(), "Website  Saved to default!!", Toast.LENGTH_SHORT).show();
						this.saveSearch2.setTextColor(Color.GRAY);
						saveSearch2.setEnabled(false);
						hideKeyboard2.hideSoftInputFromWindow(url2.getWindowToken(), 0);
			
			break;
			
		case R.id.button_Forward2:
			if(windWebSite.canGoForward()){
				windWebSite.goForward();
				hideKeyboard2.hideSoftInputFromWindow(url2.getWindowToken(), 0);
			}
			
			break;
			
		case R.id.button_Back2:
			if(windWebSite.canGoBack()){
				windWebSite.goBack();
			}
			
			break;
			
		case R.id.button_Go2:
			//display website
			String getWebsite = url2.getText().toString();
			if (getWebsite.contains("http://")){
			this.windWebSite.loadUrl(getWebsite);
			}else{
				getWebsite = "http://"+getWebsite;
				windWebSite.loadUrl(getWebsite);
				
			}
			break;
			
		case R.id.button_RefreshPage2:
			windWebSite.reload();
			break;
		
		}//end switch
		
	}

	
	private class OurWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// define which url we want to override
			view.loadUrl(url);
			/*
			if(!pd.isShowing()){
				pd.show();
			}*/
			return true;
			//return super.shouldOverrideUrlLoading(view, url);
		}//end shoulDoVerride...

		@Override
		public void onPageFinished(WebView view, String url) {
			// close dialog progress when page loaded
			//super.onPageFinished(view, url);
			if(pd.isShowing()){
				pd.dismiss();
			}
			hideKeyboard2.hideSoftInputFromWindow(url2.getWindowToken(), 0);
		}//end onPage finished
		
		
		
		
		
	}//end inner class
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}//end class wind direction and surf

