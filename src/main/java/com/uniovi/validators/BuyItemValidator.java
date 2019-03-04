package com.uniovi.validators;

import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BuyItemValidator implements Validator {
	private final UsersService usersService;

	@Autowired
	public BuyItemValidator(UsersService usersService) {
		this.usersService = usersService;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Item.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Item item = (Item) target;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User buyerUser = usersService.getUserByEmail(auth.getName());

		if(item.getSellerUser().equals(buyerUser) || item.getBuyerUser() != null || buyerUser.getMoney() < item.getPrice()) {
			errors.rejectValue("buyerUser", "Error.buy.insuficient");
		}
	}
}