package com.xihuanicode.tlatoa.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TlatoaDatabaseHelper extends SQLiteOpenHelper {

	// Database filename
	private static final String DATABASE_NAME = "sentence";
	
	// Database version
	private static final int DATABASE_VERSION = 1;

    // Tables
	public static final String TABLE_SENTENCE = "SENTENCE";
	public static final String TABLE_SENTENCE_RESOURCES = "SENTENCE_RESOURCES";
	public static final String TABLE_USER = "USER";
	
    // Columns of table: SENTENCE
	public static final String SENTENCE_ID = "_id";
	public static final String SENTENCE_TEXT = "text";
	public static final String SENTENCE_CREATED_AT = "created_at";
	public static final String SENTENCE_EXPIRES_AT = "expires_at";
	
	// Columns of table: SENTENCE_RESOURCES
	public static final String RESOURCES_SENTENCE_ID = "sentence_id";
	public static final String RESOURCES_RESOURCE_ID = "resource_id";
	public static final String RESOURCES_RESOURCE_URL = "resource_url";
	public static final String RESOURCES_SEQUENCE_ORDER = "sequence_order";
	public static final String RESOURCES_SENTENCE_IMAGE = "sentence_image";
	
	// Columns of table: SENTENCE_RESOURCES
	public static final String USER_ID =  "_id";
	public static final String USER_NAME =  "name";
	public static final String USER_FIRSTNAME =  "firstName";
	public static final String USER_LASTNAME =  "lastName";
	public static final String USER_MIDDLENAME =  "middleName";
	public static final String USER_SOCIALMEDIAID =  "socialMediaId";
	public static final String USER_GENDER =  "gender";
	public static final String USER_LOCATIONID =  "locationId";
	public static final String USER_LOCATIONNAME =  "locationName";
	public static final String USER_EMAIL =  "email";
	public static final String USER_PROFILEPICTUREURL =  "profilePictureUrl";
	public static final String USER_ROLES =  "roles";
	/* Additional column to store the bitmap of the profile picture */
	public static final String USER_PICTURE =  "picture";

	
	// CREATE TABLES SQL STATEMENTS
	private static final String CREATE_TABLE_SENTENCE =
			"CREATE TABLE " + TABLE_SENTENCE + "(" 
			+ SENTENCE_ID + " 			INTEGER  NOT NULL, "
			+ SENTENCE_TEXT + "    		TEXT     NOT NULL, "
			+ SENTENCE_CREATED_AT + "  	INTEGER  NOT NULL,"
			+ SENTENCE_EXPIRES_AT + "  	INTEGER  NOT NULL,"
			+ "PRIMARY KEY ( " + SENTENCE_ID + "  ));";
	
	private static final String CREATE_TABLE_SENTENCE_RESOURCES =
		"CREATE TABLE " + TABLE_SENTENCE_RESOURCES + "("
		+ RESOURCES_SENTENCE_ID + "    INTEGER  NOT NULL, "
		+ RESOURCES_RESOURCE_ID + "    INTEGER  NOT NULL, "
		+ RESOURCES_RESOURCE_URL + "   TEXT     NOT NULL, "
		+ RESOURCES_SEQUENCE_ORDER + " INTEGER  NOT NULL, "
		+ RESOURCES_SENTENCE_IMAGE + " BLOB  NOT NULL, "
		+ "FOREIGN KEY ( " + RESOURCES_SENTENCE_ID + "  ) REFERENCES " + TABLE_SENTENCE + " ("+ SENTENCE_ID +")" + ");";

	private static final String CREATE_TABLE_USER =
			"CREATE TABLE " + TABLE_USER + "("
			+ USER_ID  + "  				INTEGER NOT NULL, "
			+ USER_NAME  + "  				TEXT NULL, "
			+ USER_FIRSTNAME  + "  			TEXT NULL, "
			+ USER_LASTNAME  + "  			TEXT NULL, "
			+ USER_MIDDLENAME  + "  		TEXT NULL, "
			+ USER_SOCIALMEDIAID  + "  		TEXT NULL, "
			+ USER_GENDER  + "  			TEXT NULL, "
			+ USER_LOCATIONID  + "  		TEXT NULL, "
			+ USER_LOCATIONNAME  + "  		TEXT NULL, "
			+ USER_EMAIL  + "  				TEXT NULL, "
			+ USER_PROFILEPICTUREURL  + "  	TEXT NULL, "
			+ USER_ROLES  + "  				TEXT NULL, "
			+ USER_PICTURE + " 				BLOB NULL, "
			+ "PRIMARY KEY ( " + USER_ID + "  ));";
	
	public TlatoaDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_SENTENCE);
		database.execSQL(CREATE_TABLE_SENTENCE_RESOURCES);
		database.execSQL(CREATE_TABLE_USER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TlatoaDatabaseHelper.class.getName(),
				"Upgrading database from version "
				+ oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENTENCE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENTENCE_RESOURCES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		onCreate(db);
	}

}