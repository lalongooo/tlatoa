package com.xihuanicode.tlatoa;

public class Config {
	

    /**
     * The URL of the web service for the users registration 
     */
	public static final String USER_REGISTRARION_URL = "http://tlatoa.herokuapp.com/kerberos/api/user";
	
	/**
	 * Tlatoa web service URL for translation
	 */
	public static final String PHRASE_TRANSLATION_URL = "http://tlatoa.herokuapp.com/manager/api/sentence?phrase=";
	
	/**
	 * Enable caching.
	 */
	public static final boolean PHRASE_CACHE = false;
	
	/**
	 * Default animation time of each image in the translation result activity (milliseconds). 
	 */	
	public static final int ANIMATION_DURATION = 500;
	

}