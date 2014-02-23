
package com.xihuanicode.tlatoa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sromku.simple.fb.Permissions;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebook.OnProfileRequestListener;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.xihuanicode.tlatoa.viewpager.ComplexAdapter;

public class AppOverviewActivity extends Activity implements
		View.OnClickListener {

	protected static final String TAG = "TLATOA";
	private Typeface typeface;
	private Button btnFacebookLogin, btnStart;
	private ProgressDialog mProgress;
	private PageIndicator pIndicator;
	private ViewPager viewPager;
	
	// ActionBar item
	TextView actionBarTitle;

	// Facebook user properties
	private static final String FB_PROFILE_PICTURE_URL_BASE = "http://graph.facebook.com/fbUserId/picture?type=large";
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
	
	SimpleFacebook mSimpleFacebook;

	Permissions[] permissions = new Permissions[]
    {
			Permissions.BASIC_INFO,
			Permissions.EMAIL,
			Permissions.USER_BIRTHDAY,
			Permissions.USER_PHOTOS,
			Permissions.PUBLISH_ACTION,
			Permissions.PUBLISH_STREAM,
			Permissions.USER_LOCATION
    };

	SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration
			.Builder()
			.setAppId("143269149215368")
			.setNamespace("com.xihuanicode.tlatoa.activity")
			.setPermissions(permissions)
			.build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_overview);

		SimpleFacebook.setConfiguration(configuration);
		mSimpleFacebook = SimpleFacebook.getInstance(this);

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

		// Config UI state
		if (mSimpleFacebook.isLogin()) {
			btnFacebookLogin.setVisibility(Button.INVISIBLE);
			btnStart.setVisibility(Button.VISIBLE);
		} else {
			btnFacebookLogin.setVisibility(Button.VISIBLE);
			btnStart.setVisibility(Button.INVISIBLE);
		}

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
			mSimpleFacebook.getProfile(new OnProfileRequestAdapter());
		}

		@Override
		public void onNotAcceptingPermissions() {
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

	private void showTermsAndConditions() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE: // yes
					showYesMessage();
					break;
				case DialogInterface.BUTTON_NEGATIVE: // no
					showNoMessage();
					break;
				case DialogInterface.BUTTON_NEUTRAL: // neutral
					showNeutralMessage();
					break;
				default:
					// nothing
					break;
				}
			}
		};

		AlertDialog ad = new AlertDialog
				.Builder(this)
				.setMessage("Blah blah blah.\n Fine pring.\n Do you accept all our terms and conditions?")
				.setIcon(R.drawable.tlatoa_icon).setTitle("Terms of Service")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener)
				.setNeutralButton("Cancel", dialogClickListener)
				.setCancelable(false)
				.create();
		ad.show();

	}

	private void showDialog() {
		mProgress = ProgressDialog.show(this, "Thinking", "Waiting for Facebook", true);
	}

	private void hideDialog() {
		mProgress.hide();
	}

	private void startTranslating() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					goToMainActivity();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?")
				.setPositiveButton("Yes", dialogClickListener)
				.setNegativeButton("No", dialogClickListener)
				.show();
	}

	private void toast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	public class OnProfileRequestAdapter implements OnProfileRequestListener {

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

	}
	
	private void handleFbProfile(Profile profile){
		
		if(profile != null){
			
			try{
				
				fbUserId = profile.getId() != null ? profile.getId() : "" ;
				fbName = profile.getName();
				fbUsername = profile.getUsername();
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
				fbProfilePictureUrl = FB_PROFILE_PICTURE_URL_BASE.replace("fbUserId", fbUserId);
				
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

	@Override
	public void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
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
			mSimpleFacebook.login(mOnLoginListener);
			break;
		case R.id.btnStart:
			goToMainActivity();
			break;
		}
	}

}