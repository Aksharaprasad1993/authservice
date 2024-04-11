package com.example.authservice.user;


import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "userdetails")
public class User {

    @Id
    private String username;
    
    private String name;
    private String lastname;
    private String password;
    private LocalDate dob;

    public User() {

    }

    public User(String name, String lastname, String username, String password, LocalDate dob) {
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
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

	@Override
	public String toString() {
		return "User [username=" + username + ", name=" + name + ", lastname=" + lastname + ", password="
				+ password + ", dob=" + dob + "]";
	}



}

