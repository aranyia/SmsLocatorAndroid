package com.aa.smslocator.client.android.fragment;

import java.io.IOException;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.aa.smslocator.client.android.Preferences;
import com.appspot.smslocator.smsLocator.SmsLocator;
import com.appspot.smslocator.smsLocator.model.SmsResource;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class SmsListLoader extends AsyncTaskLoader<List<SmsResource>> {
	private final Preferences prefs;
	private final SmsLocator smsApi;
	
	public SmsListLoader(Context context) {
		this(context, null);
	}
	public SmsListLoader(Context context, Preferences prefs) {
		super(context);
		this.smsApi = new SmsLocator.Builder(AndroidHttp.newCompatibleTransport(),
											 new GsonFactory(), null).build();
		this.prefs = (prefs != null) ? prefs : new Preferences(context);
	}

	@Override
	public List<SmsResource> loadInBackground() {
		try {
			return smsApi.sms().listForReceiver(prefs.getPhoneNumber()).execute().getItems();
		} catch (IOException e) { return null; }
	}
}
