package com.xihuanicode.tlatoa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnPublishListener;
import com.sromku.simple.fb.entities.Feed;
import com.xihuanicode.tlatoa.db.UserDataSource;
import com.xihuanicode.tlatoa.entity.User;
import com.xihuanicode.tlatoa.utils.Utils;

public class RegistrationActivity extends FragmentActivity  implements OnClickListener {

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

	// Views
	private TextView tvFirstName;
	private TextView tvEmail;
	private Button btnConfirmRegistration;
	private ImageView ivProfilePhoto;
	private ProgressDialog pDlg;
	
	private Context c;
	private UserDataSource dataSource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.registration);
		c = this;
		dataSource = new UserDataSource(this);

		getIntentData();
		setUI();
		// Set profile picture
		setProfilePicture(fbProfilePictureUrl, ivProfilePhoto);
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
	}
	
	private void setProfilePicture(String url, final ImageView iv){
		
		showDialog();
		
		ImageLoader il = Tlatoa.getInstance().getImageLoader();
		
		il.get(url, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				iv.setImageDrawable(getResources().getDrawable(R.drawable.tlatoa_profile_default_photo));
				hideDialog();
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean b) {
				if(response.getBitmap() != null){
					iv.setImageBitmap(response.getBitmap());				
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					Bitmap bitmap = response.getBitmap();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					User user = new User(fbName, fbFirstName, fbLastName, fbMiddleName, fbUserId, fbGender, fbLocationId, fbLocale, fbEmail, fbProfilePictureUrl);
					dataSource.createUser(getApplicationContext(), user, bitmap == null ? null : stream.toByteArray());					
					hideDialog();
				}
			}
		});
	}

	private void saveUserData(){
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "We are registering you...");
        wst.execute(new String[] { Config.USER_REGISTRARION_URL });

//        showDialog();
//        
//        Response.Listener<JSONObject> rl = new Response.Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//				hideDialog();
//			}
//		};
//        
//		Response.ErrorListener er = new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				hideDialog();
//			}
//		};
//		
//		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST,Config.USER_REGISTRARION_URL, null, rl, er) {
//
//			@Override
//			protected Map<String, String> getParams() {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("user", new Gson().toJson(new User(fbName, fbFirstName, fbLastName, fbMiddleName, fbUserId, fbGender, fbLocationId, fbLocale, fbEmail, fbProfilePictureUrl)));
//				return params;
//			}
//
//			@Override
//			public Map<String, String> getHeaders() throws AuthFailureError {
//				HashMap<String, String> headers = new HashMap<String, String>();
//				headers.put("Content-Type", "application/json");
//				return headers;
//			}
//
//		};
//         
//        // Adding request to request queue
//        Tlatoa.getInstance().addToRequestQueue(jsonObjReq, "json_obj_req");
	}
	
	private void goToMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		this.finish();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirmRegistration:
			saveUserData();
			break;
		}
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
			goToMainActivity();
		}

		@Override
		public void onException(Throwable throwable)
		{
			Utils.exceptionToGa(c, (Exception) throwable, false);
		}

		@Override
		public void onThinking()
		{
			showDialog();
		}

		@Override
		public void onComplete(String postId)
		{
			hideDialog();
			goToMainActivity();
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