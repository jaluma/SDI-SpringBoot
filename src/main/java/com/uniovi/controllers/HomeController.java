package com.uniovi.controllers;

import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.ItemsService;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {
	private final ItemsService itemsService;
	private final UsersService usersService;

	@Autowired
	public HomeController(ItemsService itemsService, UsersService usersService) {
		this.itemsService = itemsService;
		this.usersService = usersService;
	}

	@RequestMapping("/")
	public String indexStandard() {
		return "index";
	}

	@RequestMapping("/loginSuccess")
	public String loginSuccess(Model model, Pageable pageable) {
		return "redirect:" + home(model, pageable);
	}

	@RequestMapping("/home")
	public String home(Model model, Pageable pageable) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		User user = usersService.getUserByEmail(email);

		Page<Item> items = itemsService.getItemsByUser(pageable, user);
		List<Item> highlighterItems = itemsService.getHighlighterItems(user);

		model.addAttribute("page", items);
		model.addAttribute("itemsList", items.getContent());
		model.addAttribute("highlighterItems", highlighterItems);

		return "home";
	}

}
