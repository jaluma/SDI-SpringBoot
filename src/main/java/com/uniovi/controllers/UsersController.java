package com.uniovi.controllers;

import com.uniovi.controllers.util.Utilities;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.ItemsService;
import com.uniovi.services.RolesService;
import com.uniovi.services.SecurityService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.SignUpFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
public class UsersController {
	private final UsersService usersService;
	private final SecurityService securityService;
	private final SignUpFormValidator signUpFormValidator;
	private final RolesService rolesService;
	private final ItemsService itemsService;

	private Logger logger = LoggerFactory.getLogger(UsersController.class);

	@Autowired
	public UsersController(UsersService usersService, SecurityService securityService, SignUpFormValidator signUpFormValidator, RolesService rolesService, ItemsService itemsService) {
		this.usersService = usersService;
		this.securityService = securityService;
		this.signUpFormValidator = signUpFormValidator;
		this.rolesService = rolesService;
		this.itemsService = itemsService;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(Model model) {
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(@Validated User user, BindingResult result, Model model) {
		signUpFormValidator.validate(user, result);
		if(result.hasErrors()) {
			return "signup";
		}

		user.setRole(rolesService.getRoles()[0]);
		usersService.addUser(user);
		logger.info(String.format("Signup user %s", user.getEmail()));

		securityService.autoLogin(user.getEmail(), user.getPasswordConfirm());
		return "redirect:home";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model) {
		return "login";
	}

	@RequestMapping(value = "/user/details/{id}", method = RequestMethod.GET)
	public String getDetailsByUser(Model model, @PathVariable Long id) {
		model.addAttribute("user", usersService.getUser(id));
		return "user/details";
	}

	@RequestMapping("/user/purchases")
	public String getList(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) String searchText, Principal principal) {
		User user = Utilities.getCurrentUser(principal, usersService);
		Page<Item> items;
		if(searchText != null && !searchText.isEmpty()) {
			items = itemsService.searchItemsByTitleDescriptionAndUsernameByBuyerUser(pageable, searchText, user);
			model.addAttribute("searchText", searchText);
		} else {
			items = itemsService.getItemsByBuyerUser(pageable, user);
			model.addAttribute("searchText", "");
		}

		model.addAttribute("currentUser", user);
		model.addAttribute("page", items);
		model.addAttribute("itemsList", items.getContent());
		return "user/purchases";
	}

}
