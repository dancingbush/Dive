package com.mooney.diveapp;


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
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class News extends Activity {

	
	/*
	 * Use a webview to display news website
	 * Phone OS should check if internet availble or not
	 * 
	 */
	
	private WebView newsWebSite;
	private String webAddress="http://www.sportdiver.com/news";
	
	private InputMethodManager hideKeyboard;//hide keyboard 
	
	private SharedPreferences preferences;
	private String prefFilename = "MyPrefs";
	private String nameEntered;//web site enetee
	private ProgressDialog pb;
	private static String prefFileName="MyPref";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.newswebsite);
	//intilizeWidgets();
	
	
	/*
	 * Get shared pref boolean from ConnectToGoggeBIll activity
	 * If purchased app do not display adds
	 * If not display an add
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
	
	
	// load the shared prefs object
			preferences = this.getSharedPreferences(prefFilename, MODE_PRIVATE);

		
	
	//set up our web view client so the Intenet brpwser intent is not called navigating away from app
	
			newsWebSite = (WebView) this.findViewById(R.id.webview_newsWebsite);
	newsWebSite.setWebViewClient(new OurWebViewClient());
	//tidesWebSite.setWebChromeClient(new MyWebChromeClient()); 
	newsWebSite.getSettings().setJavaScriptEnabled(true);
	newsWebSite.getSettings().setLoadWithOverviewMode(true);
	newsWebSite.getSettings().setUseWideViewPort(true);
	newsWebSite.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	newsWebSite.getSettings().setBuiltInZoomControls(true);
	//use chrome not webKit engine as getting no display
	
	//start dialog progress as soon as page loads and stop on overridden method pageLoad(see below)
	pb = new ProgressDialog(this, pb.STYLE_SPINNER);
	pb.setMessage("Loading Latest News ....");
	pb.show();
	
	
	
	try{
		newsWebSite.loadUrl(this.webAddress);
		//Toast.makeText(getApplicationContext(), "Your Saved website has loaded: "+townNameEntered, Toast.LENGTH_SHORT).show();
		
	
	
	//hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
	
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
	newsWebSite = (WebView) this.findViewById(R.id.webView1);
	
	
}//end intilise widgets



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
		//hideKeyboard.hideSoftInputFromWindow(url.getWindowToken(), 0);
	}//end onPage finished
	
	 @Override
     public void onPageStarted(WebView view, String url, Bitmap favicon) {
         // TODO Auto-generated method stub
         super.onPageStarted(view, url, favicon);
     }
	
	 public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
		 //handle conenction errors
            AlertDialog alert = new AlertDialog.Builder(News.this)
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

}
