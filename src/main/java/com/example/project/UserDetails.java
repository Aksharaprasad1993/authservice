package com.example.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "userdetails")
public class UserDetails {

	@Id
	@Column(name="username")
	private String username;

	@Column(name="name")
	private String name;

	@Column(name="lastname")
	private String lastname;

	@Column(name="password")
	private String password;

	@Column(name="dob")
	private String dob;

	public UserDetails() {

	}

	public UserDetails(String name, String lastname, String username, String password, String dob) {
		super();

		this.name = name;
		this.lastname = lastname;
		this.username = username;
		this.password = password;
		this.dob = dob;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getDob() {
		LocalDate formattedDate = null;
		formattedDate = LocalDate.parse(dob);

		return formattedDate;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", name=" + name + ", lastname=" + lastname + ", password=" + password
				+ ", dob=" + dob + "]";
	}

	public boolean isUsernameExists() {
		return null != username;
	}

	public boolean validatePassword(String encodedPassword) {
		return null != password && password.equalsIgnoreCase(encodedPassword);
	}
	
}
