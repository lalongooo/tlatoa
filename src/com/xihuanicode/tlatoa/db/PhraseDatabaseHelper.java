package com.xihuanicode.tlatoa.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PhraseDatabaseHelper extends SQLiteOpenHelper {

	// Table name
	public static final String TABLE_PHRASE = "PHRASE";
	
	// Columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PHRASE = "phrase";
	public static final String COLUMN_CREATED_AT = "created_at";

	// Database filename
	private static final String DATABASE_NAME = "phrase.db";
	
	// Database version
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE =
			"CREATE TABLE " + TABLE_PHRASE + "(" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_PHRASE + " TEXT UNIQUE NOT NULL, "
			+ COLUMN_CREATED_AT + " INTEGER);";
//	+ COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

	public PhraseDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PhraseDatabaseHelper.class.getName(),
				"Upgrading database from version "
				+ oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHRASE);
		onCreate(db);
	}

}