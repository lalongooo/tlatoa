package com.xihuanicode.tlatoa.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xihuanicode.tlatoa.R;

public class ComplexAdapter extends PagerAdapter {
	Context context;
	private int[] imagesArray = new int[]
	        {
				R.drawable.test_app_overview_image1,
				R.drawable.test_app_overview_image2,
				R.drawable.test_app_overview_image3
			};

	public ComplexAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return imagesArray.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.viewpager_layout, null);
		
		ImageView imageView = (ImageView) vi.findViewById(R.id.ivViewPagerImageView);
		imageView.setImageResource(imagesArray[position]);
		
		TextView tv = (TextView) vi.findViewById(R.id.tvViewPagerTextView);
		switch (position) {
		case 0:
			tv.setText("Identify the Tlatoa icon in your smartphone apps list and launch!");
			break;
		case 1:
			tv.setText("Press the microphone icon and start talking...");
			break;
		case 2:
			tv.setText("Receive the translation immediately into signal language");
			break;
		}
		
		((ViewPager) container).addView(vi, 0);

		return vi;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
}