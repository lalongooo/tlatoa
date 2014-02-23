package com.xihuanicode.tlatoa.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SentenceDatabaseHelper extends SQLiteOpenHelper {

	// Database filename
	private static final String DATABASE_NAME = "sentence";
	
	// Database version
	private static final int DATABASE_VERSION = 1;

    // Tables name
	public static final String TABLE_SENTENCE = "SENTENCE";
	public static final String TABLE_SENTENCE_RESOURCES = "SENTENCE_RESOURCES";
	
    // Columns of table: SENTENCE
	public static final String SENTENCE_ID = "_id";
	public static final String SENTENCE = "sentence";
	public static final String SENTENCE_CREATED_AT = "created_at";
	
	// Columns of table: SENTENCE_RESOURCES
	public static final String RESOURCES_SENTENCE_ID = "sentence_id";
	public static final String RESOURCES_RESOURCE_ID = "resource_id";
	public static final String RESOURCES_RESOURCE_URL = "resource_url";
	public static final String RESOURCES_SEQUENCE_ORDER = "sequence_order";
	public static final String RESOURCES_SENTENCE_IMAGE = "sentence_image";
	
	// CREATE TABLES SQL STATEMENT
	private static final String CREATE_TABLE_SENTENCE =
			"CREATE TABLE " + TABLE_SENTENCE + "(" 
			+ SENTENCE_ID + " 			INTEGER  NOT NULL, "
			+ SENTENCE + "    			TEXT     NOT NULL, "
			+ SENTENCE_CREATED_AT + "  	INTEGER  NOT NULL,"
			+ "PRIMARY KEY ( " + SENTENCE_ID + "  ));";
	
	private static final String CREATE_TABLE_SENTENCE_RESOURCES =
		"CREATE TABLE " + TABLE_SENTENCE_RESOURCES + "("
		+ RESOURCES_SENTENCE_ID + "    INTEGER  NOT NULL, "
		+ RESOURCES_RESOURCE_ID + "    INTEGER  NOT NULL, "
		+ RESOURCES_RESOURCE_URL + "   TEXT     NOT NULL, "
		+ RESOURCES_SEQUENCE_ORDER + " INTEGER  NOT NULL, "
		+ RESOURCES_SENTENCE_IMAGE + " BLOB  NOT NULL, "
		+ "FOREIGN KEY ( " + RESOURCES_SENTENCE_ID + "  ) REFERENCES " + TABLE_SENTENCE + " ("+ SENTENCE_ID +")" + ");";

	public SentenceDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_SENTENCE);
		database.execSQL(CREATE_TABLE_SENTENCE_RESOURCES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SentenceDatabaseHelper.class.getName(),
				"Upgrading database from version "
				+ oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENTENCE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_SENTENCE_RESOURCES);
		onCreate(db);
	}

}