package com.xihuanicode.tlatoa.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PhraseDataSource {

	// Database fields
	private SQLiteDatabase database;
	private PhraseDatabaseHelper dbHelper;
	private String[] allColumns =
		{
			PhraseDatabaseHelper.COLUMN_ID,
			PhraseDatabaseHelper.COLUMN_PHRASE,
			PhraseDatabaseHelper.COLUMN_CREATED_AT
		};

	public PhraseDataSource(Context context) {
		dbHelper = new PhraseDatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Phrase createPhrase(String phrase) {		
		
		// Creating the valies for inserting
		ContentValues values = null;
		try {
			values = new ContentValues();
			values.put(PhraseDatabaseHelper.COLUMN_PHRASE, phrase);
			values.put(PhraseDatabaseHelper.COLUMN_CREATED_AT, new java.util.Date().getTime());
			
		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		
		//Perform the operation in the database
		long insertId = 0;
		try {
			
			if(values != null){
				insertId = database.insert(PhraseDatabaseHelper.TABLE_PHRASE, null, values);	
			}
			
		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		// Get the last inserted row
		Cursor cursor = null;
		try {
			
			if(insertId > 0){
				cursor = database.query(
						PhraseDatabaseHelper.TABLE_PHRASE, 					// Table
						allColumns,											// Columns
						PhraseDatabaseHelper.COLUMN_ID + " = " + insertId, 	// Where (condition)
						null,												// Group By
						null,												// Order By
						null,												// Having
						null												// Limit
						);
			}

		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		Phrase newPhrase = null;
		if(cursor != null){
			cursor.moveToFirst();
			newPhrase = cursorToPhrase(cursor);
			cursor.close();
		}		

		return newPhrase;
	}

	public void deletePhrase(Phrase phrase) {
		long id = phrase.getId();
		database.delete(PhraseDatabaseHelper.TABLE_PHRASE, PhraseDatabaseHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Phrase> getAllPhrase() {
		List<Phrase> phrases = new ArrayList<Phrase>();

		Cursor cursor = database.query(PhraseDatabaseHelper.TABLE_PHRASE, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Phrase phrase = cursorToPhrase(cursor);
			phrases.add(phrase);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return phrases;
	}

	private Phrase cursorToPhrase(Cursor cursor) {
		Phrase phrase = new Phrase();
		phrase.setId(cursor.getLong(0));
		phrase.setPhrase(cursor.getString(1));
		phrase.setCreatedAt(cursor.getLong(2));
		
		return phrase;
	}
}