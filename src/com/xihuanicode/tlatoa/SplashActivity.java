package com.xihuanicode.tlatoa;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import com.xihuanicode.tlatoa.db.SentenceDataSource;
import com.xihuanicode.tlatoa.utils.Utils;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;

public class SplashActivity extends BaseActivity implements
		ISimpleDialogListener, ISimpleDialogCancelListener {

	// Class members
	private boolean isNetworkAvailable;

	// UI items
	private TextView tvTlatoa;
	private Typeface typeFace;
	
	// Database objects
	private SentenceDataSource datasource;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		// Create database
		datasource = new SentenceDataSource(this);
		datasource.open();

		// Set typeface
		typeFace = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		tvTlatoa = (TextView) this.findViewById(R.id.tvTlatoa);
		tvTlatoa.setTypeface(typeFace);

		isNetworkAvailable = Utils.isNetworkAvailable(getApplicationContext());

		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				if (isNetworkAvailable) {

				} else {
//					showNetworkWarningMessage();
				}
				
				Intent mainIntent = new Intent().setClass(SplashActivity.this, AppOverviewActivity.class);
				startActivity(mainIntent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
				finish();
			}

		};

		Timer timer = new Timer();
		timer.schedule(task, Config.SPLASH_SCREEN_DELAY);
	}

	@Override
	protected void onResume() {
		super.onResume();
		datasource.open();
	}
	
	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

//	private void showNetworkWarningMessage() {
//		SimpleDialogFragment
//				.createBuilder(this, getSupportFragmentManager())
//				.setTitle(R.string.tlatoa_network_warning_title)
//				.setMessage(R.string.tlatoa_network_warning_message)
//				.setPositiveButtonText(android.R.string.ok)
//				.setNegativeButtonText(android.R.string.no)
//				.setRequestCode(42).setTag("custom-tag")
//				.show();
//	}

	// ISimpleDialogCancelListener
	@Override
	public void onCancelled(int requestCode) {
	}

	// ISimpleDialogListener
	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}
	
	@Override
	public void onNeutralButtonClicked(int requestCode) {

	}

	// ISimpleDialogListener
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == 42) {
			startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
		}
	}

}