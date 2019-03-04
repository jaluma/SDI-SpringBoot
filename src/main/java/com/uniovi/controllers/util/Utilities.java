package com.uniovi.controllers.util;

import com.uniovi.entities.User;
import com.uniovi.services.UsersService;

import java.security.Principal;

public class Utilities {

	public static User getCurrentUser(Principal principal, UsersService usersService) {
		String email = principal.getName();
		return usersService.getUserByEmail(email);
	}
}
