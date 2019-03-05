package com.uniovi.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Chat {

	@Id
	@GeneratedValue
	private long id;

	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
	@OrderBy("time ASC")
	private Set<Message> messages = new HashSet<>();

	@ManyToOne
	private Item item;

	@ManyToMany
	private Set<User> users = new HashSet<>();

	Chat() {
	}

	public Chat(Item item) {
		this.item = item;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Set<Message> getMessages() {
		return new HashSet<>(messages);
	}

	Set<Message> _getMessages() {
		return messages;
	}

	private ArrayList<User> getUsers() {
		return new ArrayList<>(users);
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	Set<User> _getUsers() {
		return users;
	}


	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Chat chat = (Chat) o;
		return Objects.equals(messages, chat.messages) && Objects.equals(item, chat.item);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messages, item);
	}

	public User getUser(User distint) {
		return getUsers().get(0).equals(distint) ? getUsers().get(1) : getUsers().get(0);
	}
}
