package com.xihuanicode.tlatoa.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private int splashDelay = 2000;
	private TextView tvTlatoa;
	private Typeface typeFace;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		typeFace = Typeface.createFromAsset(getAssets(),"DANUBE.TTF");
		tvTlatoa = (TextView) this.findViewById(R.id.tvTlatoa);
		tvTlatoa.setTypeface(typeFace);
		
		

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Intent mainIntent = new Intent().setClass(SplashActivity.this, AppOverviewActivity.class);
				startActivity(mainIntent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
				finish();
			}

		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}

}
