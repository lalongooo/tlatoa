package com.xihuanicode.tlatoa.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.xihuanicode.tlatoa.entity.User;
import com.xihuanicode.tlatoa.entity.Role;;

public class UserDataSource {

	// Database fields
	private SQLiteDatabase database;
	private TlatoaDatabaseHelper dbHelper;
	
	public static final String[] TABLE_USER_COLUMNS =
	{
		TlatoaDatabaseHelper.USER_ID,
		TlatoaDatabaseHelper.USER_NAME,
		TlatoaDatabaseHelper.USER_FIRSTNAME,
		TlatoaDatabaseHelper.USER_LASTNAME,
		TlatoaDatabaseHelper.USER_MIDDLENAME,
		TlatoaDatabaseHelper.USER_SOCIALMEDIAID,
		TlatoaDatabaseHelper.USER_GENDER,
		TlatoaDatabaseHelper.USER_LOCATIONID,
		TlatoaDatabaseHelper.USER_LOCATIONNAME,
		TlatoaDatabaseHelper.USER_EMAIL,
		TlatoaDatabaseHelper.USER_PROFILEPICTUREURL,
		TlatoaDatabaseHelper.USER_ROLES,
		TlatoaDatabaseHelper.USER_PICTURE
	};

	public UserDataSource(Context context) {
		dbHelper = new TlatoaDatabaseHelper(context);
	}

	public void open(){
		try {
			database = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			dbHelper.close();
		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
	}

	public long createUser(Context c, User user, byte[] userPicture) {
		
		long insertId = -1;

		open();
		
		try {
			
			ContentValues values = new ContentValues();
			values.put(TlatoaDatabaseHelper.USER_ID, user.getId());
			values.put(TlatoaDatabaseHelper.USER_NAME, user.getName());
			values.put(TlatoaDatabaseHelper.USER_FIRSTNAME, user.getFirstName());
			values.put(TlatoaDatabaseHelper.USER_LASTNAME, user.getLastName());
			values.put(TlatoaDatabaseHelper.USER_MIDDLENAME,user.getMiddleName());
			values.put(TlatoaDatabaseHelper.USER_SOCIALMEDIAID,user.getSocialMediaId());
			values.put(TlatoaDatabaseHelper.USER_GENDER, user.getGender());
			values.put(TlatoaDatabaseHelper.USER_LOCATIONID,user.getLocationId());
			values.put(TlatoaDatabaseHelper.USER_LOCATIONNAME,user.getLocationName());
			values.put(TlatoaDatabaseHelper.USER_EMAIL, user.getEmail());
			values.put(TlatoaDatabaseHelper.USER_PROFILEPICTUREURL,user.getProfilePictureUrl());
			values.put(TlatoaDatabaseHelper.USER_ROLES, new Gson().toJson(user.getRoles()));
			values.put(TlatoaDatabaseHelper.USER_PICTURE, userPicture);
						
			insertId = database.insert(TlatoaDatabaseHelper.TABLE_USER, null, values);

		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		close();

		return insertId;
	}
	
	public void deleteUser(User user) throws IllegalArgumentException{
		open();
		database.delete(TlatoaDatabaseHelper.TABLE_USER, TlatoaDatabaseHelper.USER_ID + " = " + user.getId(), null);
		close();
	}	

	public User getUserById() throws IllegalArgumentException {
		
		User u = new User();
		open();
		
		// Get the Sentence from local database
		Cursor cursor = database.query
		(
				TlatoaDatabaseHelper.TABLE_USER, 									// Table
				TABLE_USER_COLUMNS, 												// Columns
				null,	    														// Where (condition)
				null,																// Group By
				null,																// Order By
				null,																// Having
				null																// Limit
		);
		
		
		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				u = cursorToUser(cursor);
				break;
			}
		}	
		 
		close();
		
		return u;
	}
	
	private User cursorToUser(Cursor c){
		
		User u = new User();
		
		u.setId(c.getLong(0));
		u.setName(c.getString(0));
		u.setFirstName(c.getString(0));
		u.setLastName(c.getString(0));
		u.setMiddleName(c.getString(0));
		u.setSocialMediaId(c.getString(0));
		u.setGender(c.getString(0));
		u.setLocationId(c.getString(0));
		u.setLocationName(c.getString(0));
		u.setEmail(c.getString(0));
		u.setProfilePictureUrl(c.getString(0));
		u.setRoles(new Gson().fromJson(c.getString(0), Role[].class));
		
		return u;
	}
	
}