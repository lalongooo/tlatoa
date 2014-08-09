package com.xihuanicode.tlatoa;

import android.app.Application;

import com.facebook.SessionDefaultAudience;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.Tracker;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.utils.Logger;

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

	@Override
	public void onCreate() {
		super.onCreate();

		initializeFb();
		initializeGa();

	}

	/*
	 * Method to handle the basic facebook configuration. Permissions, appId and
	 * default audience.
	 */
	private void initializeFb() {

		// Set log to true
		Logger.DEBUG_WITH_STACKTRACE = true;

		// Initialize facebook configuration
		Permission[] permissions = new Permission[] { Permission.PUBLIC_PROFILE,
				Permission.PUBLISH_ACTION,
				Permission.EMAIL };

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(APP_ID).setNamespace(APP_NAMESPACE)
				.setPermissions(permissions)
				.setDefaultAudience(SessionDefaultAudience.FRIENDS).build();

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

}