package com.uniovi.controllers;

import com.uniovi.entities.User;
import com.uniovi.services.ItemsService;
import com.uniovi.services.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {
	private final UsersService usersService;
	private final ItemsService itemsService;
	private Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	public AdminController(UsersService usersService, ItemsService itemsService) {
		this.usersService = usersService;
		this.itemsService = itemsService;
	}

	@RequestMapping(value = "/admin/list", method = RequestMethod.GET)
	public String getList(Model model, Pageable pageable, @RequestParam(required = false) String searchText) {
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
	public String remove(@RequestParam(value = "idChecked", required = false) List<String> removeList, Pageable pageable) {
		if(removeList != null) {
			for(String id : removeList) {
				Long idL = Long.parseLong(id);
				User user = usersService.getUser(idL);

				itemsService.buyUnlink(pageable, user);
				usersService.deleteUser(user);

				logger.info(String.format("Delete user: %d", idL));
			}
		}
		return "redirect:/admin/list";
	}
}
