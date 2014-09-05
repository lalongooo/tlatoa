package com.xihuanicode.tlatoa.entity;

public class SentenceResource implements Comparable<SentenceResource> {

	private int resourceId;
	private String resourceURL;
	private int sequenceOrder;
	private byte[] resourceImage;

	public int getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceURL() {
		return this.resourceURL;
	}

	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
	}

	public int getSequenceOrder() {
		return this.sequenceOrder;
	}

	public void setSequenceOrder(int sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}

	public byte[] getResourceImage() {
		return resourceImage;
	}

	public void setResourceImage(byte[] resourceImage) {
		this.resourceImage = resourceImage;
	}

	@Override
	public int compareTo(SentenceResource sr) {
		return this.sequenceOrder - sr.getSequenceOrder();
	}	
	
}
