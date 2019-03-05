package com.uniovi.services;

import com.uniovi.entities.Message;
import com.uniovi.entities.User;
import com.uniovi.repositories.ChatRepository;
import com.uniovi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatsService {
	private final UsersRepository usersRepository;
	private final ChatRepository chatRepository;
	private final SimpMessagingTemplate webSocket;

	@Autowired
	public ChatsService(ChatRepository chatRepository, SimpMessagingTemplate webSocket, UsersRepository usersRepository) {
		this.chatRepository = chatRepository;
		this.webSocket = webSocket;
		this.usersRepository = usersRepository;
	}

	public void addMessage(Message message) {
		chatRepository.save(message);
	}

	public List<Message> getMessages(User sender, User receiver) {
		return chatRepository.findAll(sender, receiver);
	}

	@Async
	public void pushChangesToWebSocket() {
		//nuevos mensajes al socket
		for(Message message : chatRepository.findAll()) {
			webSocket.convertAndSend("/topic/messages", message);
		}
	}
}
