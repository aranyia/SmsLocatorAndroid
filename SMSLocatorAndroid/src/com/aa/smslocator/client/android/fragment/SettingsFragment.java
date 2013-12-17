package com.aa.smslocator.client.android.fragment;

import com.aa.smslocator.client.android.Preferences;
import com.aa.smslocator.client.android.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsFragment extends Fragment {
	private Preferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
		prefs = new Preferences(getActivity());
		
		final String prefPhoneNumber = prefs.getPhoneNumber();
		
		final Switch switchUploadSms = (Switch) getActivity().findViewById(R.id.switchUploadSms);
		switchUploadSms.setChecked(prefs.isActiveUploadSms());
		switchUploadSms.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
				prefs.setActiveUploadSms(isChecked);
			}			
		});
		
		final EditText editPhoneNum = (EditText) getActivity().findViewById(R.id.editTextPhoneNumber);
		editPhoneNum.setText(prefPhoneNumber);
		
		final Button btnSetPoneNum = (Button) getActivity().findViewById(R.id.btnSetPhoneNumber);
		btnSetPoneNum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final String editPhoneNumber = editPhoneNum.getEditableText().toString();
				prefs.setPhoneNumber(editPhoneNumber);
			}
		});
    }
}
