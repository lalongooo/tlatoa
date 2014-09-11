package com.xihuanicode.tlatoa.viewpager;

import android.annotation.SuppressLint;
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
	
	private Context context;
	
	private int[] imagesArray = new int[]
	        {
				R.drawable.test_app_overview_image1,
				R.drawable.app_overview_img_1
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

	@SuppressLint("InflateParams")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vi = inflater.inflate(R.layout.viewpager_layout, null);
		
		ImageView imageView = (ImageView) vi.findViewById(R.id.ivViewPagerImageView);
		imageView.setImageResource(imagesArray[position]);
		
		TextView tv = (TextView) vi.findViewById(R.id.tvViewPagerTextView);
		switch (position) {
		case 0:
			tv.setText(context.getString(R.string.tlatoa_appoverview_1st_viewpager_item_text));
			break;
		case 1:
			tv.setText(context.getString(R.string.tlatoa_appoverview_2nd_viewpager_item_text));
			break;
		case 2:
			tv.setText(context.getString(R.string.tlatoa_appoverview_3rd_viewpager_item_text));
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