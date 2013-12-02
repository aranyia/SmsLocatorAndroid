package com.aa.smslocator.client.android;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import com.appspot.smslocator.smsLocator.SmsLocator;
import com.appspot.smslocator.smsLocator.model.ApiResponse;
import com.appspot.smslocator.smsLocator.model.Location;
import com.appspot.smslocator.smsLocator.model.SmsMessage;
import com.appspot.smslocator.smsLocator.model.SmsResource;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SmsApiService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
													  GooglePlayServicesClient.OnConnectionFailedListener {
	public static final String SERVICE_NAME = "SmsApiConnectorService";
	private final SmsLocator smsApi;
	private final Queue<SmsResource> smsQueue = new ArrayDeque<SmsResource>();
	private Preferences prefs;
	private LocationClient locationClient;
	
	public SmsApiService() {
		super();
		smsApi = new SmsLocator.Builder(AndroidHttp.newCompatibleTransport(),
										new GsonFactory(), null).build();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = new Preferences(this);
		
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationClient.disconnect();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(SERVICE_NAME, "Received start command intent.");
		if(!prefs.isActiveUploadSms()) stopSelf();
		
		addSmsToProcessQueue(intent.getExtras());
		
		return START_STICKY;
	}

	private void addSmsToProcessQueue(Bundle intentExtras) {
		if(intentExtras == null) return;
		else if(!intentExtras.containsKey(SmsReceiver.SMS_RECEIVED_INT_EXTRA)) return;
		
		final Object[] pdus = (Object[]) intentExtras.get(SmsReceiver.SMS_RECEIVED_INT_EXTRA);
		SmsMessage[] smsMessages = extractSmsMessages(pdus);
		
		final Location location = determineLocation();
		
		for(int i = 0; i < smsMessages.length; i++) {
			final SmsResource smsRes = new SmsResource()
											.setMessage(smsMessages[i])
											.setLocation(location);
			smsQueue.add(smsRes);
		}			
	}

	private void processSmsQueueWithLocation(Location location) {
		while(!smsQueue.isEmpty()) {
			final SmsResource smsRes = smsQueue.poll();
			smsRes.setLocation(location);
			
			saveSms(smsRes);
		}
		stopSelf();
	}

	private void saveSms(SmsResource... smsRes) {
		AsyncTask<SmsResource, Void, ApiResponse> asyncSave = 
			new AsyncTask<SmsResource, Void, ApiResponse>() {

			@Override
			protected ApiResponse doInBackground(SmsResource... smsRes) {
				try {
					return smsApi.sms().save(smsRes[0]).execute();
				} catch (IOException e) { Log.d(SERVICE_NAME, e.getMessage(), e);
										  return new ApiResponse().setStatus("FAILED"); }
			}
			
			protected void onPostExecute(ApiResponse response) {
				Log.d(SERVICE_NAME, "Save task finished with status: " + response.getStatus());
			}
		};

		for(final SmsResource sms : smsRes) {
			asyncSave.execute(sms);
		}
	}

	private SmsMessage[] extractSmsMessages(Object[] pdus) {
		final android.telephony.SmsMessage[] rawMessages = new android.telephony.SmsMessage[pdus.length];
		final SmsMessage[] smsMessages = new SmsMessage[pdus.length];
		
		for (int i = 0; i < pdus.length; i++) {
			rawMessages[i] = android.telephony.SmsMessage.createFromPdu((byte[]) pdus[i]);
			
			final String messageContent = rawMessages[i].getMessageBody(),
						 source = rawMessages[i].getDisplayOriginatingAddress();
			final DateTime time = new DateTime(rawMessages[i].getTimestampMillis());
						 
			smsMessages[i] = new SmsMessage().setMessage(messageContent)
											 .setSource(source)
											 .setReceiver(prefs.getPhoneNumber())
											 .setTime(time);
		}
		return smsMessages;
	}

	private Location determineLocation() {
		if(!locationClient.isConnected()) return null;
		
		final android.location.Location rawLocation = locationClient.getLastLocation();
		if(rawLocation == null) return null;
		
		final Location location = new Location()
											.setAccuracy(rawLocation.getAccuracy())
											.setLatitude(rawLocation.getLatitude())
											.setLongitude(rawLocation.getLongitude())
											.setTime(new DateTime(rawLocation.getTime()));
		return location;
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(SERVICE_NAME, "Connected to GooglePlayServices.");
		processSmsQueueWithLocation(determineLocation());
	}

	@Override
	public void onDisconnected() {
		Log.d(SERVICE_NAME, "Disconnected from GooglePlayServices.");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(SERVICE_NAME, "Failed to connected to GooglePlayServices.");
		processSmsQueueWithLocation(null);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
