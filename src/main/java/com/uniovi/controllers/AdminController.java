package com.uniovi.controllers;

import com.uniovi.entities.User;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {
	private final UsersService usersService;

	@Autowired
	public AdminController(UsersService usersService) {
		this.usersService = usersService;
	}

	@RequestMapping(value = "/admin/list", method = RequestMethod.GET)
	public String getList(Model model, Pageable pageable, Principal principal, @RequestParam(required = false) String searchText) {
		Page<User> users; // no se inicializa, se hace abajo
		if(searchText != null && !searchText.isEmpty()) {
			users = usersService.searchUsers(pageable, searchText);
			model.addAttribute("searchText", searchText);
		} else {
			users = usersService.getUsers(pageable);
			model.addAttribute("searchText", "");
		}

		model.addAttribute("page", users);
		model.addAttribute("usersList", users.getContent());

		return "admin/list";
	}

	@RequestMapping(value = "/admin/remove", method = RequestMethod.POST)
	public String remove(@RequestParam("idChecked") List<String> removeList) {
		for(String id : removeList) {
			usersService.deleteUser(Long.parseLong(id));
		}

		return "redirect:/admin/list";
	}
}
