package com.uniovi.controllers.util;

import com.uniovi.entities.User;
import com.uniovi.services.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class Utilities {

	public static User getCurrentUser(Principal principal, UsersService usersService) {
		String email = principal.getName();
		return usersService.getUserByEmail(email);
	}

	public static User getCurrentUser(UsersService usersService) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return usersService.getUserByEmail(email);
	}
}
