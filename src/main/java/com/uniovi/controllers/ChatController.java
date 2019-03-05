package com.uniovi.controllers;

import com.uniovi.controllers.util.Utilities;
import com.uniovi.entities.Message;
import com.uniovi.entities.User;
import com.uniovi.services.ChatsService;
import com.uniovi.services.ItemsService;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/chat/")
public class ChatController {
	private final ItemsService itemsService;
	private final UsersService usersService;
	private final ChatsService chatsService;

	@Autowired
	public ChatController(ItemsService itemsService, UsersService usersService, ChatsService chatsService) {
		this.itemsService = itemsService;
		this.usersService = usersService;
		this.chatsService = chatsService;
	}

	@RequestMapping("conversation/{id}")
	public String indexStandard(Model model, @PathVariable Long id) {
		User sender = Utilities.getCurrentUser(usersService);
		User receiver = usersService.getUser(id);
		model.addAttribute("sender", sender);
		model.addAttribute("receiver", receiver);

		//format time
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
		model.addAttribute("formatter", formatter);

		model.addAttribute("messagesList", chatsService.getMessages(sender, receiver));
		return "chat/conversation";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.POST)
	public String postMessage(@RequestParam("message") String message, @PathVariable Long id, Principal principal) {
		User sender = Utilities.getCurrentUser(principal, usersService);
		User receiver = usersService.getUser(id);

		Message chatMessage = new Message();
		chatMessage.setMessage(message.replace("message=", ""));
		chatMessage.setTime(OffsetDateTime.now());
		chatMessage.setSender(sender);
		chatMessage.setReceiver(receiver);

		chatsService.addMessage(chatMessage);

		return "redirect:/chat/conversation/" + id;
	}
}

