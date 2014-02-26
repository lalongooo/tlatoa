package com.xihuanicode.tlatoa.entity;

public class SentenceResource implements Comparable<SentenceResource> {

	private int resourceId;
	private String resourceURL;
	private int sequenceOrder;
	private byte[] resourceImage;

	// Resource Id
	public int getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	// Resource URL
	public String getResourceURL() {
		return this.resourceURL;
	}

	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
	}

	// Resource sequenceOrder
	public int getSequenceOrder() {
		return this.sequenceOrder;
	}

	public void setSequenceOrder(int sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}

	// Resource image
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
