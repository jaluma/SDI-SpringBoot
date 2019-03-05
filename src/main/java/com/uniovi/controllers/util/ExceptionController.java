package com.uniovi.controllers.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(IllegalStateException.class)
	public ModelAndView exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		ResponseStatus responseStatusAnnotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);

		return buildModelAndViewErrorPage(request, response, ex, responseStatusAnnotation != null ? responseStatusAnnotation.value() : HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping("*")
	public ModelAndView fallbackHandler(HttpServletRequest request, HttpServletResponse response) {
		return buildModelAndViewErrorPage(request, response, new IllegalStateException("Illegal"), HttpStatus.NOT_FOUND);
	}

	private ModelAndView buildModelAndViewErrorPage(HttpServletRequest request, HttpServletResponse response, Exception ex, HttpStatus httpStatus) {
		response.setStatus(httpStatus.value());

		ModelAndView mav = new ModelAndView("error.html");
		mav.addObject("content", request.getRequestURL());
		mav.addObject("error", ex.getMessage());
		mav.addObject("status", httpStatus.value());
		return mav;
	}
}
