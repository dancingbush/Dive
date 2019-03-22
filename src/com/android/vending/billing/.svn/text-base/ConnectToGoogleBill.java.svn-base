package com.android.vending.billing;


import com.mooney.diveapp.MainMenu2;
import com.mooney.diveapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ConnectToGoogleBill extends Activity implements OnClickListener{

	/*
	 * Connect to google bill play servoce by calling this class
	 * Tutorial form http://developer.android.com/training/in-app-billing/preparing-iab-app.html#GetSample
	 * 
	 * 
	 */
	private IabHelper mHelper;
	private String TAG="GOOGLEBILLING: ";
	public static boolean purchased=false;
	private static SharedPreferences prefs;
	private static String prefFileName="MyPref";
	private String productID="add_free_version";//aarg for luanchPurchaseFlow
	private String testProduct="android.test.purchased";
	// (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    private Button purchaseButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.purchase);
		
		
		
		//get button and set to disabled until asynch set up complelet
		purchaseButton = (Button) this.findViewById(R.id.button_purchase);
		purchaseButton.setOnClickListener(this);
		purchaseButton.setEnabled(false);
		
		
		/*
		 * Access Shared Pref data
		 * load shared prefs t check if already purchased
		 * Objet pots to pref xml file name stored in data/data/padackagname/sharedpref.xml
		 * Mode_PRIVATE means sharde pref file can be opened by entore application
		 */
				prefs= this.getSharedPreferences(prefFileName, MODE_PRIVATE);
				final SharedPreferences.Editor editor= prefs.edit();
				
				
				purchased = prefs.getBoolean("purchased", false);
				
				if(purchased){
					Log.d(TAG, "ONCTREATE PURCHASED (TRUE)" + purchased);
					purchaseButton.setText("Already Purchased!");
					
					//finish();
				}else if(!purchased){
					Log.d(TAG, "ONCREATE PURCHASED (FALSE)" + purchased);
				}
		
				
				
				
				
				
		//string is from Services 7 API Licence publication key
		
		
		
		String base64EncodedPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvNDDXwOJlMYkamBHRpqBk4ooHu7x1MMPjfHgnGOPWp2MoS/KDjej7gjpiOu+c0ydldFPkvX6fXnADZz6rY/zxlVEHH+rRfd65xlSkzjoIQ1BmD43wykpWzEUAR2Qwe0TyAYJ65bH5wvgvGbi7xa6VmcTDUg7ZzSRCs5s2iwjWGu8oD4Jj6c28bQTNHWf/k1Yx/Pn9o2eZlz1gnPWpSx/dMvWfS7clnEmruANAYl2h0yjQ+tMxlZFHuovCIIoDkwb0k5S+9n8MjWp5VbvblYBiTmpFOynT/zHUJXuQjgSs/HBQt8scr2pQQ6Cduz9rsGcNw8IkSy2DRVxTlwoqVHruQIDAQAB";
		   
		   // compute your public key and store it in base64EncodedPublicKey
		   mHelper = new IabHelper(this, base64EncodedPublicKey);
		   
		// enable debug logging (for a production application, you should set this to false).
	        mHelper.enableDebugLogging(true);
	        
	
		  /*
		   * Asynchorounously perform service binding to Google Bill 
		   * service and return any errors with IabResult object and listener
		   * This must complete before we call luanchPutchace or will get a Illegalstateexcpeion IAB nort set up
		   */
		   
	        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
	            public void onIabSetupFinished(IabResult result) {
	                Log.d(TAG, "Setup finished.");

	                if (!result.isSuccess()) {
	                    // Oh noes, there was a problem.
	                    alert("Problem setting up in-app billing: " + result);
	                    return;
	                }

	                // Have we been disposed of in the meantime? If so, quit.
	                if (mHelper == null) return;

	                // IAB is fully set up. Now, let's get an inventory of stuff we own.
	                Log.d(TAG, "Setup successful. Querying inventory.");
	                //mHelper.queryInventoryAsync(mGotInventoryListener);
	                
	                //enable purcahse button
	                purchaseButton.setEnabled(true);
	            }
	        });
		   
		   
		   /*
		    * Purchase request is made in onClickListener which is enabled
		    * after asynchrounous set up is complete
		    * 
		    */
		   
		   
		   
	
	
	}//onCreate

	
	
	//return boolean prchased to other classes to dislay ads or not
	public  boolean isAppPurchased(){
		
		
		//SharedPreferences prefs= getSharedPreferences(prefFileName, MODE_PRIVATE);
		//final SharedPreferences.Editor editor= prefs.edit();
		//purchased = prefs.getBoolean("purchased", false);
		//purchased = prefs.getBoolean("purchased", false);
		return purchased;
		//return true;
	}

	
	
	
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 
		 
		 /*
		  * Server respnse codes
		  * BILLING_RESPONSE_RESULT_OK	0	Success
          * BILLING_RESPONSE_RESULT_USER_CANCELED	1	User pressed back or canceled a dialog
          *BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE	3	Billing API version is not supported for the type requested
          * BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE	4	Requested product is not available for purchase
          * BILLING_RESPONSE_RESULT_DEVELOPER_ERROR	5	Invalid arguments provided to the API. This error can also indicate that the application was not correctly signed or properly set up for In-app Billing in Google Play, or does not have the necessary permissions in its manifest
           *BILLING_RESPONSE_RESULT_ERROR	6	Fatal error during the API action
*BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED	7	Failure to purchase since item is already owned
*BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED	8	Failure to consume since item is not owned

		  * http://developer.android.com/google/play/billing/billing_reference.html
		  */
	        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
	        if (mHelper == null) return;

	        // Pass on the activity result to the helper for handling
	        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
	            // not handled, so handle it ourselves (here's where you'd
	            // perform any handling of activity results not related to in-app
	            // billing...
	            super.onActivityResult(requestCode, resultCode, data);
	        }
	        else {
	            Log.d(TAG, "onActivityResult handled by IABUtil.");
	        }
	    }
	
	@Override
	protected void onDestroy() {
		// unbind to Google play service to avoid poor perfomrance issues
		
		super.onDestroy();
		
		   if (mHelper != null) mHelper.dispose();
		   mHelper = null;
		   
	}
	
	
	
	void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }



	@Override
	public void onClick(View v) {
		// make purcahse request
		
		/*
		    * make apurcahse request
		    * 1st arg is context, 2nd is product id in googlpe play in app purcahse for app
		    * thirsd is for inActivityResult callback ID
		    * fourth is a listener fro callback
		    * fifith is a a unquie purcahse ID, string, can be mage up
		    * 
		    * For testing purposes withput uploading apk, use Goofgle staic producd ID android.test.purchased
		    * The ID is actually productID
		    * Device must run Google Play >3ver
		    */
		   
		
		// load the shared prefs object
		prefs= this.getSharedPreferences(prefFileName, MODE_PRIVATE);
		final SharedPreferences.Editor editor= prefs.edit();
		
		
		
		   mHelper.launchPurchaseFlow(this, productID, RC_REQUEST,   
				   new IabHelper.OnIabPurchaseFinishedListener() {
					   public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
					   {
					      if (result.isFailure()) {
					         Log.d(TAG, "Error purchasing: " + result);
					         
					        
					         //alert("Error Purchasing: "+ result);
					         Log.d(TAG, "Error purchasing using test SKU: "+  result);
					        	 editor.putBoolean("purchased", false);
								editor.commit();
					         
								
								
								//check if a;ready purcahsed but with a non sogn error ie signature verfication error using static android.test.purchase item
								if(result.toString().toLowerCase().contains("owned")&&result.toString().toLowerCase().contains("already")){
									editor.putBoolean("purchased", true);
									editor.commit();
									purchased = prefs.getBoolean("purchased", false);
									alert("Already Purchased!!! No more adds...:)");
									
									
									
									 purchaseButton.setEnabled(false);
									   purchaseButton.setText("Purchased!");
									Log.d("IN LIANCH PURCHASE ON CLICK", "Already Bought so change purcahse to true: "+ purchased);
									
								}
					         return;
					      } 
					      else if(result.isSuccess()) {
					    	 // if (purchase.getSku().equals(testProduct)) {
					         // give user access to premium content and update the UI
					    	  //set the purchaesd booean to true
					    	  
					    	  
					    	//when purcajsed add this code
								editor.putBoolean("purchased", true);
								editor.commit();
								Toast.makeText(getApplicationContext(), "ADD FREE VERSION PURCAHSED!!!" +
										" Details OrderID: "+purchase.getOrderId() +" Payload ID: "+purchase.mDeveloperPayload, Toast.LENGTH_LONG).show();
								Log.d("CONNECT TO GOOGLE BILL", "ITEM PURCAHSED! : "+purchased);
								String purcahseMeassge="Purchased!! No more adds:)....\n" +
										"Order ID: "+purchase.getOrderId();
								
								 purchaseButton.setEnabled(false);
								   purchaseButton.setText("Purchased!");
					    	 // }//if getSKU=testProduct
					      }//if res=success
					   }
					}, "diveAppPurchase");
		   
		
		  
	}//onClick
	
	
	
	
}//class
