package com.uniovi.controllers;

import com.uniovi.entities.Association;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.ItemsService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.ItemValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ItemController {
	private final UsersService usersService;
	private ItemValidator itemValidator;
	private ItemsService itemsService;

	@Autowired
	public ItemController(ItemValidator itemValidator, ItemsService itemsService, UsersService usersService) {
		this.itemValidator = itemValidator;
		this.itemsService = itemsService;
		this.usersService = usersService;
	}

	@RequestMapping(value = "/item/add", method = RequestMethod.GET)
	public String getAddItem(Model model) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		model.addAttribute("now", format.format(new Date()));
		model.addAttribute("item", new Item());
		return "item/add";
	}

	@RequestMapping(value = "/item/add", method = RequestMethod.POST)
	public String setAddItem(@Validated Item item, BindingResult result, Principal principal, Model model) {
		itemValidator.validate(item, result);
		if(result.hasErrors()) {
			return getAddItem(model);
		}

		User user = getCurrentUser(principal);

		Association.Sell.link(user, item);

		itemsService.addItem(item);

		return "redirect:/item/mylist";
	}

	@RequestMapping("/item/buy/{id}")
	public String buy(@PathVariable Long id, Principal principal) {
		User buyerUser = getCurrentUser(principal);
		Item item = itemsService.getItem(id);

		Association.Buy.link(buyerUser, item);
		return "redirect:/item/list";
	}

	@RequestMapping("/item/delete/{id}")
	public String delete(@PathVariable Long id) {
		itemsService.deleteItem(id);
		return "redirect:/item/mylist";
	}

	@RequestMapping("/item/details/{id}")
	public String details(@PathVariable Long id, Model model) {
		Item item = itemsService.getItem(id);

		model.addAttribute("item", item);
		return "item/details";
	}

	@RequestMapping("/item/list")
	public String getList(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) String searchText, Principal principal) {
		User user = getCurrentUser(principal);
		Page<Item> items;
		if(searchText != null && !searchText.isEmpty()) {
			items = itemsService.searchItemsByTitleDescriptionAndUsername(pageable, searchText, user);
			model.addAttribute("searchText", searchText);
		} else {
			items = itemsService.getItems(pageable, user);
			model.addAttribute("searchText", "");
		}

		model.addAttribute("page", items);
		model.addAttribute("itemsList", items.getContent());
		return "item/list";
	}

	@RequestMapping("/item/mylist")
	public String getMyList(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) String searchText, Principal principal) {
		User user = getCurrentUser(principal);

		Page<Item> items;
		if(searchText != null && !searchText.isEmpty()) {
			items = itemsService.searchItemsByTitleDescriptionAndUsernameByUser(pageable, searchText, user);
			model.addAttribute("searchText", searchText);
		} else {
			items = itemsService.getItemsByUser(pageable, user);
			model.addAttribute("searchText", "");
		}

		model.addAttribute("page", items);
		model.addAttribute("itemsList", items.getContent());
		return "item/mylist";
	}

	/*
		AUXILIARES
	 */

	private User getCurrentUser(Principal principal) {
		String email = principal.getName();
		return usersService.getUserByEmail(email);
	}
}