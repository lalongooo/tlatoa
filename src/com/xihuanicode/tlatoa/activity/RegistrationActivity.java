package com.xihuanicode.tlatoa.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.RoundedImageView;
import com.xihuanicode.tlatoa.enums.NetworkAccessResultCode;
import com.xihuanicode.tlatoa.enums.TlatoaStorageFileName;

public class RegistrationActivity extends Activity implements OnClickListener {

//	private static final String FACEBOOK_PROFILE_PHOTO_URL = "http://graph.facebook.com/userId/picture?type=large";
	private static final String FACEBOOK_PROFILE_PHOTO_URL = "http://graph.facebook.com/1040166239/picture?type=large";	

	private String firstName;
	private String email;
	private String userId;
	private Bitmap profilePhotoBitmap;

	private TextView tvFirstName;
	private TextView tvEmail;
	private Button btnConfirmRegistration;
	private RoundedImageView ivProfilePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.registration);

		firstName = getIntent().getStringExtra("firstName");
		email = getIntent().getStringExtra("email");
		userId = getIntent().getStringExtra("userId");

		setUI();
	}

	private void setUI() {

		// Get views
		tvFirstName = (TextView) findViewById(R.id.txtFirstName);
		tvEmail = (TextView) findViewById(R.id.txtUserMail);
		btnConfirmRegistration = (Button) findViewById(R.id.btnConfirmRegistration);
		ivProfilePhoto = (RoundedImageView) findViewById(R.id.ivProfilePhoto);

		tvFirstName.setText(firstName);
		tvEmail.setText(email);

		// Add listeners
		btnConfirmRegistration.setOnClickListener(this);
		
		new GetProfilePhoto().execute(FACEBOOK_PROFILE_PHOTO_URL);
	}

	private void processUserData() {
		Toast.makeText(this, "Upload data to server", Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "Save data locally", Toast.LENGTH_SHORT).show();
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
					profilePhotoBitmap = BitmapFactory.decodeStream((InputStream) new URL(urls[0].replace("userId", userId)).getContent());
					
					// Get bytes from bitmap
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					profilePhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					
					// Save the image to internal storage
					byte[] byteArray = stream.toByteArray();
					String FILENAME = TlatoaStorageFileName.TLATOA_USER_PROFILE_PHOTO.name();
					FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
					fos.write(byteArray);
					fos.close();
					
					return NetworkAccessResultCode.SUCCESSFUL_OPERATION.name();
				} catch (MalformedURLException e) {
					return NetworkAccessResultCode.MAL_FORMED_URL.name();
				} catch (IOException e) {
					return NetworkAccessResultCode.IO_EXCEPTION.name();
				}
			} catch (Exception e) {
				return NetworkAccessResultCode.GENERAL_EXCEPTION.name();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			ivProfilePhoto.setImageBitmap(profilePhotoBitmap);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnConfirmRegistration:
			processUserData();
			goToMainActivity();
			break;

		}
	}

}
