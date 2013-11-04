package com.xihuanicode.tlatoa.customlistview;

import java.util.ArrayList;
import java.util.HashMap;

import com.xihuanicode.tlatoa.activity.R;
import com.xihuanicode.tlatoa.activity.TranslationResultActivity;
import com.xihuanicode.tlatoa.utils.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}
	
	public LazyAdapter(Context c, Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
//        if(convertView==null){
//            vi = inflater.inflate(R.layout.list_row, null);
//        }
//
//        TextView title = (TextView)vi.findViewById(R.id.title); // title
//        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
//        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
//        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
//        
//        HashMap<String, String> song = new HashMap<String, String>();
//        song = data.get(position);
//        
//        // Setting all values in listview
//        title.setText(song.get(CustomizedListView.KEY_TITLE));
//        artist.setText(song.get(CustomizedListView.KEY_ARTIST));
//        duration.setText(song.get(CustomizedListView.KEY_DURATION));
//        imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_row_for_tlatoa, null);
		}

		TextView itemName = (TextView) vi.findViewById(R.id.playlist_item_name); // title
		TextView itemTimeAgo = (TextView) vi.findViewById(R.id.playlist_item_timeago_indicator); // artist
		TextView itemNumber = (TextView) vi.findViewById(R.id.playlist_item_number); // duration
		Button btnShare = (Button) vi.findViewById(R.id.playlist_item_share_button);

		HashMap<String, String> song = new HashMap<String, String>();
		song = data.get(position);

		// Setting all values in listview
		itemName.setText(song.get(TranslationResultActivity.KEY_TITLE));
		itemTimeAgo.setText(song.get(TranslationResultActivity.KEY_DURATION));
		itemNumber.setText(song.get(TranslationResultActivity.KEY_ID));
		btnShare.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(activity , "Buton share clicked!", Toast.LENGTH_SHORT).show();
			}
		});
		
		return vi;
		
	}
}