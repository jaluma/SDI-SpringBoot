package com.uniovi.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class User {
	@Id
	@GeneratedValue
	private long id;
	@Column(unique = true)
	private String email;
	private String name;
	private String lastName;

	private String password;
	@Transient //propiedad que no se almacena e la tabla.
	private String passwordConfirm;

	private String role;
	private double money;

	@OneToMany(mappedBy = "buyerUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Item> buyerItems = new HashSet<>();

	@OneToMany(mappedBy = "sellerUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Item> sellerItems = new HashSet<>();

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Message> senderMessages = new HashSet<>();

	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Message> receiverMessages = new HashSet<>();

	public User(String email, String name, String lastName) {
		super();
		this.email = email;
		this.name = name;
		this.lastName = lastName;
	}

	public User() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return this.name + " " + this.lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	Set<Item> _getSellerItems() {
		return sellerItems;
	}

	Set<Item> _getBuyerItems() {
		return buyerItems;
	}

	public Set<Item> getSellerItems() {
		return new HashSet<>(sellerItems);
	}

	public Set<Item> getBuyerItems() {
		return new HashSet<>(buyerItems);
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	Set<Message> _getSenderMessages() {
		return senderMessages;
	}

	Set<Message> _getReceiverMessages() {
		return receiverMessages;
	}

	public void setBuyerItems(Set<Item> buyerItems) {
		this.buyerItems = buyerItems;
	}

	public void setSellerItems(Set<Item> sellerItems) {
		this.sellerItems = sellerItems;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return email.equals(user.email) && name.equals(user.name) && lastName.equals(user.lastName) && role.equals(user.role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, name, lastName, role);
	}
}