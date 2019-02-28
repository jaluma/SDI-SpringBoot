package com.uniovi.controllers;

import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.ItemsService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.ItemValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class SellController {
	private final UsersService usersService;
	private ItemValidator itemValidator;
	private ItemsService itemsService;

	@Autowired
	public SellController(ItemValidator itemValidator, ItemsService itemsService, UsersService usersService) {
		this.itemValidator = itemValidator;
		this.itemsService = itemsService;
		this.usersService = usersService;
	}

	@RequestMapping(value = "/sell/add", method = RequestMethod.GET)
	public String getAddItem(Model model) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("now", format.format(new Date()));
		model.addAttribute("item", new Item());
		return "sell/add";
	}

	@RequestMapping(value = "/sell/add", method = RequestMethod.POST)
	public String setAddItem(@Validated Item item, BindingResult result, Principal principal, Model model) {
		itemValidator.validate(item, result);
		if(result.hasErrors()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			model.addAttribute("now", format.format(new Date()));
			model.addAttribute("item", item);
			return "sell/add";
		}

		String email = principal.getName();
		User user = usersService.getUserByEmail(email);
		item.setUser(user);

		itemsService.addItem(item);
		return "redirect:/home";
	}
}
