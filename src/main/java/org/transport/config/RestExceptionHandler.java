package org.transport.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.transport.response.DataResponse;

@RestControllerAdvice
public final class RestExceptionHandler {

	@ExceptionHandler
	public DataResponse handle(Exception e) {
		return DataResponse.createError(System.currentTimeMillis(), e);
	}
}
