package com.example.authservice.user;



public class RequestUser {

	private String username;
	private String oldPassword;
	private String updatedPassword;



	public RequestUser(String username, String oldPassword, String updatedPassword) {
		super();
		this.username = username;
		this.oldPassword = oldPassword;
		this.updatedPassword = updatedPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getUpdatedPassword() {
		return updatedPassword;
	}

	public void setUpdatedPassword(String updatedPassword) {
		this.updatedPassword = updatedPassword;
	}

	@Override
	public String toString() {
		return "RequestUser [username=" + username + ", oldPassword=" + oldPassword
				+ ", updatedPassword=" + updatedPassword + "]";
	}
	
	

}
