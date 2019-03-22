package com.mooney.diveapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

//about email intent

public class About extends Activity implements OnClickListener{

	private EditText aboutText;
	private Button email;
	private Button sendEmail;
	private String emailAdd;//holds email as string to convert to array
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		
		//intilise and add text to textview
		emailAdd = "dancingbush@gmail.com";
		aboutText = (EditText) this.findViewById(R.id.tv_aboutMe);
		email = (Button) this.findViewById(R.id.ib_emailMe);
		email.setOnClickListener(this);
		sendEmail = (Button) this.findViewById(R.id.ib_sendEmail);//set to invisible until user clicks email button
		
		String aboutMessage = "Welcome!!\n\n" +
				" Feel free to mail me " +
				"if something is not working or you have an idea that may improve the app. " +
				"I am a DM and I deveolped this app as the more " +
				"dives I do the less I am inclined to record them  in a log.\n" +
				" This is usually because " +
				"I don't have my log handy or its so cold you just wanna go home and curl into the foetal position.\n\n" +
				"So with this app you can record your dive, rate it, save a picture of the dive site" +
				",  get your dive stats over time, and share your dives on FB.\n\n" +
				"\nAnyways hope u enjoy and find it useful.\n\nPS I still keep a written log and recommend u do so also\n\nHappy Dives:)  ";
		aboutText.setText(aboutMessage);
		aboutText.setGravity(Gravity.TOP| Gravity.LEFT); 
		
		
	}//end oncreate
	
	
	
	@Override
	public void onClick(View v) {
		
		
		switch (v.getId()){
		
		
		case R.id.ib_emailMe:
			// crate email intent, when button clicked remove it and replave with 'Send' button by settng visibilty
			//then set edittext to "" and add a hint to enter data
			email.setVisibility(ImageButton.GONE);
	        sendEmail.setVisibility(ImageButton.VISIBLE);
	        sendEmail.setOnClickListener(About.this);
	        
		aboutText.setText("");
		aboutText.setHint("Enter message here...");
			break;
			
			
			
		case R.id.ib_sendEmail:
			//get text from edit text and send email intent
			
			String emailAddress[] = {emailAdd};//get aeray form a string for email intent
			
			String message = aboutText.getText().toString();
			if (message !=null || !message.contains("")){
				//send email
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setData(Uri.parse("mailto:"));
				emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email from Dive App User");
				emailIntent.setType("plain/text");
				emailIntent.putExtra(Intent.EXTRA_TEXT, message);
				
				try{
				startActivity(emailIntent);
				
				}catch(android.content.ActivityNotFoundException ex){
					ex.printStackTrace();
					String error = ex.getMessage();
					Dialog d = new Dialog(About.this);
					d.setTitle("Email Not Sent!");
					TextView tv = new TextView(About.this);
					tv.setText("Hmm you have no email set up on you phone.....Try agan once set up:)");
					d.setContentView(tv);
					d.show();
					
				}//end try catch emal activity
				
				
				
				
			}else{
				//display dialog
				displayDialog();
			
			}
			break;
		
		}//end switch
		
	        
		
	}//end on click



	private void displayDialog() {
		// user has not entered any text
		AlertDialog.Builder build = null;
		AlertDialog dialog = null;
		build = new AlertDialog.Builder(this);
		build.setCancelable(true);
		build.setMessage("Sending blank email..");
		build.setPositiveButton("Try Again",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// retrun to activity
						return;
						

					}// end on click
				});// end pos button take photo

		// get photo from SD card option
		build.setNegativeButton("Main Menu",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();

					}// end onclick
				});// end pos button take photo
		dialog = build.create();
		dialog.setTitle("Email Probs");
		dialog.show();

		
		
		
	}//end displayDialog



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	
	

}//end class cabout
