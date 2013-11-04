package com.xihuanicode.tlatoa.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.navdrawer.SimpleSideDrawer;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener, ISimpleDialogListener,
		ISimpleDialogCancelListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

	private ImageView ivMicrophone;
	private SimpleSideDrawer mNav;
	private ListView menuOptions;
	private String[] menuOptionsTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);

		setUI();
	}

	private void setUI() {

		setTheme(R.style.CustomDarkTheme);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Configuration for the right side menu
		mNav = new SimpleSideDrawer(this);
		mNav.setRightBehindContentView(R.layout.right_side_menu);
		menuOptions = (ListView) this.findViewById(R.id.menu_options);
		ivMicrophone = (ImageView) this.findViewById(R.id.ivMicrophone);
		menuOptionsTitles = getResources().getStringArray(
				R.array.menu_options_string_array);
		menuOptions.setAdapter(new MyAdapter<String>(this,
				android.R.layout.simple_list_item_1, menuOptionsTitles));

		// Add listeners
		ivMicrophone.setOnClickListener(this);
		menuOptions.setOnItemClickListener(new DrawerItemClickListener());

		// With this piece of code, when the Menu key is pressed, we'll show the
		// menu from the ActionBar
		try {
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(ViewConfiguration.get(this), false);
			}
		} catch (Exception ex) {
			// TODO: Candidate code to send for reporting
		}

	}

	/* The Adapter for the right side menu */
	private class MyAdapter<T> extends ArrayAdapter<String> {

		public MyAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			if (position % 2 == 1) {
				view.setBackgroundResource(R.color.tlatoa_gray_main_color);
			} else {
				view.setBackgroundResource(R.color.tlatoa_splash_background);
			}
			return view;
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Toast.makeText(getApplicationContext(),
					"Selected option is: " + menuOptionsTitles[position],
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showConfirmationMessage() {
		SimpleDialogFragment
				.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_main_confirmation_title)
				.setMessage(R.string.tlatoa_main_confirmation_exit_message)
				.setPositiveButtonText(R.string.tlatoa_main_confirmation_ok)
				.setNegativeButtonText(R.string.tlatoa_main_confirmation_cancel)
				.setRequestCode(42).setTag("custom-tag").show();
	}

	/**
	 * Helper method to show the toast message
	 **/
	void showToastMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void speak() {

		// This creates the intent that will launch the Google Popup to
		// recognize speech.
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Que comience la clase!");

		// Given an hint to the recognizer about what the user is going to say
		// There are two form of language model available
		// 1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		// 2.LANGUAGE_MODEL_FREE_FORM : If not sure about the words or phrases
		// and its domain.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Specify the languaje that we'll be using during the translation.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX");

		// OPTIONAL: Specify how many results you want to receive.
		// The results will be sorted where the first result is the one with
		// higher confidence.
		// intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);

		// Start the Voice recognizer activity for the result.
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	private void goToTranslationResultActivity(String phrase) {
		Intent intent = new Intent(this, TranslationResultActivity.class);
		intent.putExtra("phrase", phrase);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				// Bitmap imgSignal;

				if (!textMatchList.isEmpty()) {
					String sentence = textMatchList.get(0);
					Toast.makeText(getApplicationContext(), "This is the first match what you said: " + sentence, Toast.LENGTH_SHORT).show();
					goToTranslationResultActivity(sentence);
				}

				// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				showToastMessage("Audio Error");
			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage("Client Error");
			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage("Network Error");
			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage("No Match");
			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage("Server Error");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_profile:

			// TODO: This code opens the right side menu
			// mNav.toggleRightDrawer();

			Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
			intent.putExtra("calledFrom", "MainActivity");
			startActivity(intent);

			return true;

		case R.id.action_about:
			return true;
		case android.R.id.home:
			showConfirmationMessage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		showConfirmationMessage();
	}

	// ISimpleDialogCancelListener
	@Override
	public void onCancelled(int requestCode) {
		if (requestCode == 42) {
		}
	}

	// ISimpleDialogListener
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		if (requestCode == 42) {
			finish();
		}
	}

	// ISimpleDialogListener
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		if (requestCode == 42) {
		}
	}

	// View.OnClickListener
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivMicrophone:
			speak();
			break;
		default:
			break;
		}

	}

}