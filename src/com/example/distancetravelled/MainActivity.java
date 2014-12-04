package com.example.distancetravelled;



import android.support.v7.app.ActionBarActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements DistanceReceiver.Receiver {
	
  private final static String TAG="MainActivity";
  public DistanceReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mReceiver = new DistanceReceiver(new Handler());
        mReceiver.setReceiver(this);
		Log.d(TAG,"onCreate");
		if(isMyServiceRunning(DistanceService.class)){
			Log.d(TAG,"service running");
		}else{
			Log.d(TAG,"service not running");
			Intent intent=new Intent(this,DistanceService.class);
			intent.putExtra("receiver", mReceiver);
			startService(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		Log.d(TAG,"isMyServiceRunning");
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		Log.d(TAG,"onReceiveResult");
		TextView textview=(TextView)findViewById(R.id.main_textview);
		textview.setText(resultData.getLong("long")+" meters");
		
	}
}
