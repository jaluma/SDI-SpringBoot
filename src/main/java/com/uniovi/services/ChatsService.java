package com.uniovi.services;

import com.uniovi.entities.Chat;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.repositories.ChatRepository;
import com.uniovi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

	public void addChat(Chat chat) {
		chatRepository.save(chat);
	}

	public Page<Chat> getChats(Pageable pageable, User user) {
		return chatRepository.findAll(pageable, user);
	}


	public List<Chat> getChat(User user, Item item) {
		return chatRepository.getChat(user, item);
	}

	public Chat getChat(Long id) {
		Optional<Chat> chat = chatRepository.findById(id);
		return chat.orElse(null);
	}

	public Chat findChatByUserAndItem(User sender, Item item) {
		Optional<Chat> chat = chatRepository.findByUserAndItem(sender, item);
		return chat.orElse(null);
	}
}
