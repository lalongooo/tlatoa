package com.xihuanicode.tlatoa.db;


public class Phrase {
	private long id;
	private String phrase;
	private long createdAt;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return phrase;
	}
}