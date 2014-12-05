package com.example.distancetravelled;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class DistanceService extends Service implements
		GooglePlayServicesClient.OnConnectionFailedListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		com.google.android.gms.location.LocationListener {
	public static final String TAG = "DistaceService";
	LocationClient mClient;
	LocationRequest mLocationRequest;
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	private SharedPreferences distancePreferences;
	private SharedPreferences.Editor edit;
	private Location location;
	private ResultReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			receiver = intent.getParcelableExtra("receiver");
			if (receiver != null) {
				Log.d(TAG, "receiver not null");
			}
		}
		Log.d(TAG, "onStartCommand");
		distancePreferences = this.getSharedPreferences(
				AppPreferences.DistancePreferences.DistancePrefFile,
				Context.MODE_PRIVATE);
		mClient = new LocationClient(this, this, this);
		while (mClient.isConnecting()) {
			try {
				Thread.sleep(1, 1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mClient.connect();
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		return START_STICKY;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		Log.d(TAG, "locationChanged");
			calculateDistance(location, arg0);
		    location = arg0;

	}

	@Override
	public void onConnected(Bundle arg0) {
		location=mClient.getLastLocation();
		Log.d(TAG, "onConnected");
		mClient.requestLocationUpdates(mLocationRequest, this);
		Log.d("onConnected", "here");

	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "onDisconnected");

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(TAG, "onConnectionFailed");

	}

	public void calculateDistance(Location origin, Location dest) {
		Log.d(TAG, "calculateDistance");
		edit = distancePreferences.edit();
		edit.putLong(
				AppPreferences.DistancePreferences.KEY_DISTANCE,
				distancePreferences.getLong(
						AppPreferences.DistancePreferences.KEY_DISTANCE, 0)
						+ (long) origin.distanceTo(dest));
		long distance = distancePreferences.getLong(
				AppPreferences.DistancePreferences.KEY_DISTANCE, 0);
		Log.d(TAG, "distance travelled->" + (long) origin.distanceTo(dest) + "");
		edit.commit();
		Bundle b = new Bundle();
		b.putLong(AppPreferences.KeyNames.KEY_EXTRAS_LONG_DISTANCE, distance);
		if (receiver != null) {
			receiver.send(1, b);
		}

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		mClient.disconnect();
	}

}
