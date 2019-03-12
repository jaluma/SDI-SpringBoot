package com.uniovi.controllers;

import com.uniovi.controllers.util.Utilities;
import com.uniovi.entities.*;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chat/")
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

	@RequestMapping("conversation/{id}")
	public String getConversation(Model model, @PathVariable Long id) {
		Chat chat = chatsService.getChat(id);
		if(chat == null) {
			throw new IllegalStateException("Illegal");
		}

		User sender = Utilities.getCurrentUser(usersService);
		User receiver = chat.getUser(sender);

		if(!chat.getUsers().contains(sender)) {
			throw new IllegalStateException("Illegal");
		}

		model.addAttribute("sender", sender);
		model.addAttribute("chat", chat);
		model.addAttribute("receiver", receiver);

		//format time
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
		model.addAttribute("formatter", formatter);

		List<Message> messages = new ArrayList<>(chat.getMessages());
		//		messages.sort(Comparator.comparing(Message::getTime));

		model.addAttribute("messagesList", messages);
		return "chat/conversation";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.POST)
	public String postMessage(@RequestParam("message") String message, @PathVariable Long id) {
		Chat chat = chatsService.getChat(id);
		User sender = Utilities.getCurrentUser(usersService);
		User receiver = chat.getUser(sender);

		Message chatMessage = new Message(message, OffsetDateTime.now());
		messagesService.addMessage(chatMessage);
		Association.Chats.sendMessage(sender, receiver, chat, chatMessage);
		chatsService.addChat(chat);

		return "redirect:/chat/conversation/" + id;
	}

	@RequestMapping(value = "list", method = RequestMethod.GET)
	public String getList(Model model, Pageable pageable, Principal principal) {
		Page<Chat> chats = chatsService.getChats(pageable, Utilities.getCurrentUser(principal, usersService));
		User sender = Utilities.getCurrentUser(usersService);
		model.addAttribute("sender", sender);
		model.addAttribute("page", chats);
		model.addAttribute("chatsList", chats.getContent());

		return "chat/list";
	}

	@RequestMapping(value = "create/{id}", method = RequestMethod.GET)
	public String create(Model model, @PathVariable Long id) {
		Item item = itemsService.getItem(id);

		User sender = Utilities.getCurrentUser(usersService);

		Chat chat = chatsService.findChatByUserAndItem(sender, item);
		if(chat == null) {
			chat = chatsService.createChat(sender, item);
		}

		chatsService.addChat(chat);
		logger.info(String.format("Create Chat %d", id));

		return "redirect:/chat/conversation/" + chat.getId();
	}


	@RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
	public String remove(@PathVariable Long id) {
		Chat chat = chatsService.getChat(id);
		if(chat == null) {
			throw new IllegalStateException("Illegal");
		}

		messagesService.deleteMessages(chat.getMessages());
		chatsService.deleteChat(chat);

		logger.info(String.format("Delete chat %d", id));

		return "redirect:/chat/list";
	}
}

