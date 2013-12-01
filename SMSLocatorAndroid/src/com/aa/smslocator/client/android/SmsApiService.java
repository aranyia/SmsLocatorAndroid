package com.aa.smslocator.client.android;

import java.io.IOException;

import com.appspot.smslocator.smsLocator.SmsLocator;
import com.appspot.smslocator.smsLocator.model.ApiResponse;
import com.appspot.smslocator.smsLocator.model.Location;
import com.appspot.smslocator.smsLocator.model.SmsMessage;
import com.appspot.smslocator.smsLocator.model.SmsResource;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SmsApiService extends IntentService {
	public static final String SERVICE_NAME = "SmsApiConnectorService";
	private final SmsLocator smsApi;
	private Preferences prefs;
	
	public SmsApiService() {
		super(SERVICE_NAME);
		smsApi = new SmsLocator.Builder(AndroidHttp.newCompatibleTransport(),
										new GsonFactory(), null).build();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(SERVICE_NAME, "Received intent.");
		prefs = new Preferences(this);
		if(!prefs.isActiveUploadSms()) return;
				
		final Bundle intentExtras = intent.getExtras();
		if(intentExtras == null) return;
		else if(!intentExtras.containsKey(SmsReceiver.SMS_RECEIVED_INT_EXTRA)) return;
		
		final Object[] pdus = (Object[]) intentExtras.get(SmsReceiver.SMS_RECEIVED_INT_EXTRA);
		SmsMessage[] smsMessages = extractSmsMessages(pdus);
		
		final Location location = determineLocation();
		
		SmsResource[] smsResources = new SmsResource[smsMessages.length];
		for(int i = 0; i < smsMessages.length; i++) {
			final SmsResource smsRes = new SmsResource()
											.setMessage(smsMessages[i])
											.setLocation(location);
			smsResources[i] = smsRes;
		}
		saveSms(smsResources);
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
											 .setReceiver(obtainSmsReceiver())
											 .setTime(time);
		}
		return smsMessages;
	}

	private Location determineLocation() {
		final LocationListener locationListener = new LocationListener(this);
		final android.location.Location rawLocation = locationListener.getLocation();
		if(rawLocation == null) return null;
		
		final Location location = new Location()
											.setAccuracy(rawLocation.getAccuracy())
											.setLatitude(rawLocation.getLatitude())
											.setLongitude(rawLocation.getLongitude())
											.setTime(new DateTime(rawLocation.getTime()));
		return location;
	}

	private String obtainSmsReceiver() {
		return prefs.getPhoneNumber();
	}
}
