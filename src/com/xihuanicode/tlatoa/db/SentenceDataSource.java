package com.xihuanicode.tlatoa.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xihuanicode.tlatoa.entity.Sentence;
import com.xihuanicode.tlatoa.entity.SentenceResource;
import com.xihuanicode.tlatoa.utils.*;

public class SentenceDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SentenceDatabaseHelper dbHelper;
	
	private static final String[] TABLE_SENTENCE_COLUMNS =
	{
		SentenceDatabaseHelper.SENTENCE_ID,
		SentenceDatabaseHelper.SENTENCE_TEXT,
		SentenceDatabaseHelper.SENTENCE_CREATED_AT,
		SentenceDatabaseHelper.SENTENCE_EXPIRES_AT
	};
	
	
	public static final String[] TABLE_SENTENCE_RESOURCE_COLUMNS =
	{
		SentenceDatabaseHelper.RESOURCES_SENTENCE_ID,
		SentenceDatabaseHelper.RESOURCES_RESOURCE_ID,
		SentenceDatabaseHelper.RESOURCES_RESOURCE_URL,
		SentenceDatabaseHelper.RESOURCES_SEQUENCE_ORDER,
		SentenceDatabaseHelper.RESOURCES_SENTENCE_IMAGE
	};

	public SentenceDataSource(Context context) {
		dbHelper = new SentenceDatabaseHelper(context);
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

	
	/**
	 * <p>
	 * Creates a new Sentence record in the local SQLite Database 
	 * <p>
	 * @param  c  The current application context
	 * @param  s  The new {@link com.xihuanicode.tlatoa.entity.Sentence} object
	 * @return The SentenceId if the creation was success, -1 otherwise.  
	 * @see    {@link com.xihuanicode.tlatoa.entity.Sentence}
	 */
	public long createSentence(Context c, Sentence s) {
		
		// Content values for header (Sentence)
		ContentValues values = null;
		long insertId = -1;
		// Perform the operation in the database
		open();
		
		try {

			values = new ContentValues();
			values.put(SentenceDatabaseHelper.SENTENCE_ID, s.getId());
			values.put(SentenceDatabaseHelper.SENTENCE_TEXT, s.getText());
			values.put(SentenceDatabaseHelper.SENTENCE_CREATED_AT,new java.util.Date().getTime());
			values.put(SentenceDatabaseHelper.SENTENCE_EXPIRES_AT,new java.util.Date().getTime() + Long.parseLong(Utils.getApplicationProperty(c, "cache_expiration_valid_time")));
			
			
			insertId = database.insert(SentenceDatabaseHelper.TABLE_SENTENCE, null, values);

		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		try{
			if(insertId > 0){
				
				for (SentenceResource sr : s.getSentenceResource()){
					values = new ContentValues();
					values.put(SentenceDatabaseHelper.RESOURCES_SENTENCE_ID, s.getId());
					values.put(SentenceDatabaseHelper.RESOURCES_RESOURCE_ID, sr.getResourceId());
					values.put(SentenceDatabaseHelper.RESOURCES_RESOURCE_URL, sr.getResourceURL());
					values.put(SentenceDatabaseHelper.RESOURCES_SEQUENCE_ORDER, sr.getSequenceOrder());
					values.put(SentenceDatabaseHelper.RESOURCES_SENTENCE_IMAGE, sr.getResourceImage());
					
					database.insert(SentenceDatabaseHelper.TABLE_SENTENCE_RESOURCES, null, values);
				}				

			}
		}catch(Exception ex){
			// TODO: Candidate code to send for reporting
			ex.printStackTrace();
		}
		
		// Get the last inserted row
//		Cursor cursor = null;
//		try {
//			
//			if(insertId > 0){
//				
//				cursor = database.query(
//						SentenceDatabaseHelper.TABLE_SENTENCE, 				// Table
//						TABLE_SENTENCE_COLUMNS, 							// Columns
//						SentenceDatabaseHelper.SENTENCE_ID + " = " + s.getSentenceId(), 	// Where (condition)
//						null,												// Group By
//						null,												// Order By
//						null,												// Having
//						null												// Limit
//						);
//			}
//
//		} catch (Exception e) {
//			// TODO: Candidate code to send for reporting
//			e.printStackTrace();
//		}
		
		// Close database
		close();

		return insertId;
	}

	/**
	 * <p>
	 * Deletes a Sentence record in the local SQLite Database. Only the sentenceId must be provided. 
	 * <p>
	 * @param  s  The {@link com.xihuanicode.tlatoa.entity.Sentence} object to be deleted.
	 * @return The SentenceId if the creation was success, -1 otherwise.
	 * @throws IllegalArgumentException If the {@link com.xihuanicode.tlatoa.entity.Sentence} has not a valid ID property.  
	 * @see    {@link com.xihuanicode.tlatoa.entity.Sentence}
	 */
	
	public void deletePhrase(Sentence s) throws IllegalArgumentException{
		
		long id = s.getId();
		
		if(id < 1){
			throw new IllegalArgumentException("The sentence object has to have an ID.");
		}		
		
		open();
		database.delete(SentenceDatabaseHelper.TABLE_SENTENCE, SentenceDatabaseHelper.SENTENCE_ID + " = " + id, null);
		close();
	}


	/**
	 * <p>
	 * Returns all the sentences stored in the local database. 
	 * <p>
	 * @return A list of {@link com.xihuanicode.tlatoa.entity.Sentence} objects.  
	 * @see    {@link com.xihuanicode.tlatoa.entity.Sentence}
	 */
	public List<Sentence> getAllSentences() {
		
		List<Sentence> phrases = new ArrayList<Sentence>();	
		
		// Open database
		open();
		
		// Cursor cursor = database.query(SentenceDatabaseHelper.TABLE_SENTENCE, allColumns, null, null, null, null, null);
		Cursor cursor = database.query(true, SentenceDatabaseHelper.TABLE_SENTENCE, TABLE_SENTENCE_COLUMNS, null, null, null, null, null, null);		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Sentence phrase = cursorToSentence(cursor);
			phrases.add(phrase);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		
		// Close database
		close();
		
		
		return phrases;
	}
	

	/**
	 * <p>
	 * Returns the {@link com.xihuanicode.tlatoa.entity.Sentence} object identified by the sentenceId parameter.
	 * <p>
	 * @return The {@link com.xihuanicode.tlatoa.entity.Sentence} object identified by the sentenceId parameter.
	 * @see    {@link com.xihuanicode.tlatoa.entity.Sentence}
	 */
	public Sentence getSentenceById(long sentenceId) throws IllegalArgumentException {

		if(sentenceId < 1){
			throw new IllegalArgumentException("The sentenceId parameter must be greater than zero.");
		}
		
		// Open database
		open();
		
		Sentence sentence = new Sentence();
		List<SentenceResource> resources = new ArrayList<SentenceResource>();
				
		Cursor cursor = database.query(
				
				SentenceDatabaseHelper.TABLE_SENTENCE_RESOURCES, 				// Table
				TABLE_SENTENCE_RESOURCE_COLUMNS, 							// Columns
				SentenceDatabaseHelper.RESOURCES_SENTENCE_ID + " = " + sentenceId, 	// Where (condition)
				null,												// Group By
				null,												// Order By
				null,												// Having
				null												// Limit
				);
		
		
		if(cursor.moveToFirst()){
			
			sentence.setId(sentenceId);
			
			while (!cursor.isAfterLast()) {
				SentenceResource sr = cursorToSentenceResource(cursor);
				resources.add(sr);
				cursor.moveToNext();
			}
			
			sentence.setSentenceResource(resources);
			
		}
		
		// Close database
		close();
		
		return sentence;
	}
	
	/**
	 * <p>
	 * Checks if the Sentence (s) pased as the parameter is saved in the local database. 
	 * <p>
	 * @param  s  A Sentence type for searching for it in the local database.
	 * @return The SentenceId if it exists in the local database, 0 otherwise.  
	 * @see    Sentence
	 */
	
	public long existsInLocalDb(Sentence s){
		
		long exists = 0;
		
		// Open database
		open();		
		
		// Get the Sentence from local database
		Cursor cursor = database.query
		(
				
				SentenceDatabaseHelper.TABLE_SENTENCE, 								// Table
				TABLE_SENTENCE_COLUMNS, 											// Columns
				SentenceDatabaseHelper.SENTENCE_TEXT + " = '" + s.getText() + "'",	// Where (condition)
				null,																// Group By
				null,																// Order By
				null,																// Having
				null																// Limit
		);
		
		
		if(cursor.moveToFirst()){
			
			while (!cursor.isAfterLast()) {
				s = cursorToSentence(cursor);
				cursor.moveToNext();
			}
			
			exists = s.getId();
		}
		
		// Close database
		close();
		
		return exists;
	}
	
	private Sentence cursorToSentence(Cursor c){
		
		Sentence s = new Sentence();
		
		s.setId(c.getInt(0));
		s.setText(c.getString(1));
		s.setCreatedAt(c.getLong(2));
		s.setExpiresAt(c.getLong(3));
		
		return s;
	}
	
	private SentenceResource cursorToSentenceResource(Cursor c){
		
		SentenceResource sr = new SentenceResource();
		
		sr.setResourceId(c.getInt(1));
		sr.setResourceURL(c.getString(2));
		sr.setSequenceOrder(c.getInt(3));
		sr.setResourceImage(c.getBlob(4));		
		
		return sr;
		
	}
}