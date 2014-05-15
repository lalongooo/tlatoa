package com.xihuanicode.tlatoa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.makeramen.RoundedImageView;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnPublishListener;
import com.sromku.simple.fb.entities.Feed;
import com.xihuanicode.tlatoa.entity.User;
import com.xihuanicode.tlatoa.enums.NetworkAccessResultCode;
import com.xihuanicode.tlatoa.enums.TlatoaStorageFileName;
import com.xihuanicode.tlatoa.utils.Utils;

public class RegistrationActivity extends Activity implements OnClickListener {

	private static final String SERVICE_URL = "http://tlatoa.herokuapp.com/kerberos/api/user";
	
	private static final String PROGRESS_DIALOG_TITLE = "Processing...";
	private static final String PROGRESS_DIALOG_MESSAGE = "We're processing your request";

	private Typeface typeface;
	
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
	private RoundedImageView ivProfilePhoto;
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

		// Get views
		tvFirstName = (TextView) findViewById(R.id.txRegistrationUsername);
		tvEmail = (TextView) findViewById(R.id.txRegistrationEmail);
		btnConfirmRegistration = (Button) findViewById(R.id.btnConfirmRegistration);
		ivProfilePhoto = (RoundedImageView) findViewById(R.id.ivProfilePhoto);

		tvFirstName.setText(fbUsername);
		tvEmail.setText(fbEmail);
		
		// Set typeface
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		btnConfirmRegistration.setTypeface(typeface);

		// Add listeners
		btnConfirmRegistration.setOnClickListener(this);
		
		new GetProfilePhoto().execute(fbProfilePictureUrl);
	}

	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		this.finish();
	}

	private class GetProfilePhoto extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {

			// TODO: Candidate code to send for reporting
			try {
				try {
					
					// Get profile photo from Facebook
//					profilePhotoBitmap = BitmapFactory.decodeStream((InputStream) new URL(urls[0]).getContent());
					
					URL imageURL = new URL(urls[0]);
					profilePhotoBitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
					
					try{
						
						// Get bytes from bitmap
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						profilePhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						
						// Save the image to internal storage
						FileOutputStream fos = openFileOutput(TlatoaStorageFileName.TLATOA_USER_PROFILE_PHOTO.name(), Context.MODE_PRIVATE);
						fos.write(stream.toByteArray());
						fos.close();
						
						fos = openFileOutput(TlatoaStorageFileName.TLATOA_FB_USER_NAME.name(), Context.MODE_PRIVATE);
						fos.write(fbFirstName.getBytes());
						fos.close();
						
						fos = openFileOutput(TlatoaStorageFileName.TLATOA_FB_USER_EMAIL.name(), Context.MODE_PRIVATE);
						fos.write(fbEmail.getBytes());
						fos.close();
						
					}catch (FileNotFoundException ex){
						// TODO: Candidate code to send for reporting
					}catch (IOException ex){
						// TODO: Candidate code to send for reporting
					}
					
					return NetworkAccessResultCode.SUCCESSFUL_OPERATION.name();
					
				} catch (MalformedURLException e) {
					// TODO: Candidate code to send for reporting
					return NetworkAccessResultCode.MAL_FORMED_URL.name();
				} catch (IOException e) {
					// TODO: Candidate code to send for reporting
					return NetworkAccessResultCode.IO_EXCEPTION.name();
				}
			} catch (Exception e) {
				// TODO: Candidate code to send for reporting
				return NetworkAccessResultCode.GENERAL_EXCEPTION.name();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			ivProfilePhoto.setImageBitmap(profilePhotoBitmap);
		}
	}

	private void saveUserData(){

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "We are registering you...");
        wst.execute(new String[] { SERVICE_URL });
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
			.setMessage(Utils.getStringResource(getApplicationContext(), R.string.tlatoa_registration_publish_feed_message))
			.setName(Utils.getStringResource(getApplicationContext(), R.string.tlatoa_registration_publish_feed_name))
			.setCaption(Utils.getStringResource(getApplicationContext(), R.string.tlatoa_registration_publish_feed_caption))
			.setDescription(Utils.getStringResource(getApplicationContext(), R.string.tlatoa_registration_publish_feed_description))
			.setPicture(Utils.getStringResource(getApplicationContext(), R.string.tlatoa_registration_publish_feed_picture_url))
			.setLink(Utils.getStringResource(getApplicationContext(), R.string.tlatoa_registration_publish_feed_link))
			.build();
		mSimpleFacebook.publish(feed, onPublishListener);
    }
    
	/*
	 * 
	 * Listener for publishing action
	 * 
	 * */
	final OnPublishListener onPublishListener = new SimpleFacebook.OnPublishListener()
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
		pDlg = ProgressDialog.show(this, PROGRESS_DIALOG_TITLE , PROGRESS_DIALOG_MESSAGE, true);
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