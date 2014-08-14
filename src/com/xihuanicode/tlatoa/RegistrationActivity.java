package com.xihuanicode.tlatoa;

import java.io.IOException;
import org.apache.http.HttpResponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.sromku.simple.fb.entities.Feed;
import com.xihuanicode.tlatoa.entity.User;
import com.xihuanicode.tlatoa.utils.PrefUtils;
import com.xihuanicode.tlatoa.utils.Utils;

public class RegistrationActivity extends FragmentActivity  implements OnClickListener {
	
	private static final String PROGRESS_DIALOG_TITLE = "Processing...";
	private static final String PROGRESS_DIALOG_MESSAGE = "We're processing your request";

	private Typeface typeface;
	
	// Action bar items
	private TextView actionBarTitle;
	
	// Facebook objects
	private SimpleFacebook mSimpleFacebook;
	
	// Facebook user properties
	private String fbUserId;
	private String fbName;
	private String fbUsername;
	private String fbFirstName;
	private String fbMiddleName;
	private String fbLastName;
	private String fbGender;
	private String fbEmail;
	@SuppressWarnings("unused")
	private String fbBirthday;
	@SuppressWarnings("unused")
	private String fbLink;
	@SuppressWarnings("unused")
	private String fbBio;
	private String fbLocationId;
	private String fbLocale;
	private String fbProfilePictureUrl;
	private Bitmap profilePhotoBitmap;

	// Views
	private TextView tvFirstName;
	private TextView tvEmail;
	private Button btnConfirmRegistration;
	private ImageView ivProfilePhoto;
	private ProgressDialog pDlg;
	
	private Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.registration);
		c = this;

		getIntentData();
		setUI();
	}
	
	private void getIntentData(){
		fbUserId = getIntent().getStringExtra("fbUserId");
		fbName = getIntent().getStringExtra("fbName");
		fbUsername = getIntent().getStringExtra("fbUsername");
		fbFirstName = getIntent().getStringExtra("fbFirstName");
		fbMiddleName = getIntent().getStringExtra("fbMiddleName");
		fbLastName = getIntent().getStringExtra("fbLastName");
		fbGender = getIntent().getStringExtra("fbGender");
		fbBirthday = getIntent().getStringExtra("fbBirthday");
		fbEmail = getIntent().getStringExtra("fbEmail");
		fbLink = getIntent().getStringExtra("fbLink");
		fbBio = getIntent().getStringExtra("fbBio");
		fbLocationId = getIntent().getStringExtra("fbLocationId");
		fbLocale = getIntent().getStringExtra("fbLocale");
		fbProfilePictureUrl = getIntent().getStringExtra("fbProfilePictureUrl");		
	} 

	private void setUI() {
		
		// TODO: Configure theme
		setTheme(R.style.CustomDarkTheme);

		// Get views
		actionBarTitle = (TextView) findViewById(R.id.actionbar_title);
		tvFirstName = (TextView) findViewById(R.id.txRegistrationUsername);
		tvEmail = (TextView) findViewById(R.id.txRegistrationEmail);
		btnConfirmRegistration = (Button) findViewById(R.id.btnConfirmRegistration);
		ivProfilePhoto = (ImageView) findViewById(R.id.ivUserProfilePhoto);

		tvFirstName.setText(fbUsername);
		tvEmail.setText(fbEmail);
		
		// Set typeface
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		btnConfirmRegistration.setTypeface(typeface);
		actionBarTitle.setTypeface(typeface);

		// Add listeners
		btnConfirmRegistration.setOnClickListener(this);
		
		// Set profile picture
		setProfilePicture(fbProfilePictureUrl, ivProfilePhoto);
	}
	
	private void setProfilePicture(String url, final ImageView iv){
		
		this.showDialog();
		
		ImageLoader il = Tlatoa.getInstance().getImageLoader();
		
		il.get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				iv.setImageDrawable(getResources().getDrawable(R.drawable.tlatoa_profile_default_photo));
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean b) {
				iv.setImageBitmap(response.getBitmap());
				PrefUtils.saveUserProfilePicture(getApplicationContext(), response.getBitmap());
			}
		});
		
		PrefUtils.saveUserName(getApplicationContext(), fbFirstName);
		PrefUtils.saveUserEmail(getApplicationContext(), fbEmail);
		
		this.hideDialog();
	}

	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		this.finish();
	}


	private void saveUserData(){

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "We are registering you...");
        wst.execute(new String[] { Config.USER_REGISTRARION_URL });
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirmRegistration:
			saveUserData();
			break;
		}
	}
	
	public void handleResponse(String response) {
		goToMainActivity();
    }
	
    private class WebServiceTask extends AsyncTask<String, Integer, String> {
    	 
        public static final int POST_TASK = 1;
         
        private static final String TAG = "WebServiceTask";
 
        public WebServiceTask(int taskType, Context mContext, String processMessage) {
        	
        }
 
        @Override
        protected void onPreExecute() {
        	showDialog();
        }
 
        protected String doInBackground(String... urls) {
 
            String url = urls[0];
            String result = "";
 

            User user = new User(fbName, fbFirstName, fbLastName, fbMiddleName, fbUserId, fbGender, fbLocationId, fbLocale, fbEmail, fbProfilePictureUrl);
            HttpResponse response = Utils.registerUser(url, user);
 
            if (response == null) {
                return result;
            } else {
 
                try {
 
                    result = Utils.inputStreamToString(response.getEntity().getContent());
 
                } catch (IllegalStateException e) {
                	// TODO: Candidate code to send for reporting
                    Log.e(TAG, e.getLocalizedMessage(), e);
 
                } catch (IOException e) {
                	// TODO: Candidate code to send for reporting
                    Log.e(TAG, e.getLocalizedMessage(), e);
                }
 
            }
 
            return result;
        }
 
        @Override
        protected void onPostExecute(String response) {
            
        	hideDialog();
        	publishTlatoaFeed();
            handleResponse(response);
            
            
        }
    }
    
    private void publishTlatoaFeed(){
    	
		// feed builder
		final Feed feed = new Feed.Builder()
			.setMessage(getString(R.string.tlatoa_registration_publish_feed_message))
			.setName(getString(R.string.tlatoa_registration_publish_feed_name))
			.setCaption(getString(R.string.tlatoa_registration_publish_feed_caption))
			.setPicture(getString(R.string.tlatoa_registration_publish_feed_picture_url))
			.setDescription(getString(R.string.tlatoa_registration_publish_feed_description))
			.setLink(getString(R.string.tlatoa_registration_publish_feed_link))
			.build();
		mSimpleFacebook.publish(feed, onPublishListener);
    }
    
	/*
	 * 
	 * Listener for publishing action
	 * 
	 * */
	final OnPublishListener onPublishListener = new OnPublishListener()
	{

		@Override
		public void onFail(String reason)
		{
			hideDialog();
		}

		@Override
		public void onException(Throwable throwable)
		{
			Utils.exceptionToGa(c, (Exception) throwable, false);
		}

		@Override
		public void onThinking()
		{
//			showDialog();
		}

		@Override
		public void onComplete(String postId)
		{
			hideDialog();
		}
	};
	
	private void showDialog()
	{
		pDlg = ProgressDialog.show(this, getString(R.string.tlatoa_registration_progress_dialog_title) , getString(R.string.tlatoa_registration_progress_dialog_message), true);
	}

	private void hideDialog()
	{
		pDlg.dismiss();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
	
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