package com.xihuanicode.tlatoa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.xihuanicode.tlatoa.customlistview.TranslationPlayListAdapter;
import com.xihuanicode.tlatoa.db.Phrase;
import com.xihuanicode.tlatoa.db.PhraseDataSource;
import com.xihuanicode.tlatoa.utils.BitmapDownloader;
import com.xihuanicode.tlatoa.utils.Utils;

public class TranslationResultActivity extends Activity implements
		View.OnClickListener {

	private static final String TAG = "com.xihuanicode.tlatoa.TranslationResultActivity";

	private static final int ANIMATION_DURATION = 500;

	// The phrase catched by the Google speech to text intent
	private String phrase;

	// The images sequence returned for the phrase from the web service
	private Bitmap[] imagesSequence;

	// All static variables
	static final String URL = "http://api.androidhive.info/music/music.xml";

	// XML node keys
	public static final String KEY_SONG = "song"; // parent node
	public static final String KEY_ID = "id";
	public static final String KEY_PHRASE = "title";
	public static final String KEY_CREATED_AT = "artist";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_THUMB_URL = "thumb_url";

	// UI items
	private ImageView ivTranslationResultAnimation,
			ivTranslationResultPlayButton;
	private ListView list;
	private TranslationPlayListAdapter adapter;

	// Database classes
	private PhraseDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_result);

		getIntentData();
		setUI();

		if(phrase != null){
			new Translate(this, "We are translating...").execute(phrase);
		}
		
	}

	private void getIntentData() {
		Bundle extras = getIntent().getExtras();
		
		if(extras != null){
			phrase = extras.getString("phrase");
		}		

		datasource = new PhraseDataSource(this);
		datasource.open();
		datasource.createPhrase(phrase);

	}

	private void setUI() {

		// Get views
		list = (ListView) findViewById(R.id.list);
		ivTranslationResultAnimation = (ImageView) findViewById(R.id.ivTranslationResultAnimation);
		ivTranslationResultPlayButton = (ImageView) findViewById(R.id.ivTranslationResultPlayButton);

		// Add listeners
		ivTranslationResultPlayButton.setOnClickListener(this);

		List<Phrase> phrases = datasource.getAllPhrase();
		if (phrases.size() > 0) {
			adapter = new TranslationPlayListAdapter(this, phrases);
			list.setAdapter(adapter);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTranslationResultPlayButton:
			playTranslation(imagesSequence);
			break;
		}

	}

	private class Translate extends AsyncTask<String, Integer, Bitmap[]> {

		private ProgressDialog pDlg;
		private Context mContext;
		private String processMessage = "Processing...";

		public Translate(Context mContext, String processMessage) {
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
			showProgressDialog();
		}

		@Override
		protected Bitmap[] doInBackground(String... phrase) {

			String stringResult = null;
			Bitmap[] photos = null;
			HttpResponse response = null;

			try {

				String url = URLEncoder.encode(phrase[0], "UTF8");

				response = Utils.doResponse("http://tlatoa.herokuapp.com/manager/api/sentence?phrase=" + url, 2);
				stringResult = Utils.inputStreamToString(response.getEntity().getContent());
				Log.i(TAG, stringResult);
			} catch (IllegalStateException e) {
				// TODO: Candidate code to send for reporting
				Log.i(TAG, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: Candidate code to send for reporting
				Log.i(TAG, e.getMessage());
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: Candidate code to send for reporting
				e.printStackTrace();
			}

			if (response == null) {
				return null;
			} else {
				try {
					JSONArray jsonArray = new JSONArray(stringResult);
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					JSONArray resourcesArray = (JSONArray) jsonObject.get("resources");

					photos = new Bitmap[resourcesArray.length()];

					for (int i = 0; i < resourcesArray.length(); i++) {
						jsonObject = resourcesArray.getJSONObject(i);

						Bitmap bitmap;

						// Get bitmap from HerokuApp
						BitmapDownloader bitmapDownloader = new BitmapDownloader();
						bitmap = bitmapDownloader.downloadBitmap(jsonObject
								.getString("resourceURL"));

						// Get bytes from bitmap
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

						// Add bitmap to array
						photos[i] = bitmap;

					}

					Log.i(TAG, jsonObject.toString());
				} catch (JSONException e) {
					// TODO: Candidate code to send for reporting
					Log.i(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			return photos;

		}

		@Override
		protected void onPostExecute(Bitmap[] images) {

			pDlg.dismiss();
			imagesSequence = images;
			playTranslation(images);

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("deprecation")
	private void playTranslation(Bitmap... images) {

		if (images != null) {

			try {
				ivTranslationResultPlayButton.setVisibility(View.INVISIBLE);

				AnimationDrawable animationDrawable = (AnimationDrawable) getResources()
						.getDrawable(R.drawable.tlatoa_translation_result_anim);
				animationDrawable.setOneShot(true);

				if (animationDrawable.getNumberOfFrames() == 0) {
					for (int i = 0; i < images.length; i++) {

						BitmapDrawable bitmapDrawable = new BitmapDrawable(
								getResources(), images[i]);
						Drawable drawable = (Drawable) bitmapDrawable;
						animationDrawable
								.addFrame(drawable, ANIMATION_DURATION);

					}
				}

				// Pass our animation drawable to our custom drawable class
				CustomAnimationDrawable cad = new CustomAnimationDrawable(
						animationDrawable) {
					@Override
					void onAnimationFinish() {
						ivTranslationResultPlayButton
								.setVisibility(View.VISIBLE);
					}
				};

				// Set the views drawable to our custom drawable
				ivTranslationResultAnimation
						.setImageResource(android.R.color.transparent);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					setBakgroundJELLYBean(cad);
				} else {
					ivTranslationResultAnimation.setBackgroundDrawable(cad);
				}

				// Start the animation
				cad.start();
			} catch (NotFoundException e) {
				// TODO: Candidate code to send for reporting
				Log.i(TAG, e.getMessage());
				e.printStackTrace();
			}

		}

	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setBakgroundJELLYBean(CustomAnimationDrawable cad) {
		ivTranslationResultAnimation.setBackground(cad);
	}

}