package com.aa.smslocator.client.android.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.aa.smslocator.client.android.R;
import com.appspot.smslocator.smsLocator.model.Location;
import com.appspot.smslocator.smsLocator.model.SmsMessage;
import com.appspot.smslocator.smsLocator.model.SmsResource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SmsMapFragment extends Fragment implements LoaderCallbacks<List<SmsResource>> {
	private GoogleMap map;
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yy/MM/dd hh:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
	    getLoaderManager().initLoader(0, null, this).forceLoad();
	    
		final Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map);
		if(mapFragment != null) map = ((MapFragment) mapFragment).getMap();
		
		map.setMyLocationEnabled(true);
    }

    protected void populateSmsMap(final GoogleMap map, final Collection<MarkerOptions> markers) {
    	if(map == null || markers == null) return;
    	
    	final LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
    	for(final MarkerOptions marker : markers) {
    		map.addMarker(marker);
    		boundsBuilder.include(marker.getPosition());
    	}

    	if(!markers.isEmpty()) {
    		map.animateCamera(CameraUpdateFactory.newLatLngZoom(boundsBuilder.build().getCenter(), 12.5f));
    	}
    }

    protected Collection<MarkerOptions> getSmsAsMapMarkers(final Collection<SmsResource> smsCollection) {
    	if(smsCollection == null) return Collections.emptyList();
    	final List<MarkerOptions> smsMarkers = new ArrayList<MarkerOptions>(smsCollection.size());
    	
    	for(final SmsResource smsRes : smsCollection) {
    		final Location smsLoc = smsRes.getLocation();
    		if(smsLoc != null) {
    		    MarkerOptions smsMarker = new MarkerOptions()
    		    								.position(new LatLng(smsLoc.getLatitude(), smsLoc.getLongitude()));
    		    final SmsMessage smsMsg = smsRes.getMessage();
    		    if(smsMsg != null) {
    		    	smsMarker = smsMarker.title(DATEFORMAT.format(new Date(smsMsg.getTime().getValue())))
    		    						 .snippet(smsMsg.getMessage());
    		    }
    		    smsMarkers.add(smsMarker);
    		}
    	}
		return smsMarkers;    	
    }
    
    
	@Override
	public Loader<List<SmsResource>> onCreateLoader(int id, Bundle args) {
		return new SmsListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<SmsResource>> loader, List<SmsResource> result) {
		populateSmsMap(map, getSmsAsMapMarkers(result));
	}

	@Override
	public void onLoaderReset(Loader<List<SmsResource>> loader) {
	}
}
