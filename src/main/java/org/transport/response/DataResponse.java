package org.transport.response;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

public final class DataResponse {

	public final long currentTime = System.currentTimeMillis();
	public final int code;
	public final Object data;
	public final String text;

	private DataResponse(int code, Object data, String text) {
		this.code = code;
		this.data = data;
		this.text = text;
	}

	public static DataResponse create(Object data) {
		final HttpStatus status = HttpStatus.OK;
		return new DataResponse(status.value(), data, status.getReasonPhrase());
	}

	public static DataResponse createError(Exception e) {
		final HttpStatus status = HttpStatus.BAD_REQUEST;
		return new DataResponse(status.value(), new HashMap<>(), e.getMessage());
	}
}
