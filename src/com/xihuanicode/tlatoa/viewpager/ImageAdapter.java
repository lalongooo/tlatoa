package com.xihuanicode.tlatoa.viewpager;

import com.xihuanicode.tlatoa.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapter extends PagerAdapter {
	Context context;
	private int[] imagesArray = new int[]
	        {
				R.drawable.test_app_overview_image2,
				R.drawable.test_app_overview_image1,
			};

	public ImageAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return imagesArray.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);

		int padding = context.getResources().getDimensionPixelSize(R.dimen.default_title_indicator_footer_padding);
		imageView.setPadding(padding, padding, padding, padding);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setImageResource(imagesArray[position]);

		((ViewPager) container).addView(imageView, 0);

		return imageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
}