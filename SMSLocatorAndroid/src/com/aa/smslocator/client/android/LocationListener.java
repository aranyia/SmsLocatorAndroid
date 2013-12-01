package com.aa.smslocator.client.android;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;

public class LocationListener implements ConnectionCallbacks, OnConnectionFailedListener {
	private Context context;
	private LocationClient locationClient;
	
	protected LocationListener() {
	}
	public LocationListener(Context context) {
		this.context = context;
		this.locationClient = new LocationClient(this.context, this, this);
	}
	
	public Location getLocation() {
		locationClient.connect();
		final Location location = locationClient.getLastLocation();
		locationClient.disconnect();
		
		return location;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(this.getClass().getSimpleName(),
			  "LocationClient connenction failed, error code: "+ result.getErrorCode());
	}
	
	@Override
	public void onConnected(Bundle bundle) {
		
	}
	
	@Override
	public void onDisconnected() {

	}
}
