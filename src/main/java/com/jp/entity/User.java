package com.jp.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.DynamicUpdate;
import org.jboss.aerogear.security.otp.api.Base32;

@Entity
@Table(name="customer")
@DynamicUpdate
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	
	@Pattern(regexp="^([a-zA-Z0-9]+|[a-zA-Z0-9]+\\s{1}[a-zA-Z0-9]{1,}|[a-zA-Z0-9]+\\s{1}[a-zA-Z0-9]{3,}\\s{1}[a-zA-Z0-9]{1,})$")
	private String name;
	
	@Pattern(regexp="^[a-zA-Z0-9._-]{3,}$")
	private String username;
	
	@Pattern(regexp="(?-i)(?=^.{8,}$)((?!.*\\s)(?=.*[A-Z])(?=.*[a-z]))(?=(1)(?=.*\\d)|.*[^A-Za-z0-9])^.*$")
	private String password;

	@Column(name="email",nullable = false)
	@Pattern(regexp="^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")
	private String email;
	
	private Boolean active = true;
	
	private Boolean gfa = false;
	
	@OneToOne
	private Roles roles; 
	
	private String secret;

	private Instant created = Instant.now();
	private Instant updated = Instant.now();
	
	public User() {
		// TODO Auto-generated constructor stub
		super();
        this.secret = Base32.random();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getUpdated() {
		return updated;
	}

	public void setUpdated(Instant updated) {
		this.updated = updated;
	}

	public Boolean getGfa() {
		return gfa;
	}

	public void setGfa(Boolean gfa) {
		this.gfa = gfa;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Roles getRoles() {
		return roles;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}
	

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", active="
				+ active + ", gfa=" + gfa + ", created=" + created + ", updated=" + updated + "]";
	}

	
}
