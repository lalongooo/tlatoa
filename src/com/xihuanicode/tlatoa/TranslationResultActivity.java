package com.xihuanicode.tlatoa;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xihuanicode.tlatoa.customlistview.TranslationPlayListAdapter;
import com.xihuanicode.tlatoa.db.SentenceDataSource;
import com.xihuanicode.tlatoa.entity.Sentence;
import com.xihuanicode.tlatoa.entity.SentenceResource;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class TranslationResultActivity extends FragmentActivity implements
		View.OnClickListener, ISimpleDialogListener,
		ISimpleDialogCancelListener {

	private static final String TAG = "ResultActivity";

	private static final int INFORMATION_MESSAGE_REQUEST_CODE = 42;
	
	private long currentSentenceId;

	// Action bar items
	private TextView actionBarTitle;

	// UI items
	private ImageView ivTranslationResultAnimation, ivTranslationResultPlayButton;
	private ListView lvTranslationList;
	private TranslationPlayListAdapter adapter;
	private ProgressDialog pDlg;
	private AnimationDrawable a;
	private Typeface typeface;

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

		if (extras != null) {
			phrase = extras.getString("phrase");
		}

		if (phrase != null) {
			translate(phrase);
		}
		
		if(extras.getString("phrase_test") != null){
			translate(extras.getString("phrase_test"));
		}
		

	}

	private void setUI() {

		// TODO: Configure theme
		setTheme(R.style.CustomDarkTheme);

		// Get views
		lvTranslationList = (ListView) findViewById(R.id.list);
		ivTranslationResultAnimation = (ImageView) findViewById(R.id.ivTranslationResultAnimation);
		ivTranslationResultPlayButton = (ImageView) findViewById(R.id.ivTranslationResultPlayButton);
		actionBarTitle = (TextView) findViewById(R.id.actionbar_title);

		// set typeface
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		actionBarTitle.setTypeface(typeface);

		// Add listeners
		ivTranslationResultPlayButton.setOnClickListener(this);

		// Load all translated sentences
		List<Sentence> phrases = datasource.getAllSentences();

		// Listview configuration
		if (phrases.size() > 0) {
			adapter = new TranslationPlayListAdapter(this, phrases);
			lvTranslationList.setAdapter(adapter);
		}

		lvTranslationList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				long sentenceId = Integer.valueOf(((TextView) view.findViewById(R.id.tlatoa_phrase_id)).getText().toString());
				currentSentenceId = sentenceId;
				playTranslation(sentenceId);

			}
		});

	}


	private void translate(final String phrase) {
		
		showDialog();
		
		Response.ErrorListener el = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e(TAG, error.getMessage());
				hideDialog();
			}
		};
		
		Response.Listener<JSONArray> rl = new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				try {
					new Translate().execute((JSONObject) response.get(0));
				} catch (JSONException e) {
					// TODO: Candidate code to send for reporting
				}			
			}
		}; 
		
		JsonArrayRequest jor = new JsonArrayRequest(Config.PHRASE_TRANSLATION_URL + phrase, rl, el);
		
		Tlatoa.getInstance().addToRequestQueue(jor, "-VOLLEY-TRANSLATE-");
		
	}
	
	private class Translate extends AsyncTask<JSONObject, Integer, Long> {

		@Override
		protected Long doInBackground(JSONObject... params) {

			Sentence sentence = new Sentence();
			JSONObject jsonSentence = params[0];
			
			try {
				sentence.setText(jsonSentence.getString("sentence"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Check for sentence in the local database
			sentence = datasource.existsInLocalDb(sentence);
			
			if (sentence.getId() > 0 && sentence.getExpiresAt() > sentence.getCreatedAt()) {
				sentence = datasource.getSentenceById(sentence.getId());
			} else {
				
				try {
					
					sentence.setId(jsonSentence.getInt("sentenceId"));
					sentence.setText(jsonSentence.getString("sentence"));
					List<SentenceResource> resources = new ArrayList<SentenceResource>();

					// Looping into the resources element
					JSONArray resourcesArray = (JSONArray) jsonSentence.get("resources");

					for (int i = 0; i < resourcesArray.length(); i++) {
						jsonSentence = resourcesArray.getJSONObject(i);

						Bitmap bitmap = ImageLoader.getInstance().loadImageSync(jsonSentence.getString("resourceURL"));
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

						SentenceResource sr = new SentenceResource();
						sr.setResourceId(jsonSentence.getInt("resourceId"));
						sr.setResourceURL(jsonSentence.getString("resourceURL"));
						sr.setSequenceOrder(jsonSentence.getInt("sequenceOrder"));
						sr.setResourceImage(stream.toByteArray());

						resources.add(sr);
					}

					sentence.setSentenceResource(resources);

					datasource = new SentenceDataSource(getApplication());
					datasource.deletePhrase(sentence);
					datasource.createSentence(getApplicationContext(), sentence);

				} catch (JSONException e) {
					// TODO: Candidate code to send for reporting
					Log.i(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			return sentence.getId();

		}

		@Override
		protected void onPostExecute(Long sentenceId) {
			hideDialog();
			currentSentenceId = sentenceId;
			playTranslation(sentenceId);
		}

	}
	
	@SuppressWarnings("deprecation")
	private void playTranslation(Long sentenceId) {

		Sentence sentence = datasource.getSentenceById(sentenceId);

		if (sentence != null && sentence.getSentenceResource() != null) {

			int resourceCount = sentence.getSentenceResource().size();
			List<SentenceResource> sr = sentence.getSentenceResource();
			Collections.sort(sr);

			if (resourceCount > 0) {

				try {
					
					ivTranslationResultPlayButton.setVisibility(View.INVISIBLE);
					ivTranslationResultAnimation.setImageResource(android.R.color.transparent);
					
					a = new AnimationDrawable();
					for (int i = 0; i < resourceCount; i++) {
						Bitmap bitmap = BitmapFactory.decodeByteArray(sr.get(i).getResourceImage(), 0, sr.get(i).getResourceImage().length);
						a.addFrame(new BitmapDrawable(getResources(), bitmap),Config.ANIMATION_DURATION);
					}
					ivTranslationResultAnimation.setBackgroundDrawable(a);

					a.setOneShot(true);
					a.start();
					checkIfAnimationDone(a);

				} catch (NotFoundException e) {
					// TODO: Candidate code to send for reporting
					Log.i(TAG, e.getMessage());
					e.printStackTrace();
				}

			}

		} else {

			Toast.makeText(getApplicationContext(), "No results returned", Toast.LENGTH_LONG).show();
		}

	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setBakgroundJELLYBean(AnimationDrawable cad) {
		ivTranslationResultAnimation.setBackground(cad);
	}
	
	private void checkIfAnimationDone(AnimationDrawable anim){
	    final AnimationDrawable a = anim;
	    int timeBetweenChecks = 300;
	    Handler h = new Handler();
	    h.postDelayed(new Runnable(){
	        public void run(){
	            if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)){
	                checkIfAnimationDone(a);
	            } else{
	            	if(!ivTranslationResultPlayButton.isShown()){
	            		ivTranslationResultPlayButton.setVisibility(View.VISIBLE);
	            	}
	            }
	        }
	    }, timeBetweenChecks);
	}
	
	private void showNotificationMessage() {
		SimpleDialogFragment
				.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_translation_result_msg_no_result_title)
				.setMessage(R.string.tlatoa_translation_result_msg_no_result_message)
				.setPositiveButtonText(R.string.tlatoa_translation_result_msg_no_result_positive_button)
				.setRequestCode(INFORMATION_MESSAGE_REQUEST_CODE)
				.setCancelable(false).setTag("custom-tag").show();
	}

	private void showDialog() {
		pDlg = ProgressDialog.show(this, getString(R.string.tlatoa_translation_result_progess_dialog_title), getString(R.string.tlatoa_translation_result_progess_dialog_message), true);
	}

	private void hideDialog() {
		pDlg.dismiss();
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

	// ISimpleDialogCancelListener
	@Override
	public void onCancelled(int requestCode) {
		if (requestCode == INFORMATION_MESSAGE_REQUEST_CODE) {
		}
	}

	// ISimpleDialogListener
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == INFORMATION_MESSAGE_REQUEST_CODE) {
			finish();
		}
	}

	// ISimpleDialogListener
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		if (requestCode == INFORMATION_MESSAGE_REQUEST_CODE) {
		}
	}

	@Override
	public void onNeutralButtonClicked(int requestCode) {
		if (requestCode == INFORMATION_MESSAGE_REQUEST_CODE) {
		}
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
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		View empty = findViewById(R.id.empty);
		ListView list = (ListView) findViewById(R.id.list);
		list.setEmptyView(empty);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTranslationResultPlayButton:
			playTranslation(currentSentenceId);
			break;
		}
	}

}