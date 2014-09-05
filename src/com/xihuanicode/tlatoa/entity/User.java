package com.xihuanicode.tlatoa.entity;

public class User {
	
	private long id;
	private String name;
	private String firstName;
	private String lastName;
	private String middleName;
	private String socialMediaId;
	private String gender;
	private String locationId;
	private String locationName;
	private String email;
	private String profilePictureUrl;
	private Role [] roles = { new Role("169", "Tlatoa Android App User") };	
	
	public User(long id, String name, String firstName, String lastName,
			String middleName, String socialMediaId, String gender,
			String locationId, String locationName, String email,
			String profilePictureUrl) {
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.socialMediaId = socialMediaId;
		this.gender = gender;
		this.locationId = locationId;
		this.locationName = locationName;
		this.email = email;
		this.profilePictureUrl = profilePictureUrl;
	}
	
	public User(String name, String firstName, String lastName,
			String middleName, String socialMediaId, String gender,
			String locationId, String locationName, String email,
			String profilePictureUrl) {
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.socialMediaId = socialMediaId;
		this.gender = gender;
		this.locationId = locationId;
		this.locationName = locationName;
		this.email = email;
		this.profilePictureUrl = profilePictureUrl;
	}
	
	public User() {}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getSocialMediaId() {
		return socialMediaId;
	}
	public void setSocialMediaId(String socialMediaId) {
		this.socialMediaId = socialMediaId;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}
	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}
	public Role [] getRoles() {
		return roles;
	}
	public void setRoles(Role [] roles) {
		this.roles = roles;
	}
	
}
