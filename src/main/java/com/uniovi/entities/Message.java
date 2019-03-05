package com.uniovi.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Message {

	@Id
	@GeneratedValue
	private Long id;

	private String message;
	private OffsetDateTime time;

	@ManyToOne(fetch = FetchType.LAZY)
	private User sender;
	@ManyToOne(fetch = FetchType.LAZY)
	private User receiver;

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

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
}
