package com.aa.smslocator.client.android.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.appspot.smslocator.smsLocator.model.SmsMessage;
import com.appspot.smslocator.smsLocator.model.SmsResource;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class SmsListFragment extends ListFragment implements LoaderCallbacks<List<SmsResource>> {
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy.MM.dd. hh:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
	    getLoaderManager().initLoader(0, null, this).forceLoad();
    }

	@Override
	public Loader<List<SmsResource>> onCreateLoader(int id, Bundle args) {
		return new SmsListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<SmsResource>> loader, List<SmsResource> result) {
		if(result == null) return;
		final String[] contents = new String[result.size()];
		
		int i = 0;
		for(final SmsResource sms : result) {
			final SmsMessage smsMsg = sms.getMessage();
			contents[i] = DATEFORMAT.format(new Date(smsMsg.getTime().getValue())) + "  "
						  +smsMsg.getSource() + ":\n\n" + smsMsg.getMessage() + "\n";
			i += 1;
		}
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
													android.R.layout.simple_list_item_1, contents);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<List<SmsResource>> loader) {
	}
}
