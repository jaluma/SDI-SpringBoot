package com.uniovi.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Message {

	@Id
	@GeneratedValue
	private long id;

	private String message;
	private LocalDateTime time;

	@ManyToOne(fetch = FetchType.EAGER)
	private Chat chat;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	public Message(String message, LocalDateTime time) {
		setMessage(message);
		setTime(time);
	}

	Message() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Chat getChat() {
		return chat;
	}

	void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getUser() {
		return user;
	}

	void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Message message1 = (Message) o;
		return Objects.equals(message, message1.message) && Objects.equals(time, message1.time);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, time);
	}
}
