package com.xihuanicode.tlatoa.customlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xihuanicode.tlatoa.R;

public class AboutDeveloperListAdapter extends BaseAdapter {

	private String[] data;
	private LayoutInflater layoutInflater;

	public AboutDeveloperListAdapter(Context context, String[] data) {
		this.data = data;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		View vi = convertView;

		if (vi == null) {
			vi = layoutInflater.inflate(R.layout.tlatoa_about_developers_list_row, null);
		}

		TextView tvAboutDeveloperTwitterUsername = (TextView) vi.findViewById(R.id.tvAboutDeveloperTwitterUsername);
		tvAboutDeveloperTwitterUsername.setText(data[position]);

		return vi;
	}

}
