package com.xihuanicode.tlatoa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xihuanicode.tlatoa.customlistview.AboutDeveloperListAdapter;

public class AboutActivity extends ActionBarActivity {
	
	ListView lvAboutDevelopersList;
	private AboutDeveloperListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		setUI();
	}

	private void setUI() {

		// Enable the ActionBar
		setTheme(R.style.CustomDarkTheme);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Get views
		lvAboutDevelopersList = (ListView) findViewById(R.id.lvAboutDevelopersList);

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
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitter))); 
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
	
	

}
