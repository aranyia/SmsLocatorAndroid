package com.aa.smslocator.client.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends Activity {
	private Preferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		prefs = new Preferences(this);
	
		final String prefPhoneNumber = prefs.getPhoneNumber();
		
		final Switch switchUploadSms = (Switch) findViewById(R.id.switchUploadSms);
		switchUploadSms.setChecked(prefs.isActiveUploadSms());
		switchUploadSms.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
				prefs.setActiveUploadSms(isChecked);
			}			
		});
		
		final EditText editPhoneNum = (EditText) findViewById(R.id.editTextPhoneNumber);
		editPhoneNum.setText(prefPhoneNumber);
		
		final Button btnSetPoneNum = (Button) findViewById(R.id.btnSetPhoneNumber);
		btnSetPoneNum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String editPhoneNumber = editPhoneNum.getEditableText().toString();
				prefs.setPhoneNumber(editPhoneNumber);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
