package com.ferlete.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.ferlete.R;
import com.ferlete.services.ConnectionDetector;
import com.ferlete.services.GPSTracker;
import com.ferlete.services.GPSTrackerImplementation;


public class AboutActivity extends Activity {

	// GPSTracker class
	GPSTracker gps;

	// flag for Internet connection status
	Boolean isInternetPresent = false;

	// Connection detector class
	ConnectionDetector cd;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// creating connection detector class instance
		cd = new ConnectionDetector(getApplicationContext());


		String statusGPS = null, statusInternet = null, statusWS = null;

		// get state GPS
		gps = new GPSTracker(getBaseContext(), new GPSTrackerImplementation() {

			@Override
			public void onLocationChange(GPSTracker tracker, Location location) {
				// TODO Auto-generated method stub

			}
		}) {
		};

		if (gps.canGetLocation()) {
			statusGPS = "OFF";
		} else {
			statusGPS = "ON";
		}

		// get Internet status
		isInternetPresent = cd.isConnectingToInternet();

		// check for Internet status
		if (isInternetPresent) {
			statusInternet = "ON";
		} else {
			statusInternet = "OFF";
		}


		TextView txt_allCellInfo = (TextView) findViewById(R.id.textView2);
		txt_allCellInfo.setText("Device Id:" + telephonyManager.getDeviceId()
				+ "\n" + "Operadora Rede Móvel: "
				+ telephonyManager.getNetworkOperatorName() + "\n"
				+ "Status Comunicação GPS: " + statusGPS + "\n"
				+ "Status Rede GPRS: " + statusInternet + "\n"
				+ "Status Comunicação Web-Service: " + statusWS);

		

	}
}
