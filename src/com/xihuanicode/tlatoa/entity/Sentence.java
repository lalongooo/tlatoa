package com.xihuanicode.tlatoa.entity;

import java.util.List;

public class Sentence {

	private List<SentenceResource> resources;
	private long id;
	private String text;
	private long createdAt;
	private long expiresAt;

	public List<SentenceResource> getSentenceResource() {
		return this.resources;
	}

	public void setSentenceResource(List<SentenceResource> resources) {
		this.resources = resources;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getCreatedAt() {
		return createdAt;
	}
	
	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public long getExpiresAt() {
		return expiresAt;
	}
}
