package com.uniovi.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class Message {

	@Id
	@GeneratedValue
	private long id;

	private String message;
	private OffsetDateTime time;

	@ManyToOne(fetch = FetchType.EAGER)
	private Chat chat;

	@ManyToOne(fetch = FetchType.EAGER)
	private User sender;
	@ManyToOne(fetch = FetchType.EAGER)
	private User receiver;

	public Message(String message, OffsetDateTime time) {
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

	public OffsetDateTime getTime() {
		return time;
	}

	public void setTime(OffsetDateTime time) {
		this.time = time;
	}

	public Chat getChat() {
		return chat;
	}

	void setChat(Chat chat) {
		this.chat = chat;
	}

	public User getSender() {
		return sender;
	}

	public User getReceiver() {
		return receiver;
	}

	void setSender(User sender) {
		this.sender = sender;
	}

	void setReceiver(User receiver) {
		this.receiver = receiver;
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
