package com.mooney.diveapp;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DiveHistory extends Activity implements OnClickListener {

	private TextView deepestDiveTV, warmistDiveTV, totalDivesTV,
			coledestDiveTV, totalBT_TV, longestDiveTV, shallowestDiveTV,
			averageDiveTimeTV, numberOfWarmDivesTV, noOfColdDivesTV,
			diveNumberMaxDepthTV, diveNumberMinDepthTV,
			diveNumberWarmestDiveTV, diveNumberColdestDiveTV,
			diveNumberLongestDiveTV;

	private int deppestDive = 0, shallowestDive = 1000, warmistDive = 0,
			coldestDive = 10000, totalBottomTime = 0, longestDive = 0,
			totalDives = 0, totalColdDives = 0, totalWarmDives = 0,
			diveNumberColdestDive = 0, diveNumberWarmestDive = 0,
			diveNumberLongestDive = 0, diveNumberMaxDepth = 0,
			diveNumberMinDepth = 0;
	private float averageDiveTime = 0;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dive_history);
		intilizeWidgets();

		// get the background for this activity form random choice of two
		// drawables
		int[] imageSelection = { R.drawable.divehistory1,
				R.drawable.divehistory2, R.drawable.splash2 };
		Random whichImage = new Random();
		int theImage = whichImage.nextInt(imageSelection.length);
		// this.getWindow().setBackgroundDrawableResource(imageSelection[theImage]);
		this.getWindow().setBackgroundDrawableResource(R.drawable.plane3);

		pd = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("Loading your dives....");
		// get cursor and iterate result set in a asynch inner class
		new processHistory().execute();

	}// end on create

	private void intilizeWidgets() {

		// TODO Auto-generated method stub
		deepestDiveTV = (TextView) this.findViewById(R.id.tv_deepestDive);
		warmistDiveTV = (TextView) this.findViewById(R.id.tv_warmistDive);
		totalDivesTV = (TextView) this.findViewById(R.id.tv_totalDives);
		coledestDiveTV = (TextView) this.findViewById(R.id.tv_coledestDive);
		totalBT_TV = (TextView) this.findViewById(R.id.tv_totalDiveTime);
		longestDiveTV = (TextView) this.findViewById(R.id.tv_longestDive);
		shallowestDiveTV = (TextView) this.findViewById(R.id.tv_shallowestDive);
		averageDiveTimeTV = (TextView) this
				.findViewById(R.id.tv_averageDiveTime);
		numberOfWarmDivesTV = (TextView) this
				.findViewById(R.id.tv_noOfWarmDives);
		noOfColdDivesTV = (TextView) this.findViewById(R.id.tv_noOfColdDives);
		diveNumberMaxDepthTV = (TextView) this
				.findViewById(R.id.tv_deepestDiveNumber);
		diveNumberMinDepthTV = (TextView) this
				.findViewById(R.id.tv_shallowestDiveNumber);
		diveNumberWarmestDiveTV = (TextView) this
				.findViewById(R.id.tv_warmestDiveNumber);
		diveNumberColdestDiveTV = (TextView) this
				.findViewById(R.id.tv_coldestDiveNumber);
		diveNumberLongestDiveTV = (TextView) this
				.findViewById(R.id.tv_longestDiveNumber);

	}// end intilize widgets

	private class processHistory extends AsyncTask<Void, Void, String> {

		private diveDataBase db;
		private Cursor cursor;
		private int theDepth, theBottomTime, theTemp;// assign daat from each
														// row

		protected void onPreExecute() {
			// show dialog while loading data
			// display dialog while page loading and close when
			// finihsed(override onStart())

			pd.show();

		}

		@Override
		protected String doInBackground(Void... params) {

			// get cursor object and assign values for all history int varibles
			db = new diveDataBase(DiveHistory.this);
			db.open();
			cursor = db.getCursorData();
			int iBottomTime = cursor.getColumnIndex(db.KEY_BOTTOMTIME);
			int iWaterTemp = cursor.getColumnIndex(db.KEY_WATERTEMP);
			int iDepth = cursor.getColumnIndex(db.KEY_DEPTH);
			int diveNum1 = 0;
			int firstBottomTimeEntry = 0;
			// must account for user entering their current cummulative bottom
			// time if >o on first dive

			// always chack the cursor has data before trying to retrive data
			// from DB, otherwise exception thrown
			if (cursor != null && cursor.getCount() > 0) {

				try {

					int rowNumber = db.getNumberOfRows();
					diveNum1 = Integer.parseInt(db.getDiveNumber(1));

					// assign global diveNumbers for ,aximimum/lowest/ etc
					diveNumberColdestDive = diveNum1;
					diveNumberWarmestDive = diveNum1;
					diveNumberLongestDive = diveNum1;
					diveNumberMaxDepth = diveNum1;
					diveNumberMinDepth = diveNum1;
					// get
					// first
					// dive
					// entry
				} catch (Exception e) {
					e.printStackTrace();
				}

				// if(diveNum1==1){
				try {
					firstBottomTimeEntry = Integer.parseInt(db
							.getDiveBottomTime(1));
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				// }
				Log.d("DIVE HISTORY", "For Dive Number : " + diveNum1
						+ " the bttom time is : " + firstBottomTimeEntry);
			}
			int rows = 0;
			int diveNumber = 1; // =+1 each row iteration to get diveNumber

			// iterate though the cursor object

			if (cursor != null && cursor.getCount() > 0) {

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {

					// for every row get string data from the respective column
					// index column

					if (cursor.getString(iDepth) != null
							|| cursor.getString(iWaterTemp) != null
							|| cursor.getString(iBottomTime) != null) {

						theBottomTime = Integer.parseInt(cursor
								.getString(iBottomTime));
						theDepth = Integer.parseInt(cursor.getString(iDepth));
						theTemp = Integer
								.parseInt(cursor.getString(iWaterTemp));

						// total cold vs warm temps dives
						if (theTemp < 15) {

							totalColdDives += 1;

						} else if (theTemp > 15) {

							totalWarmDives += 1;
						}

						// comapre to get maximumu value
						if (deppestDive <= theDepth) {
							deppestDive = theDepth;

							// assign the dive number
							diveNumberMaxDepth = Integer.parseInt(db
									.getDiveNumber(diveNumber));

						}
						if (shallowestDive >= theDepth) {
							shallowestDive = theDepth;

							// assign the dive number
							diveNumberMinDepth = Integer.parseInt(db
									.getDiveNumber(diveNumber));
						}

						if (warmistDive <= theTemp) {
							warmistDive = theTemp;

							// assign the dive number
							diveNumberWarmestDive = Integer.parseInt(db
									.getDiveNumber(diveNumber));

						}
						if (coldestDive >= theTemp) {
							coldestDive = theTemp;

							// assign the dive number
							diveNumberColdestDive = Integer.parseInt(db
									.getDiveNumber(diveNumber));
						}

						// if user enters first dove as not their first ie 235,
						// with bottom time >200 minutes
						// we must check this and make sure this first bottom
						// time is not given to longestDive
						// int diveno = db.getLastDiveNumber();
						if (rows == 0) {
							if (firstBottomTimeEntry > 200) {
								theBottomTime = theBottomTime
										- firstBottomTimeEntry;
							}
						}

						if (longestDive <= theBottomTime) {
							longestDive = theBottomTime;
							diveNumberLongestDive = Integer.parseInt(db
									.getDiveNumber(diveNumber));
						}

						if (rows == 0) {
							theBottomTime = Integer.parseInt(cursor
									.getString(iBottomTime));
						}

					}// end 2nd if
					else {
						// log
						Log.d("Dive Hstory", "One of the values is null!");
					}

					totalBottomTime += theBottomTime;
					totalDives = db.getLastDiveNumber();
					
					

					rows++;
					diveNumber++;
				}// end for loop thopugh cursor object
			} else {
				// first if stament,cursor is null or DB is empty
				return "error";
				// displayDialog();
			}
			// get total dives

			// finished iterating cursor object , get average dive time

			

			return "";
		}// end do in background

		@Override
		protected void onPostExecute(String result) {
			// updtae the textviews with varibles assigned in do in background

			if (pd.isShowing()) {
				pd.dismiss();
			}

			// check if cursor was null and display error dialog if is
			if (result.contains("error")) {
				displayDialog();
			} else {

				// diveNumberMaxDepthTV, diveNumberMinDepthTV,
				// diveNumberWarmestDiveTV, diveNumberColdestDiveTV,
				// diveNumberLongestDiveTV;

				// assign varibles to fields, and assign onClickListeners so
				// navigate to dive in DB ViewSeletedDive.java

				numberOfWarmDivesTV.setText(String.valueOf(totalWarmDives));
				noOfColdDivesTV.setText(String.valueOf(totalColdDives));
				diveNumberMaxDepthTV
						.setText(String.valueOf(diveNumberMaxDepth));
				diveNumberMaxDepthTV.setOnClickListener(DiveHistory.this);
				diveNumberMinDepthTV
						.setText(String.valueOf(diveNumberMinDepth));
				diveNumberMinDepthTV.setOnClickListener(DiveHistory.this);
				diveNumberWarmestDiveTV.setText(String
						.valueOf(diveNumberWarmestDive));
				diveNumberWarmestDiveTV.setOnClickListener(DiveHistory.this);
				diveNumberColdestDiveTV.setText(String
						.valueOf(diveNumberColdestDive));
				diveNumberColdestDiveTV.setOnClickListener(DiveHistory.this);
				diveNumberLongestDiveTV.setText(String
						.valueOf(diveNumberLongestDive));
				diveNumberLongestDiveTV.setOnClickListener(DiveHistory.this);

				deepestDiveTV.setText(String.valueOf(deppestDive) + " meters");
				warmistDiveTV.setText(String.valueOf(warmistDive) + " Celsius");
				totalDivesTV.setText(String.valueOf(totalDives));
				coledestDiveTV
						.setText(String.valueOf(coldestDive) + " Celsius");
				// totalBT_TV.setText(String.valueOf(totalBottomTime)+" mins");
				// longestDiveTV.setText(String.valueOf(longestDive)+ "mins");
				shallowestDiveTV.setText(String.valueOf(shallowestDive)
						+ " meters");

				if (totalBottomTime < 60) {
					// display in mins
					totalBT_TV.setText(String.valueOf(totalBottomTime)
							+ " mins");
				} else if (totalBottomTime >= 60) {
					// report in hours and mins
					int hours = Math.round(totalBottomTime / 60);
					int minutes = Math.round(totalBottomTime % 60);
					// String bottomTimeHours =
					// String.valueOf(totalBottomTime/60)+"hours " +
					// totalBottomTime
					totalBT_TV.setText(hours + " hours " + minutes + " mins");

				}// end else

				int totdives = (db.getNumberOfRows()-1);
				int lastDiveNumber = db.getLastDiveNumber();
				Log.d("STATS: 333", "AV. DIVE TIME = TOTAL DIVE TIME / NO OF DIVES: "+ totalBottomTime + "/" + lastDiveNumber );
				
				averageDiveTime = totalBottomTime / lastDiveNumber;
				Log.d("STATS 337: ", "AVERAGE DIVE TIME= "+ averageDiveTime );
				
				
				
				
				
				
				// average dive time
				
				// if user enters first dove as not their first ie 235,
				// with bottom time >200 minutes
				// we must check this and make sure this first bottom
				// time is not given to longestDive
				// int diveno = db.getLastDiveNumber();
//				int noOfDives = db.getNumberOfRows();
//				if (noOfDives <= 1) {
//					averageDiveTime=0;
//					}
				
				if (averageDiveTime < 60) {
					// display in mins
					averageDiveTimeTV.setText(String.valueOf(averageDiveTime)
							+ " mins");
				} else if (averageDiveTime >= 60) {
					// report in hours and mins
					int hours = Math.round(averageDiveTime / 60);
					int minutes = Math.round(averageDiveTime % 60);

					averageDiveTimeTV.setText(hours + " hours " + minutes
							+ " mins");

				}// end else

				if (longestDive < 60) {
					// display in mins
					longestDiveTV
							.setText(String.valueOf(longestDive) + " mins");
				} else if (longestDive >= 60) {
					// report in hours and mins
					int hours = Math.round(longestDive / 60);
					int minutes = Math.round(longestDive % 60);
					// String bottomTimeHours =
					// String.valueOf(totalBottomTime/60)+"hours " +
					// totalBottomTime
					longestDiveTV
							.setText(hours + " hours " + minutes + " mins");

				}// end else

			}// end if error

			// close db here or gte errors
			cursor.close();
			db.close();
			super.onPostExecute(result);
		}// end onPostExecute

	}// end inner asynch class

	private void displayDialog() {
		// display dialog of there are no entries in the database and finish the
		// activity

		AlertDialog.Builder build = null;
		AlertDialog dialog = null;
		build = new AlertDialog.Builder(this);
		build.setCancelable(true);
		build.setMessage("You have no dives saved in DB!!");
		build.setPositiveButton("Log A Dive",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// go to log a dive
						Intent i = new Intent("android.intent.action.LOGDIVE");
						startActivity(i);
						finish();

					}// end on click
				});// end pos button take photo

		// get photo from SD card option
		build.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();

					}// end onclick
				});// end pos button take photo
		dialog = build.create();
		dialog.setTitle("No Logged Dives");
		dialog.show();

	}// end display dalog

	@Override
	public void onClick(View textViewSelected) {
		/*
		 * When a TV conating teh dive number of specified filed is clicked we
		 * start an intent tp call ViewSelectedDive.class
		 * Must convert the dive number to a row Id from DB as
		 * dive no 345 maybe ony 3 enrty
		 */

		diveDataBase db = new diveDataBase(this);
		db.open();
		
		
		switch (textViewSelected.getId()) {

		case R.id.tv_deepestDiveNumber:

			String diveNumber = diveNumberMaxDepthTV.getText().toString();
			
			// next dive should be the corrsepomdng row of dive number, or get errors if entry != number
						int nextDiveRow =db.getRowNumber(Integer.parseInt(diveNumber));
						
						diveNumber= String.valueOf(nextDiveRow);
			Intent i = new Intent("android.intent.action.VIEWSELECTEDDIVE");
			Bundle extras = new Bundle();
			extras.putString("row", diveNumber);
			i.putExtras(extras);
			startActivity(i);
			break;

		case R.id.tv_shallowestDiveNumber:

			String diveNumber2 = diveNumberMinDepthTV.getText().toString();
			
			// next dive should be the corrsepomdng row of dive number, or get errors if entry != number
						int nextDiveRow2 =db.getRowNumber(Integer.parseInt(diveNumber2));
						
						diveNumber2= String.valueOf(nextDiveRow2);
			Intent i2 = new Intent("android.intent.action.VIEWSELECTEDDIVE");
			Bundle extras2 = new Bundle();
			extras2.putString("row", diveNumber2);
			i2.putExtras(extras2);
			startActivity(i2);

			break;

		case R.id.tv_coldestDiveNumber:

			String diveNumber3 = diveNumberColdestDiveTV.getText().toString();
			// next dive should be the corrsepomdng row of dive number, or get errors if entry != number
						int nextDiveRow3 =db.getRowNumber(Integer.parseInt(diveNumber3));
						
						diveNumber3= String.valueOf(nextDiveRow3);
						
			Intent i3 = new Intent("android.intent.action.VIEWSELECTEDDIVE");
			Bundle extras3 = new Bundle();
			extras3.putString("row", diveNumber3);
			i3.putExtras(extras3);
			startActivity(i3);

			break;

		case R.id.tv_warmestDiveNumber:

			String diveNumber4 = diveNumberWarmestDiveTV.getText().toString();
			
			// next dive should be the corrsepomdng row of dive number, or get errors if entry != number
						int nextDiveRow4 =db.getRowNumber(Integer.parseInt(diveNumber4));
						
						diveNumber4= String.valueOf(nextDiveRow4);
						
			Intent i4 = new Intent("android.intent.action.VIEWSELECTEDDIVE");
			Bundle extras4 = new Bundle();
			extras4.putString("row", diveNumber4);
			i4.putExtras(extras4);
			startActivity(i4);

			break;
			
			
		case R.id.tv_longestDiveNumber:

			String diveNumber5 = diveNumberLongestDiveTV.getText().toString();
			
			// next dive should be the corrsepomdng row of dive number, or get errors if entry != number
			int nextDiveRow5 =db.getRowNumber(Integer.parseInt(diveNumber5));
			
			diveNumber5= String.valueOf(nextDiveRow5);
			
			Intent i5 = new Intent("android.intent.action.VIEWSELECTEDDIVE");
			Bundle extras5 = new Bundle();
			extras5.putString("row", diveNumber5);
			i5.putExtras(extras5);
			startActivity(i5);

			break;
		
		
		} // swicth

		
	
	} // onClick TV

}// end class Dive History
