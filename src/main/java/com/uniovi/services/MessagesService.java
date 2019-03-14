package com.uniovi.services;

import com.uniovi.entities.Association;
import com.uniovi.entities.Chat;
import com.uniovi.entities.Message;
import com.uniovi.entities.User;
import com.uniovi.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MessagesService {

	private final MessageRepository messageRepository;

	@Autowired
	public MessagesService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public void addMessage(Message message) {
		messageRepository.save(message);
	}

	public List<Message> getMessages(Chat chat) {
		return messageRepository.getMessagesByChat(chat);
	}

	public void deleteMessages(Set<Message> list) {
		for(Message message : list) {
			messageRepository.delete(message);
		}
	}

	public void deleteAll() {
		messageRepository.deleteAll();
	}

	void addAll(Iterable<Message> messagesList) {
		messageRepository.saveAll(messagesList);
	}

	public void sendMessage(User sender, Chat chat, Message chatMessage) {
		Association.Chats.sendMessage(sender, chat, chatMessage);
	}
}
