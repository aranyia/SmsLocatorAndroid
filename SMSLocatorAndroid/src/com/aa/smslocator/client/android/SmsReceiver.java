package com.aa.smslocator.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

public class SmsReceiver extends BroadcastReceiver {
	public static final String SMS_RECEIVED_INT_EXTRA = "pdus";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;
		
		final Object[] pdus = (Object[]) intent.getExtras().get(SMS_RECEIVED_INT_EXTRA);
		if(pdus == null) return;
		
		final Intent smsProcessIntent = new Intent(SmsApiService.SMS_INTENT_ACTION);
		smsProcessIntent.putExtra(SMS_RECEIVED_INT_EXTRA, pdus);
		context.startService(smsProcessIntent);
	}
}
