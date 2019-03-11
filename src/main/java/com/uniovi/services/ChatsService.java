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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatsService {
	private final ChatRepository chatRepository;

	@Autowired
	public ChatsService(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

	@Transactional
	public void addChat(Chat chat) {
		chatRepository.save(chat);
	}

	public List<Chat> getChats(Item item) {
		return chatRepository.findAll(item);
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

	@Transactional
	public void deleteChat(Chat chat) {
		Association.Chats.removeMessages(chat.getUsers().get(0), chat.getUsers().get(1), chat);
		Association.Chats.removeChat(chat);
		chatRepository.delete(chat);
	}

	@Transactional
	public Chat createChat(User sender, Item item) {
		Set<User> set = new HashSet<>();
		set.add(sender);
		set.add(item.getSellerUser());
		return new Chat(item, set);
	}

	@Transactional
	public void deleteAll() {
		chatRepository.deleteAll();
	}

	public void addAll(Iterable<Chat> chatList) {
		chatRepository.saveAll(chatList);
	}
}
