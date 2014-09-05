package com.xihuanicode.tlatoa.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xihuanicode.tlatoa.Config;
import com.xihuanicode.tlatoa.entity.Sentence;
import com.xihuanicode.tlatoa.entity.SentenceResource;

public class SentenceDataSource {

	// Database fields
	private SQLiteDatabase database;
	private TlatoaDatabaseHelper dbHelper;
	
	private static final String[] TABLE_SENTENCE_COLUMNS =
	{
		TlatoaDatabaseHelper.SENTENCE_ID,
		TlatoaDatabaseHelper.SENTENCE_TEXT,
		TlatoaDatabaseHelper.SENTENCE_CREATED_AT,
		TlatoaDatabaseHelper.SENTENCE_EXPIRES_AT
	};
	
	
	public static final String[] TABLE_SENTENCE_RESOURCE_COLUMNS =
	{
		TlatoaDatabaseHelper.RESOURCES_SENTENCE_ID,
		TlatoaDatabaseHelper.RESOURCES_RESOURCE_ID,
		TlatoaDatabaseHelper.RESOURCES_RESOURCE_URL,
		TlatoaDatabaseHelper.RESOURCES_SEQUENCE_ORDER,
		TlatoaDatabaseHelper.RESOURCES_SENTENCE_IMAGE
	};

	public SentenceDataSource(Context context) {
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
			values.put(TlatoaDatabaseHelper.SENTENCE_ID, s.getId());
			values.put(TlatoaDatabaseHelper.SENTENCE_TEXT, s.getText());
			values.put(TlatoaDatabaseHelper.SENTENCE_CREATED_AT,new java.util.Date().getTime());
			values.put(TlatoaDatabaseHelper.SENTENCE_EXPIRES_AT,new java.util.Date().getTime() + Config.CACHE_EXPIRATION_VALID_TIME);
			
			
			insertId = database.insert(TlatoaDatabaseHelper.TABLE_SENTENCE, null, values);

		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		try{
			if(insertId > 0){
				
				for (SentenceResource sr : s.getSentenceResource()){
					values = new ContentValues();
					values.put(TlatoaDatabaseHelper.RESOURCES_SENTENCE_ID, s.getId());
					values.put(TlatoaDatabaseHelper.RESOURCES_RESOURCE_ID, sr.getResourceId());
					values.put(TlatoaDatabaseHelper.RESOURCES_RESOURCE_URL, sr.getResourceURL());
					values.put(TlatoaDatabaseHelper.RESOURCES_SEQUENCE_ORDER, sr.getSequenceOrder());
					values.put(TlatoaDatabaseHelper.RESOURCES_SENTENCE_IMAGE, sr.getResourceImage());
					
					database.insert(TlatoaDatabaseHelper.TABLE_SENTENCE_RESOURCES, null, values);
				}				

			}
		}catch(Exception ex){
			// TODO: Candidate code to send for reporting
			ex.printStackTrace();
		}
		
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
		database.delete(TlatoaDatabaseHelper.TABLE_SENTENCE, TlatoaDatabaseHelper.SENTENCE_ID + " = " + id, null);
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
		Cursor cursor = database.query(true, TlatoaDatabaseHelper.TABLE_SENTENCE, TABLE_SENTENCE_COLUMNS, null, null, null, null, null, null);		
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
	 * Returns the {@link Sentence} object identified by the sentenceId parameter.
	 * <p>
	 * @return The {@link Sentence} object identified by the sentenceId parameter.
	 * @see    {@link Sentence}
	 */
	public Sentence getSentenceById(long sentenceId) throws IllegalArgumentException {
		
		// Open database
		open();
		Sentence s = new Sentence();
		
		// Get the Sentence from local database
		Cursor cursor = database.query
		(
				TlatoaDatabaseHelper.TABLE_SENTENCE, 								// Table
				TABLE_SENTENCE_COLUMNS, 											// Columns
				TlatoaDatabaseHelper.SENTENCE_ID + " = '" + sentenceId + "'",	    // Where (condition)
				null,																// Group By
				null,																// Order By
				null,																// Having
				null																// Limit
		);
		
		
		if(cursor.moveToFirst()){
			
			s = null;
			while (!cursor.isAfterLast()) {
				s = cursorToSentence(cursor);
				break;
			}
		}


		// Now retrieve the sentence resources
		List<SentenceResource> resources = new ArrayList<SentenceResource>();
		cursor = database.query(
			TlatoaDatabaseHelper.TABLE_SENTENCE_RESOURCES, 				// Table
			TABLE_SENTENCE_RESOURCE_COLUMNS, 							// Columns
			TlatoaDatabaseHelper.RESOURCES_SENTENCE_ID + " = " + s.getId(), 	// Where (condition)
			null,												// Group By
			null,												// Order By
			null,												// Having
			null												// Limit
		);

		if(cursor.moveToFirst()){
			
			s.setId(s.getId());
			
			while (!cursor.isAfterLast()) {
				SentenceResource sr = cursorToSentenceResource(cursor);
				resources.add(sr);
				cursor.moveToNext();
			}
			
			s.setSentenceResource(resources);
			
		}		
		 
		// Close database
		close();
		
		return s;
	}
	
	/**
	 * <p>
	 * Checks if the Sentence (s) pased as the parameter is saved in the local database. 
	 * <p>
	 * @param  s  A {@link com.xihuanicode.tlatoa.entity.Sentence} type for searching for it in the local database.
	 * @return The same {@link com.xihuanicode.tlatoa.entity.Sentence} object if it exists in the local database, null otherwise.
	 * @see    {@link com.xihuanicode.tlatoa.entity.Sentence}
	 */
	
	public Sentence existsInLocalDb(Sentence s){
		
		// Open database
		open();
		
		// Get the Sentence from local database
		Cursor cursor = database.query
		(
				TlatoaDatabaseHelper.TABLE_SENTENCE, 								// Table
				TABLE_SENTENCE_COLUMNS, 											// Columns
				TlatoaDatabaseHelper.SENTENCE_TEXT + " = '" + s.getText() + "'",	// Where (condition)
				null,																// Group By
				null,																// Order By
				null,																// Having
				null																// Limit
		);
		
		
		if(cursor.moveToFirst()){
			
			s = null;
			while (!cursor.isAfterLast()) {
				s = cursorToSentence(cursor);
				break;
			}
		}


		// Now retrieve the sentence resources
		List<SentenceResource> resources = new ArrayList<SentenceResource>();
		cursor = database.query(
			TlatoaDatabaseHelper.TABLE_SENTENCE_RESOURCES, 				// Table
			TABLE_SENTENCE_RESOURCE_COLUMNS, 							// Columns
			TlatoaDatabaseHelper.RESOURCES_SENTENCE_ID + " = " + s.getId(), 	// Where (condition)
			null,												// Group By
			null,												// Order By
			null,												// Having
			null												// Limit
		);

		if(cursor.moveToFirst()){
			
			s.setId(s.getId());
			
			while (!cursor.isAfterLast()) {
				SentenceResource sr = cursorToSentenceResource(cursor);
				resources.add(sr);
				cursor.moveToNext();
			}
			
			s.setSentenceResource(resources);
			
		}		
		 
		// Close database
		close();
		
		return s;
	}
	
	
	/**
	 * <p>
	 * Checks if the {@link Sentence} object passed as a parameter has expired. 
	 * <p>
	 * @param  s  A {@link Sentence} object.
	 * @return True, if the {@link Sentence} has expired, false otherwise.
	 * @see    {@link com.xihuanicode.tlatoa.entity.Sentence}
	 */
	
	public boolean hasExpired(Sentence s){		
		return new java.util.Date().getTime() > existsInLocalDb(s).getExpiresAt();
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