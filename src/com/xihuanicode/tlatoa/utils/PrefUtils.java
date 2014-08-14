/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xihuanicode.tlatoa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Utilities and constants related to app preferences.
 */
public class PrefUtils  {

    /**
     * Profile picture of the user. It is obtained from the user Facebook profile.
     */
    public static final String TLATOA_USER_PROFILE_PICTURE = "tlatoa_user_profile_picture";

    /**
     * The name of the user. It is obtained from the user Facebook profile.
     */
    public static final String TLATOA_USER_NAME = "tlatoa_user_name";
    
    /**
     * The email of the user. It is obtained from the user Facebook profile.
     */
    public static final String TLATOA_USER_EMAIL= "tlatoa_user_email";
    
    public static void saveUserProfilePicture(final Context context, Bitmap bitmap){
    	
        Gson gson = new Gson();
        String bitmapString = gson.toJson(bitmap);
    	
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(TLATOA_USER_PROFILE_PICTURE, bitmapString).apply();
    }
    
    public static Bitmap getUserProfilePicture(final Context context){
    	
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sp.getString(TLATOA_USER_PROFILE_PICTURE, null);
        
        return json == null ? null : new Gson().fromJson(json, Bitmap.class);
    }
        
    public static void saveUserName(final Context context, String name){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(TLATOA_USER_NAME, name).apply();
    }
    
    public static String getUserName(final Context context){    	
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(TLATOA_USER_NAME, null);
    }
    
    public static void saveUserEmail(final Context context, String name){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(TLATOA_USER_EMAIL, name).apply();
    }
    
    public static String getUserEmail(final Context context){    	
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(TLATOA_USER_EMAIL, null);
    }
    
}
