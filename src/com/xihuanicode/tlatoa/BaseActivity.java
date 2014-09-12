package com.xihuanicode.tlatoa;

import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;

public class BaseActivity extends FragmentActivity {

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

}
