package com.xihuanicode.tlatoa.entity;

public class SentenceResource {
   	private int resourceId;
   	private String resourceURL;
   	private int sequenceOrder;

 	public int getResourceId(){
		return this.resourceId;
	}
	public void setResourceId(int resourceId){
		this.resourceId = resourceId;
	}
 	public String getResourceURL(){
		return this.resourceURL;
	}
	public void setResourceURL(String resourceURL){
		this.resourceURL = resourceURL;
	}
 	public int getSequenceOrder(){
		return this.sequenceOrder;
	}
	public void setSequenceOrder(int sequenceOrder){
		this.sequenceOrder = sequenceOrder;
	}
}
