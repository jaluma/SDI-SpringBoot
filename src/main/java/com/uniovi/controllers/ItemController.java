package com.uniovi.controllers;

import com.uniovi.controllers.util.Utilities;
import com.uniovi.entities.Association;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.ItemsService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.BuyItemValidator;
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

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ItemController {
	private final UsersService usersService;
	private final BuyItemValidator buyItemValidator;
	private final ItemsService itemsService;
	private final ItemValidator itemValidator;

	@Autowired
	public ItemController(BuyItemValidator buyItemValidator, ItemsService itemsService, UsersService usersService, ItemValidator buyedItemValidator) {
		this.buyItemValidator = buyItemValidator;
		this.itemsService = itemsService;
		this.usersService = usersService;
		this.itemValidator = buyedItemValidator;
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

		User user = Utilities.getCurrentUser(principal, usersService);
		// restamos 20€ si el item esta destacado
		if(item.isHighlighter()) {
			user.setMoney(user.getMoney() - 20);
		}

		Association.Sell.link(user, item);
		itemsService.addItem(item);

		return "redirect:/item/mylist";
	}

	@RequestMapping(value = "/item/buy/{id}")
	public String buy(@PathVariable Long id, Principal principal, HttpSession session) {
		Item item = itemsService.getItem(id);
		if(item == null) {
			throw new IllegalStateException("Illegal");
		}

		User buyerUser = Utilities.getCurrentUser(principal, usersService);

		if(item.getSellerUser().equals(buyerUser) || item.getBuyerUser() != null) {
			session.setAttribute("buy", "Error.buy.id");
			return "redirect:/item/list";
		}

		if(buyerUser.getMoney() < item.getPrice()) {
			session.setAttribute("buy", "Error.buy.insuficient");
			return "redirect:/item/list";
		}

		Association.Buy.link(buyerUser, item);

		buyerUser.setMoney(buyerUser.getMoney() - item.getPrice());
		itemsService.addItem(item);
		usersService.addUser(buyerUser);

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

		if(item == null) {
			throw new IllegalStateException("Illegal");
		}

		model.addAttribute("item", item);
		return "item/details";
	}

	@RequestMapping("/item/highlighter/{id}")
	public String highlighter(@PathVariable Long id, Model model, Principal principal) {
		Item item = itemsService.getItem(id);
		User user = Utilities.getCurrentUser(principal, usersService);

		if(item == null || user == null) {
			throw new IllegalStateException("Illegal");
		}

		if(!item.getSellerUser().equals(user)) {
			throw new IllegalStateException("Illegal");
		}

		if(user.getMoney() < 20) {
			throw new IllegalStateException("Illegal");
		}

		user.setMoney(user.getMoney() - 20);
		item.setHighlighter(true);
		itemsService.addItem(item);

		return "redirect:/item/mylist";
	}

	@RequestMapping("/item/list")
	public String getList(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) String searchText, Principal principal, HttpSession session) {
		User user = Utilities.getCurrentUser(principal, usersService);
		if(session.getAttribute("buy") != null) {
			model.addAttribute("buy", session.getAttribute("buy"));
			session.removeAttribute("buy");
		}

		Page<Item> items;
		if(searchText != null && !searchText.isEmpty()) {
			items = itemsService.searchItemsByTitleDescriptionAndUsernameBySellerUser(pageable, searchText, user);
			model.addAttribute("searchText", searchText);
		} else {
			items = itemsService.getItemsBySellerUser(pageable, user);
			model.addAttribute("searchText", "");
		}

		model.addAttribute("page", items);
		model.addAttribute("itemsList", items.getContent());
		return "item/list";
	}

	@RequestMapping("/item/mylist")
	public String getMyList(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) String searchText, Principal principal) {
		User user = Utilities.getCurrentUser(principal, usersService);

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


}
