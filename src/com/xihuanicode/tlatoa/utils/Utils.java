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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.xihuanicode.tlatoa.entity.Role;
import com.xihuanicode.tlatoa.entity.User;

public class Utils {
	
	private static final String TAG = "com.xihuanicode.tlatoa.utils.Utils";
	
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
	 * 
	 * @param code The language code. Like: en, cz, iw, ...
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
        catch(Exception ex){
        	// TODO: Candidate code to send for reporting
        }
    }
    
    

	/**
	 * Check if voice recognition is present
	 * 
	 * @param context The current context
	 */
	public static Boolean isSpeechRecognitionEnabled(Context context) {

		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		return activities.size() > 0;
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
	 * @param is The InputStream to be converted.
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
        	// TODO: Candidate code to send for reporting
        }

        // Return full string
        return total.toString();
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
        	// TODO: Candidate code to send for reporting
            Log.e(TAG, e.getLocalizedMessage(), e);
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
    
    

}
