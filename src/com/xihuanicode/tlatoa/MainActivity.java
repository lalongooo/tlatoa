package com.xihuanicode.tlatoa;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.navdrawer.SimpleSideDrawer;
import com.xihuanicode.tlatoa.utils.Utils;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener,
		ISimpleDialogListener,
		ISimpleDialogCancelListener {

	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private static final int NOTIFICATION_MESSAGE_REQUEST_CODE = 42;
	private static final int CONFIRMATION_MESSAGE_REQUEST_CODE = 43;

	private Typeface typeface;
	private SimpleSideDrawer mNav;
	private ImageView ivMicrophone;
	private ListView menuOptions;
	private String[] menuOptionsTitles;
	
	private LayoutInflater mInflater;
	
	// Action bar items
	private ImageView actionBarMore;
	private TextView actionBarTitle;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);

		setUI();
		
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
	}

	private void setUI() {
		
		// Get views
		actionBarMore = (ImageView) findViewById(R.id.actionbar_more);
		actionBarTitle = (TextView) findViewById(R.id.actionbar_title);
		
		// set typeface
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		actionBarTitle.setTypeface(typeface);

		// Configuration for the right side menu
		mNav = new SimpleSideDrawer(this);
		mNav.setRightBehindContentView(R.layout.right_side_menu);
		menuOptions = (ListView) this.findViewById(R.id.menu_options);
		menuOptionsTitles = getResources().getStringArray(R.array.menu_options_string_array);
		menuOptions.setAdapter(new MyAdapter<String>(this,android.R.layout.simple_list_item_1, menuOptionsTitles));
		ivMicrophone = (ImageView) this.findViewById(R.id.ivMicrophone);

		// Add listeners
		ivMicrophone.setOnClickListener(this);
		actionBarMore.setOnClickListener(this);
		menuOptions.setOnItemClickListener(new DrawerItemClickListener());

		// With this piece of code, when the Menu key is pressed, we'll show the
		// menu from the ActionBar
		try {
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(ViewConfiguration.get(this), false);
			}
		} catch (Exception ex) {
			// TODO: Candidate code to send for reporting
		}
		
		if(!Utils.isSpeechRecognitionEnabled(this)){
			this.ivMicrophone.setImageResource(R.drawable.tlatoa_microphone_disabled);
			this.ivMicrophone.setClickable(false);
			showNotificationMessage();
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
				view.setBackgroundResource(R.color.tlatoa_main_color_gray);
			} else {
				view.setBackgroundResource(R.color.tlatoa_main_color_orange);
			}
			return view;
		}
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Toast.makeText(getApplicationContext(), "Selected option is: " + menuOptionsTitles[position], Toast.LENGTH_SHORT).show();
		}
	}

	private void showNotificationMessage() {
		SimpleDialogFragment
				.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_main_speech_not_supported_title)
				.setMessage(R.string.tlatoa_main_speech_not_supported_message)
				.setPositiveButtonText(R.string.tlatoa_main_speech_not_supported_ok_option)
				.setRequestCode(NOTIFICATION_MESSAGE_REQUEST_CODE)
				.setCancelable(false)
				.setTag("custom-tag")
				.show();
	}
	
	private void showConfirmationMessage() {
		SimpleDialogFragment
				.createBuilder(this, getSupportFragmentManager())
				.setTitle(R.string.tlatoa_main_exit_confirmation_title)
				.setMessage(R.string.tlatoa_main_exit_confirmation_exit_message)
				.setPositiveButtonText(R.string.tlatoa_main_exit_confirmation_ok_option)
				.setNegativeButtonText(R.string.tlatoa_main_exit_confirmation_cancel_option)
				.setRequestCode(CONFIRMATION_MESSAGE_REQUEST_CODE)
				.setTag("custom-tag")
				.show();
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
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start translating...");

		// Given an hint to the recognizer about what the user is going to say
		// There are two form of language model available
		// 1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		// 2.LANGUAGE_MODEL_FREE_FORM : If not sure about the words or phrases
		// and its domain.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {

			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					String sentence = textMatchList.get(0);
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
		
		Intent intent;
		
		switch (item.getItemId()) {
			case R.id.action_profile:
				// TODO: This code opens the right side menu
				// mNav.toggleRightDrawer();	
				intent = new Intent(MainActivity.this, ProfileActivity.class);
				finish();
				startActivity(intent);
				
				return true;
	
			case R.id.action_about:
				intent = new Intent(MainActivity.this, AboutActivity.class);
				finish();
				startActivity(intent);
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
		if (requestCode == CONFIRMATION_MESSAGE_REQUEST_CODE) {
			finish();
		}
	}

	// ISimpleDialogListener
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		if (requestCode == NOTIFICATION_MESSAGE_REQUEST_CODE) {
		}
	}

	// View.OnClickListener
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivMicrophone:
			speak();
			break;
		case R.id.actionbar_more:
			actionBarMoreClicked();
			break;
		default:
			break;
		}
	}
	
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	actionBarMoreClicked();
    	return false;
    }
	
	private void actionBarMoreClicked(){
		actionBarMore.setSelected(true);
		
		LinearLayout moreContentView = (LinearLayout) mInflater.inflate(R.layout.action_bar_main_more_content, null);
		moreContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		
		final PopupWindow popupWindow = new PopupWindow(this);
		popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
        popupWindow.setContentView(moreContentView);
        popupWindow.showAsDropDown(actionBarMore);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
            	actionBarMore.setSelected(false);
            }
        });
        
        
        Button settingsButton = (Button) moreContentView.findViewById(R.id.main_more_content_profile);
        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
                popupWindow.dismiss();
            }
        });

        Button aboutButton = (Button) moreContentView.findViewById(R.id.main_more_content_about);
        aboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                finish();
                popupWindow.dismiss();
            }
        });
        
        popupWindow.update(moreContentView.getMeasuredWidth(), moreContentView.getMeasuredHeight());
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
	
	public void goToResults(View v) {
		Intent intent = new Intent(this, TranslationResultActivity.class);
		startActivity(intent); 
	}

}