package com.uniovi.controllers;

import com.uniovi.entities.User;
import com.uniovi.services.RolesService;
import com.uniovi.services.SecurityService;
import com.uniovi.services.UsersService;
import com.uniovi.validators.SignUpFormValidator;
import com.uniovi.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UsersController {
	private static final double DEFUALT_MONEY = 100.0;

	private final UsersService usersService;
	private final SecurityService securityService;
	private final SignUpFormValidator signUpFormValidator;
	private final UserValidator userValidator;
	private final RolesService rolesService;

	@Autowired
	public UsersController(UsersService usersService, SecurityService securityService, SignUpFormValidator signUpFormValidator, UserValidator userValidator, RolesService rolesService) {
		this.usersService = usersService;
		this.securityService = securityService;
		this.signUpFormValidator = signUpFormValidator;
		this.userValidator = userValidator;
		this.rolesService = rolesService;
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
		user.setMoney(DEFUALT_MONEY);
		usersService.addUser(user);
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

}
