package com.xihuanicode.tlatoa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
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
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xihuanicode.tlatoa.customlistview.TranslationPlayListAdapter;
import com.xihuanicode.tlatoa.db.Phrase;
import com.xihuanicode.tlatoa.db.SentenceDataSource;
import com.xihuanicode.tlatoa.entity.Sentence;
import com.xihuanicode.tlatoa.entity.SentenceResource;
import com.xihuanicode.tlatoa.utils.BitmapDownloader;
import com.xihuanicode.tlatoa.utils.Utils;

public class TranslationResultActivity extends Activity implements
		View.OnClickListener {

	private static final String TAG = "ResultActivity";

	private static final int ANIMATION_DURATION = 500;

	private static final String TLATOA_SENTENCE_WS_URL = "http://tlatoa.herokuapp.com/manager/api/sentence?phrase=";


	// The images sequence returned for the phrase from the web service
	private Sentence sentence;

	// UI items
	private ImageView ivTranslationResultAnimation, ivTranslationResultPlayButton;
	private ListView lvTranslationList;
	private TranslationPlayListAdapter adapter;

	// Database classes
	private SentenceDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.translation_result);

		// Database object initialization
		datasource = new SentenceDataSource(this);

		setUI();
		
		Bundle extras = getIntent().getExtras();
		String phrase = null;
		
		if(extras != null){
			phrase = extras.getString("phrase");
		}
		
		if(phrase != null){
			new Translate(this, "We are translating...").execute(phrase);
		}
		
	}

	private void setUI() {

		// Get views
		lvTranslationList = (ListView) findViewById(R.id.list);
		ivTranslationResultAnimation = (ImageView) findViewById(R.id.ivTranslationResultAnimation);
		ivTranslationResultPlayButton = (ImageView) findViewById(R.id.ivTranslationResultPlayButton);

		// Add listeners
		ivTranslationResultPlayButton.setOnClickListener(this);

		// Load all translated sentences
		List<Phrase> phrases = datasource.getAllSentences();
		
		// Listview configuration
		if (phrases.size() > 0) {
			adapter = new TranslationPlayListAdapter(this, phrases);
			lvTranslationList.setAdapter(adapter);
		}
		
		lvTranslationList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				int sentenceId = Integer.valueOf(((TextView)view.findViewById(R.id.tlatoa_phrase_id)).getText().toString());
				Sentence s = datasource.getSentenceById(sentenceId);
				playTranslation(s);
				
				
			}
		});		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTranslationResultPlayButton:
			playTranslation(this.sentence);
			break;
		}

	}

	private class Translate extends AsyncTask<String, Integer, Sentence> {

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
		protected Sentence doInBackground(String... phrase) {

			// Creating the entity object to store it in the database
			Sentence sentence = new Sentence();
			
			// Http request objects
			String jsonResult = null;
			HttpResponse response = null;
			
			// Check for sentence in the local database			
			sentence.setSentence(phrase[0]);			
			int sentenceId = datasource.existsInLocalDb(sentence);
			
			if(sentenceId > 0){
				sentence = datasource.getSentenceById(sentenceId);
			}else{
				
				// Get the first JSON result from the Tlatoa Web Service
				try {
	
					String url = URLEncoder.encode(phrase[0], "UTF8");
					response = Utils.doResponse(TLATOA_SENTENCE_WS_URL + url, 2);
					
					if (response != null) {
						jsonResult = Utils.inputStreamToString(response.getEntity().getContent());
					}
					
					Log.i(TAG, jsonResult);
					
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
				
				
				// Get the image elements
				if (response == null) {
					return null;
				} else {
					
					try {
						
						// Getting the first and unique element
						JSONArray jsonArray = new JSONArray(jsonResult);
						JSONObject jsonObject = jsonArray.getJSONObject(0);
	
						sentence.setSentenceId(jsonObject.getInt("sentenceId"));
						sentence.setSentence(jsonObject.getString("sentence"));
						List<SentenceResource> resources = new ArrayList<SentenceResource>();
											
						// Looping into the resources element
						JSONArray resourcesArray = (JSONArray) jsonObject.get("resources");
	
						for (int i = 0; i < resourcesArray.length(); i++) {
							jsonObject = resourcesArray.getJSONObject(i);
	
							Bitmap bitmap;
	
							// Get bitmap from HerokuApp
							BitmapDownloader bitmapDownloader = new BitmapDownloader();
							bitmap = bitmapDownloader.downloadBitmap(jsonObject.getString("resourceURL"));
	
							// Get bytes from bitmap
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
							
							SentenceResource sr = new SentenceResource();
							sr.setResourceId(jsonObject.getInt("resourceId"));
							sr.setResourceURL(jsonObject.getString("resourceURL"));
							sr.setSequenceOrder(jsonObject.getInt("sequenceOrder"));
							sr.setResourceImage(stream.toByteArray());
							
							resources.add(sr);
						}
						
						sentence.setSentenceResource(resources);
						
						datasource = new SentenceDataSource(getApplication());
						datasource.createSentence(sentence);
	
						Log.i(TAG, jsonObject.toString());
					} catch (JSONException e) {
						// TODO: Candidate code to send for reporting
						Log.i(TAG, e.getMessage());
						e.printStackTrace();
					}
				}
			}

			return sentence;

		}

		@Override
		protected void onPostExecute(Sentence s) {			

			pDlg.dismiss();
			sentence = s;
			playTranslation(s);

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
	private void playTranslation(Sentence s) {
		
		if(s != null){
		
			int resourceCount = s.getSentenceResource().size();
			List<SentenceResource> sr = s.getSentenceResource();
			Collections.sort(sr);

			if (resourceCount > 0) {

				try {
					ivTranslationResultPlayButton.setVisibility(View.INVISIBLE);

					AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.tlatoa_translation_result_anim);
					animationDrawable.setOneShot(true);

					if (animationDrawable.getNumberOfFrames() == 0) {
						for (int i = 0; i < resourceCount; i++) {
							
							SentenceResource r = sr.get(i);
							
							Bitmap bitmap = BitmapFactory.decodeByteArray(r.getResourceImage(), 0, r.getResourceImage().length);
							
							BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
							Drawable drawable = (Drawable) bitmapDrawable;
							animationDrawable.addFrame(drawable, ANIMATION_DURATION);

						}
					}

					// Pass our animation drawable to our custom drawable class
					CustomAnimationDrawable cad = new CustomAnimationDrawable(animationDrawable) {
						@Override
						void onAnimationFinish() {
							ivTranslationResultPlayButton.setVisibility(View.VISIBLE);
						}
					};

					// Set the views drawable to our custom drawable
					ivTranslationResultAnimation.setImageResource(android.R.color.transparent);
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

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setBakgroundJELLYBean(CustomAnimationDrawable cad) {
		ivTranslationResultAnimation.setBackground(cad);
	}

}