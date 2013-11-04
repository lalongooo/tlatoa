package com.xihuanicode.tlatoa.activity;

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

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLogoutListener;
import com.xihuanicode.tlatoa.enums.TlatoaStorageFileName;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class ProfileActivity extends FragmentActivity
implements View.OnClickListener, ISimpleDialogListener, ISimpleDialogCancelListener {
	
	protected static final String TAG = "ProfileActivity";
	private SimpleFacebook mSimpleFacebook;
	private Button btnFacebookLogout;
	private ImageView ivProfilePhoto;
	private Typeface typeface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile);
		
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		
		setUI();
	}

	private void setUI() {
		
		// Get views
		btnFacebookLogout = (Button)findViewById(R.id.btnFacebookLogout);
		ivProfilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);
		
		// Get user profile photo from internal storage
		Bitmap bitmap = BitmapFactory.decodeFile(getFileStreamPath(TlatoaStorageFileName.TLATOA_USER_PROFILE_PHOTO.name()).getAbsolutePath());
		
		// Set bitmap to imageView
		ivProfilePhoto.setImageBitmap(bitmap);
		

		
		setTheme(R.style.CustomDarkTheme);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Set facebook typeface
		typeface = Typeface.createFromAsset(getAssets(), "FACEBOLF.TTF");
		btnFacebookLogout.setTypeface(typeface);
		
		
		// Add listeners
		btnFacebookLogout.setOnClickListener(this);
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
		}
	}
	
	private void showConfirmationMessage() {
		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_main_confirmation_title)
				.setMessage(R.string.tlatoa_main_confirmation_exit_message)
				.setPositiveButtonText(R.string.tlatoa_main_confirmation_ok)
				.setNegativeButtonText(R.string.tlatoa_main_confirmation_cancel)
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

}
