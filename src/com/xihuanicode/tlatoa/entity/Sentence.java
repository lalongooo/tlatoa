package com.xihuanicode.tlatoa.entity;

import java.util.List;

public class Sentence {

	private List<SentenceResource> resources;
	private long id;
	private String text;
	private long createdAt;
	private long expiresAt;

	// Resources
	public List<SentenceResource> getSentenceResource() {
		return this.resources;
	}

	public void setSentenceResource(List<SentenceResource> resources) {
		this.resources = resources;
	}

	// Sentence Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	// Text
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	// Created at
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getCreatedAt() {
		return createdAt;
	}
	
	// Expires at
	public void setExpiresAt(long expiresAt) {
		this.createdAt = expiresAt;
	}

	public long getExpiresAt() {
		return expiresAt;
	}
}
