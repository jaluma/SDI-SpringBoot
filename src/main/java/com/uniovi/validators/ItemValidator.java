package com.uniovi.validators;

import com.uniovi.entities.Item;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class ItemValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return Item.class.equals(aClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Item item = (Item) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "Error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "Error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateFormat", "Error.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "Error.empty");


		if(diffDays(item.getDate(), new Date()) < 0) {
			errors.rejectValue("dateFormat", "Error.item.add.date");
		}
		if(item.getPrice() < 0) {
			errors.rejectValue("price", "Error.item.add.price");
		}
	}

	private static int diffDays(Date until, Date from) {
		long diff = until.getTime() - from.getTime();
		return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
}