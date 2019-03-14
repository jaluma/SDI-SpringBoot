package com.uniovi.controllers;

import com.uniovi.controllers.util.Utilities;
import com.uniovi.entities.Chat;
import com.uniovi.entities.Item;
import com.uniovi.entities.Message;
import com.uniovi.entities.User;
import com.uniovi.services.ChatsService;
import com.uniovi.services.ItemsService;
import com.uniovi.services.MessagesService;
import com.uniovi.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ChatController {
	private final ItemsService itemsService;
	private final UsersService usersService;
	private final ChatsService chatsService;
	private final MessagesService messagesService;

	private Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	public ChatController(ItemsService itemsService, UsersService usersService, ChatsService chatsService, MessagesService messagesService) {
		this.itemsService = itemsService;
		this.usersService = usersService;
		this.chatsService = chatsService;
		this.messagesService = messagesService;
	}

	@RequestMapping("/chat/conversation/{id}")
	public String getConversation(Model model, @PathVariable Long id) {
		Chat chat = chatsService.getChat(id);
		if(chat == null) {
			throw new IllegalStateException("Illegal");
		}

		User user = Utilities.getCurrentUser(usersService);

		if(!chatsService.isValidUser(chat, user)) {
			throw new IllegalStateException("Illegal");
		}

		model.addAttribute("user", user);
		model.addAttribute("chat", chat);

		//format time
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
		model.addAttribute("formatter", formatter);

		List<Message> messages = new ArrayList<>(chat.getMessages());
		messages.sort(Comparator.comparing(Message::getTime));

		model.addAttribute("messagesList", messages);
		return "chat/conversation";
	}

	@RequestMapping(value = "/chat/{id}", method = RequestMethod.POST)
	public String postMessage(@RequestParam("message") String message, @PathVariable Long id) {
		Chat chat = chatsService.getChat(id);
		User sender = Utilities.getCurrentUser(usersService);


		Message chatMessage = new Message(message, LocalDateTime.now());
		messagesService.sendMessage(sender, chat, chatMessage);
		messagesService.addMessage(chatMessage);
		chatsService.addChat(chat);

		return "redirect:/chat/conversation/" + id;
	}

	@RequestMapping(value = "/chat/list", method = RequestMethod.GET)
	public String getList(Model model, Pageable pageable, Principal principal) {
		Page<Chat> chats = chatsService.getListChat(pageable, Utilities.getCurrentUser(principal, usersService));
		User user = Utilities.getCurrentUser(usersService);
		model.addAttribute("usersService", usersService);
		model.addAttribute("user", user);
		model.addAttribute("page", chats);
		model.addAttribute("chatsList", chats.getContent());

		return "chat/list";
	}

	@RequestMapping(value = "/chat/create/{id}", method = RequestMethod.GET)
	public String create(@PathVariable Long id) {
		Item item = itemsService.getItem(id);
		if(item == null) {
			throw new IllegalStateException("Illegal");
		}

		User sender = Utilities.getCurrentUser(usersService);

		Chat chat = chatsService.findChatByUserAndItem(sender, item);
		if(chat == null) {
			chat = chatsService.createChat(item);
		}

		chatsService.addChat(chat);
		logger.info(String.format("Create Chat %d", id));

		return "redirect:/chat/conversation/" + chat.getId();
	}


	@RequestMapping(value = "/chat/delete/{id}", method = RequestMethod.GET)
	public String remove(@PathVariable Long id) {
		Chat chat = chatsService.getChat(id);
		if(chat == null) {
			throw new IllegalStateException("Illegal");
		}

		chatsService.deleteChat(chat);

		logger.info(String.format("Delete chat %d", id));

		return "redirect:/chat/list";
	}
}

