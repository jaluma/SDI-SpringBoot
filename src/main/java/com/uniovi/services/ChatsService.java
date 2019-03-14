package com.uniovi.services;

import com.uniovi.entities.Association;
import com.uniovi.entities.Chat;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChatsService {
	private final ChatRepository chatRepository;
	private final UsersService usersService;

	@Autowired
	public ChatsService(ChatRepository chatRepository, UsersService usersService) {
		this.chatRepository = chatRepository;
		this.usersService = usersService;
	}

	@Transactional
	public void addChat(Chat chat) {
		chatRepository.save(chat);
	}

	public List<Chat> getChats(Item item) {
		return chatRepository.findAll(item);
	}

	public Chat getChat(Long id) {
		Optional<Chat> chat = chatRepository.findById(id);
		return chat.orElse(null);
	}

	public Chat findChatByUserAndItem(User sender, Item item) {
		Optional<Chat> chat = chatRepository.findByUserAndItem(sender, item);
		return chat.orElse(null);
	}

	@Transactional
	public void deleteChat(Chat chat) {
		Association.Chats.removeMessages(chat);
		Association.Chats.removeChat(chat);
		chatRepository.save(chat);
	}

	@Transactional
	public Chat createChat(Item item) {
		return new Chat(item);
	}

	@Transactional
	public void deleteAll() {
		chatRepository.deleteAll();
	}

	void addAll(Iterable<Chat> chatList) {
		chatRepository.saveAll(chatList);
	}

	public boolean isValidUser(Chat chat, User user) {
		List<User> lista = usersService.getUsers(chat);
		if(lista.contains(user)) {
			return true;
		}
		if(chat.getItem().getSellerUser().equals(user)) {
			return true;
		}
		return lista.size() + 1 <= 2;

	}

	public Page<Chat> getListChat(Pageable pageable, User user) {
		return chatRepository.findAllList(pageable, user);
	}
}
