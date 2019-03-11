package com.uniovi.services;

import com.uniovi.entities.Chat;
import com.uniovi.entities.Message;
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
			messageRepository.deleteById(message.getId());
		}
	}

	public void deleteAll() {
		messageRepository.deleteAll();
	}

	public void addAll(Iterable<Message> messagesList) {
		messageRepository.saveAll(messagesList);
	}
}
