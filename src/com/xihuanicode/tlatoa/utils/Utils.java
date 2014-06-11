package com.xihuanicode.tlatoa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;
import com.google.gson.Gson;
import com.xihuanicode.tlatoa.Tlatoa;
import com.xihuanicode.tlatoa.entity.Role;
import com.xihuanicode.tlatoa.entity.User;
import com.xihuanicode.tlatoa.enums.GeneralizedScreenSize;

public class Utils {
	
	/**
	 * Take screenshot of the activity including the action bar
	 * 
	 * @param activity
	 * @return The screenshot of the activity including the action bar
	 */
	public static Bitmap takeScreenshot(Activity activity)
	{
		ViewGroup decor = (ViewGroup)activity.getWindow().getDecorView();
		ViewGroup decorChild = (ViewGroup)decor.getChildAt(0);
		decorChild.setDrawingCacheEnabled(true);
		decorChild.buildDrawingCache();
		Bitmap drawingCache = decorChild.getDrawingCache(true);
		Bitmap bitmap = Bitmap.createBitmap(drawingCache);
		decorChild.setDrawingCacheEnabled(false);
		return bitmap;
	}

	/**
	 * Print hash key
	 * 
	 * @param context The current {@link android.content.Context}
	 * @return void
	 */
	public static void printHashKey(Context context)
	{
		try
		{
			String TAG = "com.xihuanicode.tlatoa.activity";
			PackageInfo info = context.getPackageManager().getPackageInfo(TAG,PackageManager.GET_SIGNATURES);
			for (Signature signature: info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				Log.d(TAG, "keyHash: " + keyHash);
			}
		}
		catch (NameNotFoundException e)
		{

		}
		catch (NoSuchAlgorithmException e)
		{

		}
	}

	/**
	 * Update language
	 * @param context The current {@link android.content.Context}
	 * @param code The language code. Like: en, cz, iw, ...
	 * @return void
	 */
	public static void updateLanguage(Context context, String code)
	{
		Locale locale = new Locale(code);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	}
	
	
	/**
	 * Copy stream
	 * 
	 * @param is The InputStream from which wi'll get data
	 * @param os The OutputStream where we'll copy the data
	 * @return void
	 */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception e){
        	exceptionToGa(null, e, false);
        }
    }
    
    

	/**
	 * 
	 * Check if voice recognition is present
	 * 
	 * @param context The current {@link android.content.Context}
	 * @return True if the speech recognition is enabled. Otherwise, false.
	 */
	public static Boolean isSpeechRecognitionEnabled(Context context) {

		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> intent = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		return intent.size() > 0;
	}
	
	/**
	 * Check if the device is connected to the internet
	 * 
	 * @param context The current context
	 */
	public static Boolean isNetworkAvailable(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		return  (networkInfo != null && networkInfo.isConnected());
	}
	
	
	
	/**
	 * Converts the inputstream to a string value
	 * 
	 * @param is The {@link java.io.InputStream} to be converted.
	 * @return The String contained in the {@link java.io.InputStream} object 
	 */	
    public static String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
        	exceptionToGa(null, e, false);
        }

        // Return full string
        return total.toString();
    }

	/**
	 * Returns a String resource identified by the id parameter
	 * 
	 * @param c The current {@link android.content.Context}
	 * @param id The resource id
	 * @return The String resource 
	 */	
    public static String getStringResource(Context c, int id){
    	return c.getResources().getString(id);
    }
    
    
    public static HttpResponse doResponse(String url, int taskType) {

    	HttpClient httpclient = new DefaultHttpClient(getHttpParams()); 
        HttpResponse response = null;

        try {
            switch (taskType) {

            case 1:
        		Role [] roles = new Role[] { new Role("49", "TestRole" ) };
        		User user = new User("fbName", "fbFirstName", "fbLastName", "fbMiddleName", "fbUserId", "fbGender", "fbLocationId", "fbLocale", "fbEmail", "fbProfilePictureUrl", roles);
                String jsonEn = new Gson().toJson(user);

                HttpPost httppost = new HttpPost(url);
                httppost.setHeader("Content-Type", "application/json");
                httppost.setEntity(new ByteArrayEntity(jsonEn.toString().getBytes("UTF8")));

                response = httpclient.execute(httppost);
                break;
            case 2:
                HttpGet httpget = new HttpGet(url);
                response = httpclient.execute(httpget);
                break;
            }
        } catch (Exception e) {
        	exceptionToGa(null, e, false);
        }

        return response;
    }
    
    private static final int CONN_TIMEOUT = 10000;    
    private static final int SOCKET_TIMEOUT = 10000;
    
    
    // Establish connection and socket (data retrieval) timeouts
    private static HttpParams getHttpParams() {
         
        HttpParams htpp = new BasicHttpParams();
         
        HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
        HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);
         
        return htpp;
    }
    
	public static HttpResponse registerUser(String url, User user) {

		HttpClient httpclient = new DefaultHttpClient(getHttpParams());
		HttpResponse response = null;

		try {

			Role[] roles = new Role[] { new Role("49", "TestRole") };
			user.setRoles(roles);
			String jsonEn = new Gson().toJson(user);

			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Content-Type", "application/json");
			httppost.setEntity(new ByteArrayEntity(jsonEn.toString().getBytes(
					"UTF8")));

			response = httpclient.execute(httppost);

		} catch (Exception e) {
			exceptionToGa(null, e, false);
		}

		return response;

	}
    

	/**
	 * Sends the exception to Google Analytics
	 * 
	 * @param c The current {@link android.content.Context}
	 * @param e The thrown exception object to be sent to GA. 
	 * @param isFatal Indicates if the e object is a fatal exception.
	 */	
    public static void exceptionToGa(Context c, Exception e, boolean isFatal){
    	
  	  Tracker tracker = Tlatoa.getGaTracker();
  	  tracker.send(MapBuilder
  	      .createException(new StandardExceptionParser(c, null) 	// Context and optional collection of package names to be used in reporting the exception.
  	      .getDescription(Thread.currentThread().getName(),    		// The name of the thread on which the exception occurred.
  	                                       e),                 		// The exception.
  	                                       	isFatal)             	// False indicates a fatal exception
  	      .build()
  	  );
    }
    
    
	/**
	 * Determines the generalized screen size of the device.
	 * 
	 * @param c The current {@link android.content.Context}
	 * @return A {@link com.xihuanicode.tlatoa.enums.GeneralizedScreenSize} object with the generalized screen representation.
	 */
	public static GeneralizedScreenSize getDeviceGeneralizedScreenSize(Context c) {

		if ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			return GeneralizedScreenSize.SCREENLAYOUT_SIZE_SMALL;
		} else if ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			return GeneralizedScreenSize.SCREENLAYOUT_SIZE_NORMAL;
		} else if ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			return GeneralizedScreenSize.SCREENLAYOUT_SIZE_LARGE;
		} else if ((c.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			return GeneralizedScreenSize.SCREENLAYOUT_SIZE_XLARGE;
		} else{
			return GeneralizedScreenSize.SCREENLAYOUT_SIZE_UNDEFINED;
		}
	}

	
	/**
	 * Returns the property identified by name. There must be placed an "app.properties" file in the assets folder.
	 * 
	 * @param c The current android application {@link android.content.Context}
	 * @param name The name of the property
	 * @return The property value if it exists, null otherwise.
	 * 
	 * @see java.util.Properties
	 */	
	public static String getApplicationProperty(Context c, String name){

		String property= null;
		Resources resources = c.getResources();
		AssetManager assetManager = resources.getAssets();

		// Read from the /assets directory
		try {
			
		    InputStream inputStream = assetManager.open("app.properties");
		    Properties properties = new Properties();
		    properties.load(inputStream);
		    
		    property = properties.getProperty(name);
		    
		} catch (IOException e) {
			exceptionToGa(c, e, false);

		}
		
		return property;
	}

}
