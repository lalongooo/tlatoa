
package com.xihuanicode.tlatoa;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.xihuanicode.tlatoa.enums.GeneralizedScreenSize;
import com.xihuanicode.tlatoa.utils.Utils;
import com.xihuanicode.tlatoa.viewpager.ComplexAdapter;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class AppOverviewActivity extends BaseActivity implements
		View.OnClickListener, ISimpleDialogListener, ISimpleDialogCancelListener {

	protected static final String TAG = AppOverviewActivity.class.getSimpleName();
	private Typeface typeface;
	private Button btnFacebookLogin, btnStart;
	private PageIndicator pIndicator;
	private ViewPager viewPager;
	
	// ActionBar item
	private TextView actionBarTitle;

	// Facebook user properties
	private String fbUserId;
	private String fbName;
	private String fbUsername;
	private String fbFirstName;
	private String fbMiddleName;
	private String fbLastName;
	private String fbGender;
	private String fbBirthday;
	private String fbEmail;
	private String fbLink;
	private String fbBio;
	private String fbLocationId;
	private String fbLocale;
	private String fbProfilePictureUrl;
	
	private SimpleFacebook mSimpleFacebook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_overview);
		setUI();
	}

	private void setUI() {

		// Get views
		btnFacebookLogin = (Button) this.findViewById(R.id.btnFacebookLogin);
		btnStart = (Button) this.findViewById(R.id.btnStart);
		viewPager = (ViewPager) findViewById(R.id.pager);
		pIndicator = (CirclePageIndicator) findViewById(R.id.indicator);		
		actionBarTitle = (TextView) findViewById(R.id.actionbar_title);

		// Set typeface
		typeface = Typeface.createFromAsset(getAssets(), "FACEBOLF.TTF");
		btnFacebookLogin.setTypeface(typeface);
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		actionBarTitle.setTypeface(typeface);
		btnStart.setTypeface(typeface);

		// Viewpager configuration
		ComplexAdapter adapter = new ComplexAdapter(this);
		viewPager.setAdapter(adapter);
		pIndicator.setViewPager(viewPager);
		
		// Add listeners
		btnFacebookLogin.setOnClickListener(this);
		btnStart.setOnClickListener(this);
	}

	// Login listener
	private OnLoginListener mOnLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
			toast(reason);
			Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable) {
			toast(throwable.getMessage());
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {

		}

		@Override
		public void onLogin() {
			mSimpleFacebook.getProfile(new OnProfileListener() {
				
				@Override
				public void onThinking() {
					
				}

				@Override
				public void onException(Throwable throwable) {
					// TODO: Candidate code to send for reporting
				}

				@Override
				public void onFail(String reason) {
					// TODO: Candidate code to send for reporting
				}

				@Override
				public void onComplete(Profile profile) {			
					handleFbProfile(profile);
				}
			});
		}

		@Override
		public void onNotAcceptingPermissions(Type type) {
			toast("You didn't accept read permissions");
			
		}
		
	};

	void showYesMessage() {
		Toast.makeText(getApplicationContext(),"YES option has been selected!", Toast.LENGTH_SHORT).show();
	}

	void showNoMessage() {
		Toast.makeText(getApplicationContext(), "NO option has been selected!", Toast.LENGTH_SHORT).show();
	}

	void showNeutralMessage() {
		Toast.makeText(getApplicationContext(), "NEUTRAL option has been selected!", Toast.LENGTH_SHORT).show();
	}
	
	private void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void handleFbProfile(Profile profile){
		
		if(profile != null){
			
			try{
				
				fbUserId = profile.getId() != null ? profile.getId() : "" ;
				fbName = profile.getName();
				fbUsername = profile.getFirstName();
				fbFirstName = profile.getFirstName();
				fbMiddleName = profile.getMiddleName();
				fbLastName = profile.getLastName();
				fbGender = profile.getGender();
				fbBirthday = profile.getBirthday();
				fbEmail = profile.getEmail();
				fbLink = profile.getLink();
				fbBio = profile.getId();
				fbLocationId = profile.getLocation() != null ? profile.getLocation().getName() : "" ;
				fbLocale = profile.getLocale();
				fbProfilePictureUrl = getFacebookProfilePictureURL(fbUserId, Config.FB_PROFILE_PICTURE_URL_BASE);
				
				goToRegistrationActivity();
				
			}catch (Exception ex){
				// TODO: Candidate code to send for reporting
			}
			
		}else{
			// TODO: Candidate code to send for reporting
		}
		
	}

	private void goToRegistrationActivity() {
		Intent intent = new Intent(this, RegistrationActivity.class);
		
		intent.putExtra("fbUserId", fbUserId);
		intent.putExtra("fbName", fbName);
		intent.putExtra("fbUsername", fbUsername);
		intent.putExtra("fbFirstName", fbFirstName);
		intent.putExtra("fbMiddleName", fbMiddleName);
		intent.putExtra("fbLastName", fbLastName);
		intent.putExtra("fbGender", fbGender);
		intent.putExtra("fbBirthday", fbBirthday);
		intent.putExtra("fbEmail", fbEmail);
		intent.putExtra("fbLink", fbLink);
		intent.putExtra("fbBio", fbBio);
		intent.putExtra("fbLocationId", fbLocationId);
		intent.putExtra("fbLocale", fbLocale);
		intent.putExtra("fbProfilePictureUrl", fbProfilePictureUrl);

		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		this.finish();
	}

	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		this.finish();
	}
	
	private String getFacebookProfilePictureURL(String fbUserId, String urlBase){
		
		String profilePictureURL = "";
		
		if (Utils.getDeviceGeneralizedScreenSize(getApplicationContext()) == GeneralizedScreenSize.SCREENLAYOUT_SIZE_SMALL){
			profilePictureURL = urlBase .replace("fbUserId", fbUserId) + "normal";
		}else if(Utils.getDeviceGeneralizedScreenSize(getApplicationContext()) == GeneralizedScreenSize.SCREENLAYOUT_SIZE_NORMAL){
			profilePictureURL = urlBase .replace("fbUserId", fbUserId) + "large";
		}else if(Utils.getDeviceGeneralizedScreenSize(getApplicationContext()) == GeneralizedScreenSize.SCREENLAYOUT_SIZE_LARGE){
			profilePictureURL = urlBase .replace("fbUserId", fbUserId) + "large";
		}else if(Utils.getDeviceGeneralizedScreenSize(getApplicationContext()) == GeneralizedScreenSize.SCREENLAYOUT_SIZE_XLARGE){
			profilePictureURL = urlBase .replace("fbUserId", fbUserId) + "large";
		}else{
			profilePictureURL = urlBase .replace("fbUserId", fbUserId) + "large";
		}
		
		return profilePictureURL;
		
	}

	@Override
	public void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		// Config UI state
		if (mSimpleFacebook.isLogin()) {
			btnFacebookLogin.setVisibility(Button.GONE);
			btnStart.setVisibility(Button.VISIBLE);
		} else {
			btnFacebookLogin.setVisibility(Button.VISIBLE);
			btnStart.setVisibility(Button.GONE);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnFacebookLogin:
			if(!Utils.isNetworkAvailable(getApplicationContext())){
				showNetworkWarningMessage();
			}else{
				mSimpleFacebook.login(mOnLoginListener);
			}			
			break;
		case R.id.btnStart:
			goToMainActivity();
			break;
		}
	}
	
	private void showNetworkWarningMessage() {
		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_network_warning_title)
				.setMessage(R.string.tlatoa_network_warning_message)
				.setPositiveButtonText(android.R.string.ok)
				.setNegativeButtonText(android.R.string.no).setRequestCode(42)
				.setTag("custom-tag").show();
	}

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