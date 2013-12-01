package com.aa.smslocator.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
	public static final String SMS_RECEIVED_INT_EXTRA = "pdus";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(this.getClass().getSimpleName(), "Received intent: "+ intent.getAction());
		if(!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;
		
		final Object[] pdus = (Object[]) intent.getExtras().get(SMS_RECEIVED_INT_EXTRA);
		if(pdus == null) return;
		
		final Intent smsServiceIntent = new Intent(context, SmsApiService.class);
		smsServiceIntent.putExtra(SMS_RECEIVED_INT_EXTRA, pdus);
		context.startService(smsServiceIntent);
	}
}
