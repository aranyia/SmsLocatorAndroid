package com.aa.smslocator.client.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class Preferences {
	private final SharedPreferences prefs;
	private final Context context;
	protected static final String PREF_FILE = "appSettings",
								  PREF_NUMBER = "phoneLineNumber",
								  PREF_SMS_UPLOAD_ACTIVE = "funcUploadSms";
	
	public Preferences(Context context) {
		this.prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		this.context = context;
	}
	
	public String getPhoneNumber() {
		String phoneNumber = prefs.getString(PREF_NUMBER, null);
		
		if(phoneNumber == null) {
			final TelephonyManager telManager = 
					(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(telManager == null) return null;
			else phoneNumber = telManager.getLine1Number();
			
			if(phoneNumber != null) setPhoneNumber(phoneNumber);
		}
		return phoneNumber;
	}
	
	public boolean setPhoneNumber(String phoneNumber) {
		if(phoneNumber == null || "".equals(phoneNumber)) return false;
		return prefs.edit().putString(PREF_NUMBER, phoneNumber).commit();
	}
	
	public boolean isActiveUploadSms() {
		return prefs.getBoolean(PREF_SMS_UPLOAD_ACTIVE, true);
	}
	
	public boolean setActiveUploadSms(boolean active) {
		return prefs.edit().putBoolean(PREF_SMS_UPLOAD_ACTIVE, active).commit();
	}
}
