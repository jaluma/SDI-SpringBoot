package com.uniovi.controllers.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class AdminLoggingRestController {

	private final ServletContext servletContext;
	@Value("${logging.file}")
	private File loggingFile;

	@Autowired
	public AdminLoggingRestController(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = "/admin/logging", method = RequestMethod.GET)
	public ResponseEntity<Resource> getImageAsByteArray(HttpServletResponse response) throws IOException {
		InputStreamResource resource = new InputStreamResource(new FileInputStream(loggingFile));

		return ResponseEntity.ok().contentLength(loggingFile.length()).contentType(MediaType.TEXT_PLAIN).body(resource);
	}
}
