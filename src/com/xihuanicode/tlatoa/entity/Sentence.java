
package com.xihuanicode.tlatoa.entity;

import java.util.List;

public class Sentence {
   	private List<SentenceResource> resources;
   	private String sentence;
   	private int sentenceId;

 	public List<SentenceResource> getSentenceResource(){
		return this.resources;
	}
	public void setSentenceResource(List<SentenceResource> resources){
		this.resources = resources;
	}
 	public String getSentence(){
		return this.sentence;
	}
	public void setSentence(String sentence){
		this.sentence = sentence;
	}
 	public int getSentenceId(){
		return this.sentenceId;
	}
	public void setSentenceId(int sentenceId){
		this.sentenceId = sentenceId;
	}
}
