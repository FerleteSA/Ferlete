package com.ferlete.services;

import android.location.Location;

public interface GPSTrackerImplementation {
	public void onLocationChange(GPSTracker tracker, Location location);
}
