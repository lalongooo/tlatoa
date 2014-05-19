package com.xihuanicode.tlatoa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.xihuanicode.tlatoa.customlistview.AboutDeveloperListAdapter;

public class AboutActivity extends Activity  implements View.OnClickListener {
	
	private Typeface typeface;
	private ListView lvAboutDevelopersList;
	private AboutDeveloperListAdapter adapter;
	
	// ActionBar item
	private ImageView actionBarBack;
	private TextView actionBarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		setUI();
	}

	private void setUI() {
		
		// Get views
		lvAboutDevelopersList = (ListView) findViewById(R.id.lvAboutDevelopersList);
		actionBarBack = (ImageView) findViewById(R.id.actionbar_back);
		actionBarTitle = (TextView) findViewById(R.id.actionbar_title);
		
		// Set typeface
		typeface = Typeface.createFromAsset(getAssets(), "DANUBE.TTF");
		actionBarTitle.setTypeface(typeface);
		
		// Set listeners
		actionBarBack.setOnClickListener(this);

		// Init adapter
		adapter = new AboutDeveloperListAdapter(getApplicationContext(), new String[]{"@krescruz","@elisasdiego","@lalongooo", "@srrobo"});		
		
		// Set adapter
		lvAboutDevelopersList.setAdapter(adapter);
		
		lvAboutDevelopersList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String twitter = ((TextView) arg1.findViewById(R.id.tvAboutDeveloperTwitterUsername)).getText().toString();
				
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("twitter://user?screen_name=" + twitter));
					startActivity(intent);

					}catch (Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitter.replace("@", "")))); 
					}
			}
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean returnValue = false;
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				
			default:
				returnValue = super.onOptionsItemSelected(item);
		}
		return returnValue;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.actionbar_back:
				actionBarBack.setSelected(true);
				goBack();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		goBack();
	}

	private void goBack(){
		Intent intent = new Intent().setClass(AboutActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
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