package com.example.authservice.user;

import java.time.LocalDate;

public class ResponseUser {

    private String username;
    private String name;
    private String lastname;
    private LocalDate dob;

    public ResponseUser(String username, String name, String lastname, LocalDate dob) {
		super();
		this.username = username;
		this.name = name;
		this.lastname = lastname;
		this.dob = dob;
	}

	public ResponseUser() {
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

}

