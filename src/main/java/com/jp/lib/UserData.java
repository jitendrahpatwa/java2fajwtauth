package com.jp.lib;

import javax.persistence.OneToOne;

import com.jp.entity.Roles;

public class UserData {

	public UserData() {
		// TODO Auto-generated constructor stub
	}
	
	private String name;
	private String email;
	private String username;
	private Boolean active = true;
	private Boolean gfa = false;
	private Roles roles;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Boolean getGfa() {
		return gfa;
	}
	public void setGfa(Boolean gfa) {
		this.gfa = gfa;
	}
	public Roles getRoles() {
		return roles;
	}
	public void setRoles(Roles roles) {
		this.roles = roles;
	}
	
	
}
