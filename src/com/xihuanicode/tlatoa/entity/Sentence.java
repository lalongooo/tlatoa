package com.xihuanicode.tlatoa.entity;

import java.util.List;

public class Sentence {

	private List<SentenceResource> resources;
	private int sentenceId;
	private String sentence;
	private int createdAt;

	// Resources
	public List<SentenceResource> getSentenceResource() {
		return this.resources;
	}

	public void setSentenceResource(List<SentenceResource> resources) {
		this.resources = resources;
	}

	// Sentence
	public String getSentence() {
		return this.sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	// Sentence Id
	public int getSentenceId() {
		return this.sentenceId;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	// Created at
	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public int getCreatedAt() {
		return createdAt;
	}
}
