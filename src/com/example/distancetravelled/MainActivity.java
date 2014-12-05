package com.example.distancetravelled;

import android.support.v7.app.ActionBarActivity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		DistanceReceiver.Receiver,OnClickListener {

	private final static String TAG = "MainActivity";
	public DistanceReceiver mReceiver;
	private TextView text;
	private Button button;
    private int flag=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mReceiver = new DistanceReceiver(new Handler());
		mReceiver.setReceiver(this);
		text = (TextView) findViewById(R.id.main_textview);
		button=(Button)findViewById(R.id.main_button_control);
		button.setOnClickListener(this);
		Log.d(TAG, "onCreate");
		text.setText(getSharedPreferences(
				AppPreferences.DistancePreferences.DistancePrefFile,
				MODE_PRIVATE).getLong(
				AppPreferences.DistancePreferences.KEY_DISTANCE, 0)
				+ " meters");
		if (isMyServiceRunning(DistanceService.class)) {
			stopDistanceService();
			updateButtonText();
			flag=0;
			startDistanceService();
			updateButtonText();
			flag=1;
			
		} else {
             startDistanceService();
             updateButtonText();
             flag=1;
             
		}
		
	}

	public void stopDistanceService() {
		Intent stopIntent = new Intent(this, DistanceService.class);
		stopService(stopIntent);

	}
	public void startDistanceService(){
		Intent startIntent=new Intent(this,DistanceService.class);
		startIntent.putExtra(AppPreferences.KeyNames.KEY_SEND_RECIEVER,mReceiver);
		startService(startIntent);
	}
	public void updateButtonText(){
		if(flag==1){
			button.setText("start");
		}else{
			button.setText("stop");
		}
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		Log.d(TAG, "isMyServiceRunning");
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
		Log.d(TAG, "onReceiveResult");
		text.setText(resultData.getLong("long") + " meters");
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==button.getId()){
			if(flag==0){
				startDistanceService();
				updateButtonText();
				flag=1;
			}else{
				stopDistanceService();
				updateButtonText();
				flag=0;
			}
		}
		
	}
}
