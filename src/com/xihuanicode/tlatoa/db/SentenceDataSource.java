package com.xihuanicode.tlatoa.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xihuanicode.tlatoa.entity.Sentence;
import com.xihuanicode.tlatoa.entity.SentenceResource;

public class SentenceDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SentenceDatabaseHelper dbHelper;
	
	private static final String[] TABLE_SENTENCE_COLUMNS =
	{
		SentenceDatabaseHelper.SENTENCE_ID,
		SentenceDatabaseHelper.SENTENCE,
		SentenceDatabaseHelper.SENTENCE_CREATED_AT
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

	public Phrase createSentence(Sentence sentence) {		
		
		// Content values for header (Sentence)
		ContentValues values = null;
		long insertId = 0;
		
		
		try {

			values = new ContentValues();
			values.put(SentenceDatabaseHelper.SENTENCE_ID, sentence.getSentenceId());
			values.put(SentenceDatabaseHelper.SENTENCE, sentence.getSentence());
			values.put(SentenceDatabaseHelper.SENTENCE_CREATED_AT,new java.util.Date().getTime());
			
			// Perform the operation in the database
			open();
			insertId = database.insert(SentenceDatabaseHelper.TABLE_SENTENCE, null, values);
			close();

		} catch (Exception e) {
			// TODO: Candidate code to send for reporting
			e.printStackTrace();
		}
		
		try{
			if(insertId > 0){
				
				for (SentenceResource sr : sentence.getSentenceResource()){
					values = new ContentValues();
					values.put(SentenceDatabaseHelper.RESOURCES_SENTENCE_ID, sentence.getSentenceId());
					values.put(SentenceDatabaseHelper.RESOURCES_RESOURCE_ID, sr.getResourceId());
					values.put(SentenceDatabaseHelper.RESOURCES_RESOURCE_URL, sr.getResourceURL());
					values.put(SentenceDatabaseHelper.RESOURCES_SEQUENCE_ORDER, sr.getSequenceOrder());
					values.put(SentenceDatabaseHelper.RESOURCES_SENTENCE_IMAGE, sr.getResourceImage());
					
					// Perform the operation in the database
					open();
					insertId = database.insert(SentenceDatabaseHelper.TABLE_SENTENCE_RESOURCES, null, values);
					close();
				}				

			}
		}catch(Exception ex){
			// TODO: Candidate code to send for reporting
			ex.printStackTrace();
		}
		
		// Open database
		open();
		
		
		// Get the last inserted row
		Cursor cursor = null;
		try {
			
			if(insertId > 0){
				
				cursor = database.query(
						SentenceDatabaseHelper.TABLE_SENTENCE, 				// Table
						TABLE_SENTENCE_COLUMNS, 							// Columns
						SentenceDatabaseHelper.SENTENCE_ID + " = " + sentence.getSentenceId(), 	// Where (condition)
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
		
		// Close database
		close();

		return newPhrase;
	}

	public void deletePhrase(Phrase phrase) {
		long id = phrase.getId();
		open();
		database.delete(SentenceDatabaseHelper.TABLE_SENTENCE, SentenceDatabaseHelper.SENTENCE_ID + " = " + id, null);
		close();
	}

	public List<Phrase> getAllSentences() {
		
		List<Phrase> phrases = new ArrayList<Phrase>();	
		
		// Open database
		open();
		
		// Cursor cursor = database.query(SentenceDatabaseHelper.TABLE_SENTENCE, allColumns, null, null, null, null, null);
		Cursor cursor = database.query(true, SentenceDatabaseHelper.TABLE_SENTENCE, TABLE_SENTENCE_COLUMNS, null, null, null, null, null, null);		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Phrase phrase = cursorToPhrase(cursor);
			phrases.add(phrase);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		
		// Close database
		close();
		
		
		return phrases;
	}
	
	public Sentence getSentenceById(int sentenceId){

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
			
			sentence.setSentenceId(sentenceId);
			
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
	
	private SentenceResource cursorToSentenceResource(Cursor cursor){
		
		SentenceResource sr = new SentenceResource();
		
		sr.setResourceId(cursor.getInt(1));
		sr.setResourceURL(cursor.getString(2));
		sr.setSequenceOrder(cursor.getInt(3));
		sr.setResourceImage(cursor.getBlob(4));		
		
		return sr;
		
	}

	private Phrase cursorToPhrase(Cursor cursor) {
		
		Phrase phrase = new Phrase();
		
		phrase.setId(cursor.getLong(0));
		phrase.setPhrase(cursor.getString(1));
		phrase.setCreatedAt(cursor.getLong(2));
		
		return phrase;
	}
}