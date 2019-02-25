package com.uniovi.validators;

import com.uniovi.entities.User;
import com.uniovi.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
	private final UsersService usersService;

	@Autowired
	public UserValidator(UsersService usersService) {
		this.usersService = usersService;
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		User user = (User) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "Error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Error.empty");

		// editamos el user
		if(user.getUsername() != null) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Error.empty");
			if(usersService.getUserByDni(user.getUsername()) != null) {
				errors.rejectValue("username", "Error.signup.username.duplicate");
			}
		}

	}
}