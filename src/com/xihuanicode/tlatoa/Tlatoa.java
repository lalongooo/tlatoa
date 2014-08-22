package com.xihuanicode.tlatoa;

import android.app.Application;
import android.text.TextUtils;

import com.facebook.SessionDefaultAudience;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.Tracker;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.utils.Logger;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xihuanicode.tlatoa.utils.LruBitmapCache;

public class Tlatoa extends Application {

	/*
	 * Google Analytics configuration values.
	 */
	private static GoogleAnalytics mGa;
	private static Tracker mTracker;

	// Placeholder property ID.
	private static final String GA_PROPERTY_ID = "UA-45551921-1";

	// Prevent hits from being sent to reports, i.e. during testing.
	private static final boolean GA_IS_DRY_RUN = false;

	// GA Logger verbosity.
	private static final LogLevel GA_LOG_VERBOSITY = LogLevel.INFO;

	/*
	 * Facebook configuration values.
	 */
	private static final String APP_ID = "143269149215368";
	private static final String APP_NAMESPACE = "com.xihuanicode.tlatoa.activity";

	/*
	 * Volley Library variables
	 * */
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static Tlatoa mInstance;
	

	
	public static final String TAG = Tlatoa.class.getSimpleName();
	
	protected com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
	
	@Override
	public void onCreate() {
		super.onCreate();

		initializeFb();
		initializeGa();
		
		mInstance = this;
		ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

	}
	
	public static synchronized Tlatoa getInstance(){
		return mInstance;
	}

	/*
	 * Method to handle the basic facebook configuration. Permissions, appId and
	 * default audience.
	 */
	private void initializeFb() {

		// Set log to true
		Logger.DEBUG_WITH_STACKTRACE = true;

		// Initialize facebook configuration
		Permission[] permissions = new Permission[] {
				Permission.PUBLIC_PROFILE,
				Permission.PUBLISH_ACTION,
				Permission.EMAIL,
				Permission.PUBLIC_PROFILE };

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(APP_ID)
				.setNamespace(APP_NAMESPACE)
				.setPermissions(permissions)
				.setDefaultAudience(SessionDefaultAudience.FRIENDS)
				.setAskForAllPermissionsAtOnce(true)
				.build();

		SimpleFacebook.setConfiguration(configuration);
	}

	/*
	 * Method to handle basic Google Analytics initialization. This call will
	 * not block as all Google Analytics work occurs off the main thread.
	 */
	private void initializeGa() {

		mGa = GoogleAnalytics.getInstance(this);
		mTracker = mGa.getTracker(GA_PROPERTY_ID);

		// Set dryRun flag.
		mGa.setDryRun(GA_IS_DRY_RUN);

		// Set Logger verbosity.
		mGa.getLogger().setLogLevel(GA_LOG_VERBOSITY);
	}

	/*
	 * Returns the Google Analytics tracker.
	 */
	public static Tracker getGaTracker() {
		return mTracker;
	}

	/*
	 * Returns the Google Analytics instance.
	 */
	public static GoogleAnalytics getGaInstance() {
		return mGa;
	}

	public RequestQueue getRequestQueue(){
		if(mRequestQueue == null){
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());			
		}
		return mRequestQueue;
	}
	
	public ImageLoader getImageLoader() {
		getRequestQueue();
		if(mImageLoader == null){
			mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache());
		}
		return this.mImageLoader;
	}
	
	public <T> void addToRequestQueue(Request <T> req, String tag){
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}
	
	public <T> void addToRequestQueue(Request <T> req){
		req.setTag(TAG);
		getRequestQueue().add(req);
	}	
	
	public void cancelPendingRequest(Object tag){
		if(mRequestQueue != null){
			mRequestQueue.cancelAll(tag);
		}
	}
	
}