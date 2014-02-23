package com.xihuanicode.tlatoa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.makeramen.RoundedImageView;
import com.xihuanicode.tlatoa.entity.Role;
import com.xihuanicode.tlatoa.entity.User;
import com.xihuanicode.tlatoa.enums.NetworkAccessResultCode;
import com.xihuanicode.tlatoa.enums.TlatoaStorageFileName;
import com.xihuanicode.tlatoa.utils.Utils;

@SuppressWarnings("unused")
public class RegistrationActivity extends Activity implements OnClickListener {

	private static final String SERVICE_URL = "http://tlatoa.herokuapp.com/kerberos/api/user";
	
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
	private Bitmap profilePhotoBitmap;

	// Views
	private TextView tvFirstName;
	private TextView tvEmail;
	private Button btnConfirmRegistration;
	private RoundedImageView ivProfilePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.registration);

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
					profilePhotoBitmap = BitmapFactory.decodeStream((InputStream) new URL(urls[0]).getContent());
					
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
        public static final int GET_TASK = 2;
         
        private static final String TAG = "WebServiceTask";
        private static final int CONN_TIMEOUT = 5000;
        
        private static final int SOCKET_TIMEOUT = 5000;
         
        private int taskType = GET_TASK;
        private Context mContext = null;
        private String processMessage = "Processing...";
 
        private ProgressDialog pDlg;
 
        public WebServiceTask(int taskType, Context mContext, String processMessage) {
 
            this.taskType = taskType;
            this.mContext = mContext;
            this.processMessage = processMessage;
        }
 
        @SuppressWarnings("deprecation")
		private void showProgressDialog() {
             
            pDlg = new ProgressDialog(mContext);
            pDlg.setMessage(processMessage);
            pDlg.setProgressDrawable(mContext.getWallpaper());
            pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDlg.setCancelable(false);
            pDlg.show();
 
        }
 
        @Override
        protected void onPreExecute() {
 
//            hideKeyboard();
            showProgressDialog();
 
        }
 
        protected String doInBackground(String... urls) {
 
            String url = urls[0];
            String result = "";
 
            HttpResponse response = Utils.doResponse(url, taskType);
 
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
             
            handleResponse(response);
            pDlg.dismiss();
             
        }
         
        // Establish connection and socket (data retrieval) timeouts
        private HttpParams getHttpParams() {
             
            HttpParams htpp = new BasicHttpParams();
             
            HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
             
            return htpp;
        }          
    }
}