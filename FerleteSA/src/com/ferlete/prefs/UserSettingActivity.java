package com.ferlete.prefs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ferlete.R;
import com.ferlete.activity.NotificationView;
import com.ferlete.services.GetGPSSendWS;

public class UserSettingActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	String TAG_LOGCAT = "FerleteSA";

	String notificationTitle;
	String notificationContent;
	String tickerMessage;

	Boolean active;
	String timeSync;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.preferences);

		/*
		 * final ActionBar actionBar = getSupportActionBar();
		 * actionBar.setDisplayShowTitleEnabled(true);
		 * actionBar.setDisplayShowCustomEnabled(true);
		 * actionBar.setDisplayUseLogoEnabled(false);
		 * actionBar.setDisplayShowHomeEnabled(true);
		 */

		addPreferencesFromResource(R.xml.preferences);

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);

		CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager()
				.findPreference("pref_visibilidade");

		checkboxPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						//Log.i("FerleteSA", "Pref:" + preference.getKey()
						//		+ " new Value:" + newValue.toString());

						boolean checked = Boolean.valueOf(newValue.toString());
						if (checked) {
							startServiceComWS();
						} else {
							stopServiceComWS();
						}

						return true;
					}

				});

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preference,
			String key) {

		//Log.i("FerleteSA", "onSharedPreferenceChanged -> key:" + key);

		
	}

	public void startServiceComWS() {
		startService(new Intent(this, GetGPSSendWS.class));
		notificationTitle = "Ferlete";
		notificationContent = "Visibilidade ON";
		tickerMessage = "Agora você esta visível";
		showNotify();

	}

	public void stopServiceComWS() {
		stopService(new Intent(this, GetGPSSendWS.class));
		notificationTitle = "Ferlete";
		notificationContent = "Visibilidade OFF";
		tickerMessage = "Agora você esta invisível";
		showNotify();

	}

	public void showNotify() {

		Intent notificationIntent = new Intent(getApplicationContext(),
				NotificationView.class);
		notificationIntent.putExtra("content", notificationContent);

		/**
		 * This is needed to make this intent different from its previous
		 * intents
		 */
		notificationIntent.setData(Uri.parse("tel:/"
				+ (int) System.currentTimeMillis()));

		/**
		 * Creating different tasks for each notification. See the flag
		 * Intent.FLAG_ACTIVITY_NEW_TASK
		 */
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		/** Getting the System service NotificationManager */
		NotificationManager nManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		/** Configuring notification builder to create a notification */
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				getApplicationContext()).setWhen(System.currentTimeMillis())
				.setContentText(notificationContent)
				.setContentTitle(notificationTitle)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true)
				.setTicker(tickerMessage).setContentIntent(pendingIntent);

		/** Creating a notification from the notification builder */
		//Notification notification = notificationBuilder.build();

		/**
		 * Sending the notification to system. The first argument ensures that
		 * each notification is having a unique id If two notifications share
		 * same notification id, then the last notification replaces the first
		 * notification
		 * */
		//nManager.notify((int) System.currentTimeMillis(), notification);

	}

}
