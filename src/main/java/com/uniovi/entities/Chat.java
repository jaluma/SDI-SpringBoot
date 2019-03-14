package com.uniovi.entities;

import javax.persistence.*;
import java.util.*;

@Entity
public class Chat {

	@Id
	@GeneratedValue
	private long id;

	@OneToMany(mappedBy = "chat", orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("time ASC")
	private List<Message> messages = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	private Item item;

	Chat() {
	}

	public Chat(Item item) {
		this.item = item;
		Association.Chats.createChat(this, item);
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

	public Set<Message> getMessages() {
		return new HashSet<>(messages);
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	List<Message> _getMessages() {
		return messages;
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
}
