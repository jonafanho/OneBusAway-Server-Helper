package org.transport.response;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler
	public DataResponse handle(Exception e) {
		return DataResponse.createError(e);
	}
}
