package com.uniovi.services;

import org.springframework.stereotype.Service;

@Service
public class RolesService {

	private static final String[] roles = {"ROLE_STANDARD", "ROLE_ADMIN"};

	public String[] getRoles() {
		return roles;
	}
}