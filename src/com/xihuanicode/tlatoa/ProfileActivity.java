package com.xihuanicode.tlatoa;

import java.io.FileNotFoundException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLogoutListener;
import com.xihuanicode.tlatoa.enums.TlatoaStorageFileName;
import com.xihuanicode.tlatoa.utils.Utils;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class ProfileActivity extends FragmentActivity
implements View.OnClickListener, ISimpleDialogListener, ISimpleDialogCancelListener {
	
	protected static final String TAG = "ProfileActivity";

	private SimpleFacebook mSimpleFacebook;
	private Typeface typeface;
	private TextView tvUsername, tvEmail;
	private Button btnFacebookLogout;
	private ImageView ivProfilePhoto;
	
	// Action bar items
	private ImageView actionBarBack;
	private TextView actionBarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile);
		
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		
		setUI();
	}

	private void setUI() {
		
		// TODO: Configure theme
		setTheme(R.style.CustomDarkTheme);
		
		// Get views
		actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
		actionBarTitle = (TextView) findViewById(R.id.actionbar_title);
		btnFacebookLogout = (Button)findViewById(R.id.btnFacebookLogout);
		ivProfilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
		tvUsername = (TextView) findViewById(R.id.txProfileUsername);
		tvEmail = (TextView) findViewById(R.id.txProfileEmail);
		
		Bitmap bitmap = null;
		String userName = "";
		String userEmail = "";
		
		try {
			
			userName = Utils.inputStreamToString(openFileInput(TlatoaStorageFileName.TLATOA_FB_USER_NAME.name()));
			userEmail = Utils.inputStreamToString(openFileInput(TlatoaStorageFileName.TLATOA_FB_USER_EMAIL.name()));
			bitmap = BitmapFactory.decodeFile(getFileStreamPath(TlatoaStorageFileName.TLATOA_USER_PROFILE_PHOTO.name()).getAbsolutePath());
			
			
		} catch (FileNotFoundException e) {
			// TODO: Candidate code to send for reporting
		}

		
		// Set values on screen
		tvUsername.setText(userName);
		tvEmail.setText(userEmail);
		ivProfilePhoto.setImageBitmap(bitmap);
		
		// Set facebook typeface
		typeface = Typeface.createFromAsset(getAssets(), "FACEBOLF.TTF");
		btnFacebookLogout.setTypeface(typeface);
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		actionBarTitle.setTypeface(typeface);
		
		
		// Add listeners
		btnFacebookLogout.setOnClickListener(this);
		actionBarBack.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean returnValue = false;
		try {

			switch (item.getItemId()) {
			case android.R.id.home:
				finish();
			default:
				returnValue = super.onOptionsItemSelected(item);
			}

		} catch (Exception ex) {
			Log.e("Error", ex.getMessage());
		}

		return returnValue;
	}

	
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnFacebookLogout:
				showConfirmationMessage();			
				break;
			case R.id.actionbar_back:
				actionBarBack.setSelected(true);
				goBack();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goBack();
	}

	private void goBack(){
		Intent intent = new Intent().setClass(ProfileActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void showConfirmationMessage() {
		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_main_exit_confirmation_title)
				.setMessage(R.string.tlatoa_main_exit_confirmation_exit_message)
				.setPositiveButtonText(R.string.tlatoa_main_exit_confirmation_ok_option)
				.setNegativeButtonText(R.string.tlatoa_main_exit_confirmation_cancel_option)
				.setRequestCode(40)
				.setTag("custom-tag")
				.show();
	}
	
	// ISimpleDialogCancelListener
	@Override
	public void onCancelled(int requestCode) {
		if (requestCode == 40) {}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		if (requestCode == 40) {}
	}
	
	// ISimpleDialogListener
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == 40) {
			mSimpleFacebook.logout(mOnLogoutListener);
		}
	}
	
	// Logout listener
	private OnLogoutListener mOnLogoutListener = new OnLogoutListener()
	{

		@Override
		public void onFail(String reason)
		{
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable)
		{
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking()
		{
			// show progress bar or something to the user while login is happening
			Log.e(TAG, "Thinking...");
		}

		@Override
		public void onLogout()
		{
			Intent mainIntent = new Intent().setClass(ProfileActivity.this, AppOverviewActivity.class);
			startActivity(mainIntent);
			overridePendingTransition(R.anim.open_next, R.anim.close_main);
			finish();
			
		}

	};

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
