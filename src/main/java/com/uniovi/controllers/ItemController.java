package com.uniovi.controllers;

import com.uniovi.controllers.util.Utilities;
import com.uniovi.entities.Chat;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.ChatsService;
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
import java.util.List;

@Controller
public class ItemController {
	private final UsersService usersService;
	private final BuyItemValidator buyItemValidator;
	private final ItemsService itemsService;
	private final ItemValidator itemValidator;
	private final ChatsService chatsService;

	@Autowired
	public ItemController(BuyItemValidator buyItemValidator, ItemsService itemsService, UsersService usersService, ItemValidator buyedItemValidator, ChatsService chatsService) {
		this.buyItemValidator = buyItemValidator;
		this.itemsService = itemsService;
		this.usersService = usersService;
		this.itemValidator = buyedItemValidator;
		this.chatsService = chatsService;
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
		itemsService.addItem(item, user);

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

		itemsService.buy(buyerUser, item);

		return "redirect:/item/list";
	}

	@RequestMapping("/item/delete/{id}")
	public String delete(@PathVariable Long id) {
		Item item = itemsService.getItem(id);

		if(item == null) {
			throw new IllegalStateException("Illegal");
		}
		// conseguir los chats y borrarlos
		List<Chat> chats = chatsService.getChats(item);
		for(Chat chat : chats) {
			chatsService.deleteChat(chat);
		}

		itemsService.deleteItem(item);
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
	public String highlighter(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
		Item item = itemsService.getItem(id);
		User user = Utilities.getCurrentUser(principal, usersService);

		if(item == null || user == null) {
			throw new IllegalStateException("Illegal");
		}

		if(!item.getSellerUser().equals(user)) {
			throw new IllegalStateException("Illegal");
		}

		if(user.getMoney() < 20) {
			session.setAttribute("highlighter", "Error.buy.insuficient");
			return "redirect:/item/mylist";
		}

		itemsService.highlighter(item, user);
		itemsService.add(item);

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
	public String getMyList(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) String searchText, Principal principal, HttpSession session) {
		User user = Utilities.getCurrentUser(principal, usersService);
		if(session.getAttribute("highlighter") != null) {
			model.addAttribute("highlighter", session.getAttribute("highlighter"));
			session.removeAttribute("highlighter");
		}

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
